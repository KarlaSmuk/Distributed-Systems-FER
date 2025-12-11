package hr.fer.tel.rassus.stupidudp.model;

import lombok.*;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Reading {

    private Long sensorId;

    private Double no2;

    private Long scalar;

    private Integer vector;

    public byte[] toBytes() {
        JSONObject obj = new JSONObject();
        obj.put("sensorId", sensorId);
        obj.put("no2", no2);
        obj.put("scalar", scalar);
        obj.put("vector", vector);
        return (obj.toString()).getBytes(StandardCharsets.UTF_8);
    }

    public static Reading fromBytes(byte[] bytes) {
        String jsonString = new String(bytes, StandardCharsets.UTF_8).trim();
        JSONObject obj = new JSONObject(jsonString.trim());
        Reading reading = new Reading();
        reading.sensorId = obj.getLong("sensorId");
        reading.no2 = obj.getDouble("no2");
        reading.scalar = obj.getLong("scalar");
        reading.vector = Math.toIntExact(obj.getLong("vector"));
        return reading;
    }
}
