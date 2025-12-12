/*
 * This code has been developed at Departement of Telecommunications,
 * Faculty of Electrical Eengineering and Computing, University of Zagreb.
 */
package hr.fer.tel.rassus.stupidudp.client;

import hr.fer.tel.rassus.stupidudp.kafka.KafkaSensor;
import hr.fer.tel.rassus.stupidudp.model.Reading;
import hr.fer.tel.rassus.stupidudp.model.Sensor;
import hr.fer.tel.rassus.stupidudp.network.*;

import java.awt.event.KeyAdapter;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Krešimir Pripužić <kresimir.pripuzic@fer.hr>
 */
public class StupidUDPClient {

    public static void run() throws IOException, InterruptedException {
        System.out.println();
        System.out.println("Starting up UDP Client");
        System.out.println();

        // encode this String into a sequence of bytes using the platform's
        // default charset and store it into a new byte array

        // simulate package loss
        // 0.3 -> 30% lost packages
        // 1000ms delay of lost package
        DatagramSocket socket = new SimpleSimulatedDatagramSocket(0.3, 1000); //SOCKET

        // determine the IP address of a host, given the host's name
        InetAddress address = InetAddress.getByName("localhost");

        while (KafkaSensor.stop == false) {

            Thread.sleep(3000); // 3 seconds

            Long currentTime = KafkaSensor.emulatedSystemClock.currentTimeMillis();
            Reading reading = ReadingFinder.findReading((currentTime - KafkaSensor.sensorStartTime) / 1000); // 1000 bc of ms to s

            KafkaSensor.sensor.increaseVector();
            KafkaSensor.sensor.setScalar(currentTime); // set new scalar time when sending a reading

            reading.setSensorId(KafkaSensor.sensor.getId());
            reading.setScalar(currentTime);
            reading.setVector(KafkaSensor.sensor.getVector());

            KafkaSensor.myReadings.add(reading);

            System.out.println();
            System.out.println("Find reading, increasing vector for sensor");
            System.out.println("UDP Client sends: " + reading);
            System.out.println();
            byte[] sendBuf = reading.toBytes();
            byte[] rcvBuf = new byte[256];

            // create a datagram packet for receiving data
            DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);

            for (Sensor neighbor : KafkaSensor.sensor.getNeighbors()) {

                // create a datagram packet for sending data
                DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, address, neighbor.getPort());

                while (true) { // loop for sending again if packet is lost

                    try {
                        // send a datagram packet from this socket
                        socket.send(packet);


                        // receive a datagram packet from this socket (ACK)
                        socket.receive(rcvPacket); //RECVFROM
                        String receiveString = new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength());
                        System.out.println("Received ACK message: " + receiveString);
                        break; // if packet is received exit loop
                    } catch (SocketTimeoutException e) {
                        System.out.println("Lost packet, sending again");
                    } catch (IOException ex) {
                        Logger.getLogger(StupidUDPClient.class.getName()).log(Level.ALL, "Exception", ex);
                    }
                }
            }
            System.out.println();
        }
        // close the datagram socket
        socket.close(); //CLOSE
    }
}
