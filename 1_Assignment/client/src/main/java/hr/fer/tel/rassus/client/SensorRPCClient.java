package hr.fer.tel.rassus.client;

import hr.fer.tel.rassus.client.model.Reading;
import hr.fer.tel.rassus.client.retrofit.RetrofitService;
import hr.fer.tel.rassus.client.retrofit.request.CreateReadingRequest;
import hr.fer.tel.rassus.client.retrofit.request.RegisterSensorRequest;
import hr.fer.tel.rassus.client.retrofit.response.ReadingResponse;
import hr.fer.tel.rassus.client.retrofit.response.SensorResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Data;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

@Data
public class SensorRPCClient {

    private static final Logger logger = Logger.getLogger(SensorRPCClient.class.getName());


    private ManagedChannel channel;
    private SensorGrpc.SensorBlockingStub neighborSensorStub;

    private long startTime;

    private long id;
    private double latitude;
    private double longitude;
    private String ip;
    private int port;

    private Reading currentReading;
    private Reading nearestNeighbourReading;
    private SensorResponse nearestNeighbour;

    private SensorRPCServer sensorServer;
    private RetrofitService retrofitService = new RetrofitService();

    private static volatile boolean running = true;


    public SensorRPCClient(String ip) {
        this.ip = ip;
        this.startTime = System.currentTimeMillis();

        Random rand = new Random();
        this.latitude = 45.75 + (45.85 - 45.75) * rand.nextDouble();
        this.longitude = 15.87 + (16.00 - 15.87) * rand.nextDouble();
    }

    private void findReadingFromCSV() throws FileNotFoundException {
        long activeSeconds = (System.currentTimeMillis() - startTime) / 1000;

        logger.info("SENSOR " + getId() + " has been active for " + activeSeconds + " seconds.");

        Reading reading = ReadingFinder.findReading(activeSeconds);

        if (reading != null) {
            this.currentReading = new Reading(reading.getTemperature(), reading.getPressure(), reading.getHumidity(), reading.getCo(), reading.getNo2(), reading.getSo2());
            logger.info("SENSOR " + getId() + ": Current reading: " + currentReading);
        }
    }

    private void registerSensor() {
        RegisterSensorRequest request = new RegisterSensorRequest(this.getLatitude(), this.getLongitude(), this.ip, this.port);

        this.retrofitService.getSensorApi().registerSensor(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SensorResponse> call, Response<SensorResponse> response) {
                if (response.isSuccessful()) {
                    logger.info("SENSOR " + response.body().getId() + ": Successfully registered sensor: " + response.body().getId());
                    setId(response.body().getId());
                    try {
                        findReadingFromCSV();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        logger.warning("SENSOR " + getId() + ": Failed to register sensor (" + response.code() + "): " + errorMsg);
                    } catch (IOException e) {
                        logger.warning("SENSOR " + getId() + ": Error reading errorBody: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<SensorResponse> call, Throwable throwable) {
                logger.warning("Failed to register sensor: " + throwable.getMessage());
            }
        });
    }

    private void createReading(Reading reading) {
        CreateReadingRequest request = new CreateReadingRequest(reading.getTemperature(), reading.getPressure(), reading.getHumidity(), reading.getCo(), reading.getNo2(), reading.getSo2());

        this.retrofitService.getReadingApi().createReading(String.valueOf(this.id), request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ReadingResponse> call, Response<ReadingResponse> response) {
                if (response.isSuccessful()) {
                    logger.info("SENSOR " + getId() + ": Successfully created sensor reading: " + response.body());
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        logger.warning("SENSOR " + getId() + ": Failed to create sensor reading (" + response.code() + "): " + errorMsg);
                    } catch (IOException e) {
                        logger.warning("SENSOR " + getId() + ": Error reading errorBody: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ReadingResponse> call, Throwable throwable) {
                logger.warning("SENSOR " + getId() + ": Failed to create sensor reading: " + throwable.getMessage());
            }
        });
    }

    private void findNearestNeighbor() {
        this.retrofitService.getSensorApi().getNearestNeighbour(String.valueOf(this.id)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SensorResponse> call, Response<SensorResponse> response) {
                if (response.isSuccessful()) {
                    logger.info("SENSOR " + getId() + ": Successfully found nearest neighbour sensor: " + response.body());
                    setNearestNeighbour(response.body());
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        logger.warning("SENSOR " + getId() + ": Failed to find nearest neighbour sensor (" + response.code() + "): " + errorMsg);
                    } catch (IOException e) {
                        logger.warning("SENSOR " + getId() + ": Error reading errorBody: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<SensorResponse> call, Throwable throwable) {
                logger.warning("SENSOR " + getId() + ": Failed to found nearest neighbour sensor: " + throwable.getMessage());
            }
        });
    }

    private void requestNeighbourReading() {
        SensorDataRequest request = SensorDataRequest.newBuilder().build();

        try {
            SensorDataResponse response = neighborSensorStub.requestNeighbourReading(request);
            this.setNearestNeighbourReading(new Reading(response.getTemperature(), response.getPressure(), response.getHumidity(), response.getCo(), response.getNo2(), response.getSo2()));

            logger.info("SENSOR " + getId() + ": Fetched nearest neighbour sensor reading: " + response);
        } catch (Exception e) {
            logger.warning("SENSOR " + getId() + ": Failed to request nearest neighbour sensor reading: " + e.getMessage());
            this.setNearestNeighbour(null);
            this.setNearestNeighbourReading(null);
        }
    }

    private Reading calibrateReading(Reading myReading, Reading neighborReading) {
        double temperature = averageValue(myReading.getTemperature(), neighborReading.getTemperature());
        double pressure = averageValue(myReading.getPressure(), neighborReading.getPressure());
        double humidity = averageValue(myReading.getHumidity(), neighborReading.getHumidity());
        double co = averageValue(myReading.getCo(), neighborReading.getCo());
        double no2 = averageValue(myReading.getNo2(), neighborReading.getNo2());
        double so2 = averageValue(myReading.getSo2(), neighborReading.getSo2());

        return new Reading(temperature, pressure, humidity, co, no2, so2);
    }

    private double averageValue(double v1, double v2) {
        if (v1 > 0 && v2 > 0) {
            return (v1 + v2) / 2;
        } else if (v1 > 0) {
            return v1;
        } else if (v2 > 0) {
            return v2;
        } else {
            return 0;
        }
    }

    public void initializeNeighborSensorConnection(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.neighborSensorStub = SensorGrpc.newBlockingStub(channel);
    }

    public void start() throws IOException, InterruptedException {

        // start the gRPC server to receive requests from other sensors
        this.sensorServer = new SensorRPCServer(this);
        Thread serverThread = new Thread(() -> {
            try {
                this.sensorServer.start();
                this.port = this.sensorServer.getPort();
                logger.info("SENSOR " + getId() + ": gRPC server started on port " + this.port);
            } catch (IOException e) {
                logger.warning("Failed to start SensorServer: " + e.getMessage());
            }
        });
        serverThread.start();
        serverThread.join(); // wait until the server has started

        // register the sensor (REST server)
        this.registerSensor();

        sleep(5000);

        // find the nearest neighbor sensor (REST server)
        this.findNearestNeighbor();

        sleep(5000);

        // initialize gRPC connection to the neighbor sensor if neighbour is found
        if (this.nearestNeighbour != null) {
            initializeNeighborSensorConnection(nearestNeighbour.getIp(), nearestNeighbour.getPort());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.warning("Stopping sensor...");
            running = false;
        }));

        while (running) {
            try {
                // find reading
                this.findReadingFromCSV();

                sleep(5000);

                if (this.nearestNeighbour != null) {
                    // request reading from the neighbor sensor (gRPC)
                    this.requestNeighbourReading();

                    sleep(1000);

                    // calibrate own readings using neighbors reading
                    this.currentReading = calibrateReading(this.currentReading, this.nearestNeighbourReading);
                }

                // send reading to the REST server
                this.createReading(this.currentReading);


                sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.stop();
                this.sensorServer.stop();
                logger.warning("SENSOR " + getId() + ": stopped: ");
            }
        }
    }

    public void stop() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        SensorRPCClient client = new SensorRPCClient("127.0.0.1");
        client.start();
    }
}
