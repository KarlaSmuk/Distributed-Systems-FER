package hr.fer.tel.rassus.client;

import hr.fer.tel.rassus.client.model.Reading;

import java.io.*;

public class ReadingFinder {

    public Reading findReading(int activeSeconds) {

        int row = (activeSeconds % 100) + 1;

        try (BufferedReader br = new BufferedReader(new FileReader("readings.csv"))) {
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
                            values[4] == null ? Double.parseDouble(values[5]) : Double.parseDouble(values[4])
                    );
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
