package hr.fer.tel.rassus.client;

import hr.fer.tel.rassus.client.model.Reading;

import java.io.*;
import java.util.Objects;

public class ReadingFinder {

    public static Reading findReading(long activeSeconds) throws FileNotFoundException {

        int row = Math.toIntExact((activeSeconds % 100) + 1);

        InputStream readings = ReadingFinder.class.getClassLoader().getResourceAsStream("readings.csv");
        if (readings == null) {
            throw new FileNotFoundException("File readings.csv not found");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(readings))) {
            String line;
            int currentRow = 0;

            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                currentRow++;

                if (currentRow == row) {
                    String[] values = line.split(",");

                    return new Reading(
                            Double.parseDouble(values[0]),
                            Double.parseDouble(values[1]),
                            Double.parseDouble(values[2]),
                            Double.parseDouble(values[3]),
                            Objects.equals(values[4], "") ? Double.parseDouble(values[5]) : Double.parseDouble(values[4])
                    );
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
