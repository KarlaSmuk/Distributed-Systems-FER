package hr.fer.tel.rassus.stupidudp.kafka;

import hr.fer.tel.rassus.stupidudp.client.StupidUDPClient;
import hr.fer.tel.rassus.stupidudp.model.Reading;
import hr.fer.tel.rassus.stupidudp.model.Sensor;
import hr.fer.tel.rassus.stupidudp.network.EmulatedSystemClock;
import hr.fer.tel.rassus.stupidudp.server.StupidUDPServer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

// Producer -> sends register message on topic "Register"
// Consumer -> receiving messages from topic "Command" and "Register"
public class KafkaSensor {

    public static final String TOPIC_REGISTER = "Register";

    public static final String TOPIC_COMMAND = "Command";

    public static Boolean start = false;

    public static Boolean stop = false;

    public static EmulatedSystemClock emulatedSystemClock = null;

    public static int PORT;

    public static Long sensorStartTime = 0L;

    public static Sensor sensor = null;

    public static List<Reading> myReadings = new ArrayList<>();

    public static List<Reading> receivedReadings = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("Starting Kafka Sensor with id: " + args[0]);

        // 1. PRODUCER
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        Producer<String, String> producer = new KafkaProducer<>(producerProperties);

        emulatedSystemClock = new EmulatedSystemClock();
        sensorStartTime = emulatedSystemClock.currentTimeMillis();
        sensor = generateSensor(Long.parseLong(args[0]), sensorStartTime);

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_REGISTER, null, new JSONObject(sensor).toString());

        producer.send(record);
        producer.flush(); // ensure all previously sent messages have actually completed

        // 2. CONSUMER
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, String> consumerCommand = new KafkaConsumer<>(consumerProperties);

        consumerCommand.subscribe(Collections.singleton(TOPIC_COMMAND));

        Consumer<String, String> consumerRegister = new KafkaConsumer<>(consumerProperties);
        consumerRegister.subscribe(Collections.singleton(TOPIC_REGISTER));


        // needs 2 threads
        // one to loop until start
        // other to loop until stop

        Thread untilStart = new Thread(() -> {
            while (!start) {
                System.out.println("Waiting for START command");
                ConsumerRecords<String, String> consumerRecords = consumerRegister.poll(Duration.ofMillis(1000));

                consumerRecords.forEach(r -> {
                    Sensor neighbour = new ObjectMapper().readValue(r.value(), Sensor.class);
                    if (!Objects.equals(neighbour.getId(), sensor.getId())) {
                        System.out.println();
                        System.out.println("Received neighbour " + neighbour);
                        sensor.addNeighbor(neighbour);
                        System.out.println("Current neighbours: " + sensor.getNeighbors());
                        System.out.println();
                    }
                });

                consumerRegister.commitAsync();
            }

            // after start is sent -> start == true
            Thread runStupidUDPClient = new Thread(() -> {
                try {
                    StupidUDPClient.run();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });

            Thread runStupidUDPServer = new Thread(() -> {
                try {
                    StupidUDPServer.run();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });

            Thread sortEvery5Seconds = new Thread(() -> {
                while (!stop && start) {

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        return; // exit thread
                    }

                    processLast5Seconds();
                }
            });


            sortEvery5Seconds.start();
            runStupidUDPClient.start();
            runStupidUDPServer.start();
        });

        Thread untilStop = new Thread(() -> {
            while (!stop) {
                ConsumerRecords<String, String> consumerRecords = consumerCommand.poll(Duration.ofMillis(1000));

                consumerRecords.forEach(r -> {
                    System.out.println();
                    System.out.println("Received: " + r.value());
                    System.out.println();

                    if (r.value().equals("Stop"))
                        stop = true;
                    else if (r.value().equals("Start"))
                        start = true;
                });

                consumerCommand.commitAsync();
            }
            System.exit(0);
        });

        untilStart.start();
        untilStop.start();
    }

    private static void processLast5Seconds() {
        System.out.println();
        System.out.println("Run SORT every 5 seconds");

        // Filtriraj očitanja u zadnjih 5 sekundi
        List<Reading> readings = new ArrayList<>();
        readings.addAll(myReadings);
        readings.addAll(receivedReadings);

        if (readings.isEmpty()) {
            System.out.println("Readings are empty");
            return;
        }

        readings.removeIf(r -> System.currentTimeMillis() - r.getScalar() > 5000);

        // sort by scalar
        List<Reading> scalarSorted = new ArrayList<>(readings);
        scalarSorted.sort(Comparator.comparingLong(Reading::getScalar));

        System.out.println("Sorted by scalar time");
        scalarSorted.forEach(System.out::println);

        // sort by vector
        List<Reading> vectorSorted = new ArrayList<>(readings);
        vectorSorted.sort((a, b) -> compareVector(a.getVector(), b.getVector()));

        System.out.println("Sorted by vector time");
        vectorSorted.forEach(System.out::println);

        // average value
        double avg = readings.stream()
                .mapToDouble(Reading::getNo2)
                .average()
                .orElse(0);

        System.out.println("Average value of readings NO2: " + avg);
        System.out.println();
    }

    private static int compareVector(Integer t1, Integer t2) {
        return t1.compareTo(t2);
    }

    private static Sensor generateSensor(Long id, Long scalar) {
        PORT = 3000 + Integer.parseInt(id.toString());
        return new Sensor(id, "localhost", PORT, scalar);
    }
}
