package hr.fer.tel.rassus.stupidudp.client;


import hr.fer.tel.rassus.stupidudp.model.Reading;

import java.io.*;

public class ReadingFinder {

    public static Reading findReading(Long activeSeconds) throws FileNotFoundException {

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

                    Reading r = new Reading();

                    r.setNo2(values[4].isEmpty() ? 0.0 : Double.parseDouble(values[4]));

                    return r;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
