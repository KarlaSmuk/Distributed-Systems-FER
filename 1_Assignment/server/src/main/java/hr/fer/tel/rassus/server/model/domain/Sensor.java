package hr.fer.tel.rassus.server.model.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;

    private Double longitude;

    private String ip;

    private Integer port;

    @OneToMany(mappedBy = "sensor")
    private List<Reading> readings;

    public Sensor(Double latitude, Double longitude, String ip, Integer port) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.ip = ip;
        this.port = port;
    }
}
