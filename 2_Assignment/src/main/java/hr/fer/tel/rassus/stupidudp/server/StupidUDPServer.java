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
            for(Reading r : KafkaSensor.receivedReadings){
                if (r.getSensorId().equals(reading.getSensorId()) &&
                        r.getNo2().equals(reading.getNo2()) &&
                        r.getVector().equals(reading.getVector()) &&
                        r.getScalar().equals(reading.getScalar()))
                {
                    repeated = true;
                    break;
                }
            }

            String message;
            if(!repeated){
                message = "New reading received: " + reading;

                KafkaSensor.receivedReadings.add(reading);
                KafkaSensor.sensor.increaseVector();

                System.out.println("Vector time increased: " + KafkaSensor.sensor);

                for(Sensor neighbour: KafkaSensor.sensor.getNeighbors()){
                    if(Objects.equals(reading.getSensorId(), neighbour.getId())){
                        neighbour.setVector(reading.getVector());
                    }
                }
            } else {
                message = "Repeated reading received: " + reading;
            }

            // encode a String into a sequence of bytes using the platform's
            // default charset
            sendBuf = message.getBytes();
            System.out.println("Server sends: " + message);
            System.out.println();

            // create a DatagramPacket for sending packets
            DatagramPacket sendPacket = new DatagramPacket(sendBuf,
                    sendBuf.length, packet.getAddress(), packet.getPort());

            // send packet
            socket.send(sendPacket); //SENDTO
        }
    }
}
