/*
 * This code has been developed at Departement of Telecommunications,
 * Faculty of Electrical Eengineering and Computing, University of Zagreb.
 */
package hr.fer.tel.rassus.stupidudp.client;

import hr.fer.tel.rassus.stupidudp.kafka.KafkaSensor;
import hr.fer.tel.rassus.stupidudp.model.Reading;
import hr.fer.tel.rassus.stupidudp.model.Sensor;
import hr.fer.tel.rassus.stupidudp.network.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Krešimir Pripužić <kresimir.pripuzic@fer.hr>
 */
public class StupidUDPClient {

    public static void main(String[] args) throws IOException {

        // encode this String into a sequence of bytes using the platform's
        // default charset and store it into a new byte array

        // simulate package loss
        // 0.3 -> 30% lost packages
        // 1000ms delay of lost package
        DatagramSocket socket = new SimpleSimulatedDatagramSocket(0.3, 1000); //SOCKET

        while (KafkaSensor.stop == false) {

            Long currentTime = KafkaSensor.emulatedSystemClock.currentTimeMillis();
            int row = Math.toIntExact(((currentTime - KafkaSensor.sensorStartTime) % 100) + 1);
            Reading reading = readCsvRow(row);

            KafkaSensor.sensor.increaseVector();

            reading.setSensorId(KafkaSensor.sensor.getId());
            reading.setScalarTime(currentTime);
            reading.setVectorTime(KafkaSensor.sensor.getVector());

            KafkaSensor.myReadings.add(reading);

            // determine the IP address of a host, given the host's name
            InetAddress address = InetAddress.getByName("localhost");

            System.out.println("Client sends: " + reading);

            byte[] sendBuf = reading.toBytes();
            byte[] rcvBuf = new byte[256];

            // create a datagram packet for receiving data
            DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);

            for(Sensor neighbor : KafkaSensor.sensor.getNeighbors()) {

                // create a datagram packet for sending data
                DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, address, neighbor.getPort());

                while(true) { // loop for sending again if packet is lost

                    // send a datagram packet from this socket
                    socket.send(packet);

                    try {
                        // receive a datagram packet from this socket
                        socket.receive(rcvPacket); //RECVFROM
                        String receiveString = new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength());
                        System.out.println("Received: " + receiveString);
                        break; // if packet is received exit loop
                    } catch (SocketTimeoutException e) {
                       System.out.println("Lost packet, sending again");
                    } catch (IOException ex) {
                        Logger.getLogger(StupidUDPClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        // close the datagram socket
        socket.close(); //CLOSE
    }

    public static Reading readCsvRow(int row) throws IOException {

        try (Reader reader = Files.newBufferedReader(Paths.get("readings.csv"))) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);

            int index = 0;
            for (CSVRecord record : records) {
                if (index == row) {
                    Reading r = new Reading();

                    r.setNo2(Double.valueOf(record.get("no2")));

                    return r;
                }
                index++;
            }
        }

        return null; // row does not exist
    }
}
