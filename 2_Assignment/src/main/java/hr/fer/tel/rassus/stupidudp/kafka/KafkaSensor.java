package hr.fer.tel.rassus.stupidudp.kafka;

import hr.fer.tel.rassus.stupidudp.model.Reading;
import hr.fer.tel.rassus.stupidudp.model.Sensor;
import hr.fer.tel.rassus.stupidudp.network.EmulatedSystemClock;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// Producer -> sends register message on topic "Register"
// Consumer -> receiving messages from topic "Command" and "Register"
public class KafkaSensor {

    public static final String TOPIC_REGISTER = "Register";

    public static final String TOPIC_COMMAND = "Command";

    public static Boolean start = false;

    public static Boolean stop = false;

    public static final EmulatedSystemClock emulatedSystemClock = new EmulatedSystemClock();

    public static int PORT;

    public static Long sensorStartTime = 0L;

    public static Sensor sensor;

    public static List<Reading> myReadings;

    public static List<Reading> receivedReadings;

    public static void main(String[] args) throws IOException {

        // 1. PRODUCER
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(producerProperties);

        sensor = new Sensor(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE), "localhost", new ServerSocket(0).getLocalPort());
        sensorStartTime = emulatedSystemClock.currentTimeMillis();

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
        Consumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(consumerProperties);

        consumer.subscribe(Collections.singleton(TOPIC_COMMAND));

        System.out.println("Waiting for messaged to arrive on topic " + TOPIC_COMMAND);

        Consumer<String, String> consumerRegister = new org.apache.kafka.clients.consumer.KafkaConsumer<>(consumerProperties);
        consumerRegister.subscribe(Collections.singleton(TOPIC_REGISTER));

        System.out.println("Waiting for messaged to arrive on topic " + TOPIC_REGISTER);

        // needs 2 threads
        // one to loop until start
        // other to loop until stop

        Thread untilStart = new Thread(() -> {
            while (!start) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));

                consumerRecords.forEach(r -> {
                    System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", r.key(), r.value(), r.partition(), r.offset());
                    Sensor neighbour =  new ObjectMapper().readValue(r.value(), Sensor.class);
                    if (!Objects.equals(neighbour.getId(), sensor.getId()))
                    {
                        sensor.addNeighbor(neighbour);
                        System.out.println("New neighbour: " + neighbour);
                        System.out.println("Current neighbours: " + sensor.getNeighbors());
                    }
                });

                consumer.commitAsync();
            }

        });

        Thread untilStop = new Thread(() -> {
            while (!stop) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));

                consumerRecords.forEach(r -> {
                    System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", r.key(), r.value(), r.partition(), r.offset());
                    String message = "Received: " + r.value();
                    System.out.println(message);

                    if(r.value().equals("Stop"))
                        stop = true;
                    else if(r.value().equals("Start"))
                        start = true;
                });

                consumer.commitAsync();
            }
            System.exit(0);
        });

        untilStart.start();
        untilStop.start();
    }
}
