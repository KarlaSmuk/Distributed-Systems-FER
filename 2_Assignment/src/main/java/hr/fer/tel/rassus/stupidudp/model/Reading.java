package hr.fer.tel.rassus.stupidudp.model;

import lombok.*;
import tools.jackson.databind.ObjectMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Reading {

    private Long sensorId;

    private Double no2;

    private Long scalarTime;

    private Integer vectorTime;


    public byte[] toBytes() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(this);
    }

    public static Reading fromBytes(byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(bytes, Reading.class);
    }
}
