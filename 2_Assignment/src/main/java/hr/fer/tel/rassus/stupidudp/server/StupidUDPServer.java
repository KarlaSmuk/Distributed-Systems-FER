/*
 * This code has been developed at Departement of Telecommunications,
 * Faculty of Electrical Engineering and Computing, University of Zagreb.
 */
package hr.fer.tel.rassus.stupidudp.server;

import hr.fer.tel.rassus.stupidudp.kafka.KafkaSensor;
import hr.fer.tel.rassus.stupidudp.model.Reading;
import hr.fer.tel.rassus.stupidudp.model.Sensor;
import hr.fer.tel.rassus.stupidudp.network.SimpleSimulatedDatagramSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Objects;

/**
 *
 * @author Krešimir Pripužić <kresimir.pripuzic@fer.hr>
 */
public class StupidUDPServer {

    public static void run() throws IOException {
        System.out.println();
        System.out.println("Starting up UDP Server");
        System.out.println();

        byte[] rcvBuf = new byte[256]; // received bytes
        byte[] sendBuf;// sent bytes

        // create a UDP socket and bind it to the specified port on the local
        // host
        DatagramSocket socket = new SimpleSimulatedDatagramSocket(KafkaSensor.PORT, 0.3, 1000); //SOCKET -> BIND

        while (KafkaSensor.stop == false) { //OBRADA ZAHTJEVA
            // create a DatagramPacket for receiving packets
            DatagramPacket packet = new DatagramPacket(rcvBuf, rcvBuf.length);

            // receive packet
            socket.receive(packet); //RECVFROM

            // construct a new String by decoding the specified subarray of
            // bytes
            // using the platform's default charset
            Reading reading = Reading.fromBytes(packet.getData());
            System.out.println();
            System.out.println("UDP Server received: " + reading);

            boolean repeated = false;
            for (Reading r : KafkaSensor.receivedReadings) {
                if (r.getSensorId().equals(reading.getSensorId()) &&
                        r.getNo2().equals(reading.getNo2()) &&
                        r.getVector().equals(reading.getVector()) &&
                        r.getScalar().equals(reading.getScalar())) {
                    repeated = true;
                    break;
                }
            }

            String message;
            if (!repeated) {
                message = "New reading received: " + reading;

                KafkaSensor.receivedReadings.add(reading);
                KafkaSensor.sensor.increaseVector();
                // when new reading is received update scalar
                // if scalar for reading is greater than currentTime on sensor, set one from reading
                if (reading.getScalar() > KafkaSensor.emulatedSystemClock.currentTimeMillis()) {
                    KafkaSensor.sensor.setScalar(reading.getScalar());
                // if not set current sensor time
                } else {
                    KafkaSensor.sensor.setScalar(KafkaSensor.emulatedSystemClock.currentTimeMillis());
                }

                System.out.println("New reading received, increasing vector for sensor");

                for (Sensor neighbour : KafkaSensor.sensor.getNeighbors()) {
                    if (Objects.equals(reading.getSensorId(), neighbour.getId())) {
                        // to update neighbour set vector bc new received vector is always max
                        neighbour.setVector(reading.getVector());
                    }
                }
            } else {
                message = "Repeated reading received: " + reading;
            }

            // encode a String into a sequence of bytes using the platform's
            // default charset
            sendBuf = message.getBytes();
            System.out.println("Server sends ACK message: " + message);
            System.out.println();

            // create a DatagramPacket for sending packets
            DatagramPacket sendPacket = new DatagramPacket(sendBuf,
                    sendBuf.length, packet.getAddress(), packet.getPort());

            // send packet
            socket.send(sendPacket); //SENDTO
        }
    }
}
