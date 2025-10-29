package hr.fer.tel.rassus.server.model.dto;

import hr.fer.tel.rassus.server.model.domain.Sensor;
import lombok.Data;

@Data
public class SensorDto {

    private String id;

    private Double latitude;

    private Double longitude;

    private String ip;

    private Integer port;

    public SensorDto(Sensor sensor) {
        this.id = String.valueOf(sensor.getId());
        this.latitude = sensor.getLatitude();
        this.longitude = sensor.getLongitude();
        this.ip = sensor.getIp();
        this.port = sensor.getPort();
    }
}
