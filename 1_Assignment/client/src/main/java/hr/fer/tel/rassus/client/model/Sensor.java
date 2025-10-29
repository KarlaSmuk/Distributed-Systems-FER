package hr.fer.tel.rassus.client.model;

import lombok.Data;

@Data
public class Sensor {

    private Double latitude;

    private Double longitude;

    private String ip;

    private Integer port;
}
