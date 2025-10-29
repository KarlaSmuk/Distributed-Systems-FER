package hr.fer.tel.rassus.server.model.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double latitude;

    private Double longitude;

    private String ip;

    private Integer port;

    public Sensor(Double latitude, Double longitude, String ip, Integer port) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.ip = ip;
        this.port = port;
    }
}
