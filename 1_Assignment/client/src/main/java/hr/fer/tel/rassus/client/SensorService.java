package hr.fer.tel.rassus.client;

import hr.fer.tel.rassus.client.model.Reading;
import io.grpc.stub.StreamObserver;

public class SensorService extends SensorGrpc.SensorImplBase {

    private final SensorRPCClient sensorRPCClient;

    public SensorService(SensorRPCClient sensorRPCClient) {
        this.sensorRPCClient = sensorRPCClient;
    }

    @Override
    public void requestNeighbourReading(SensorDataRequest request, StreamObserver<SensorDataResponse> responseObserver) {
        Reading reading = sensorRPCClient.getCurrentReading();

        SensorDataResponse response = SensorDataResponse.newBuilder()
                .setTemperature(reading.getTemperature())
                .setPressure(reading.getPressure())
                .setHumidity(reading.getHumidity())
                .setCo(reading.getCo())
                .setNo2(reading.getNo2())
                .setSo2(reading.getSo2())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
