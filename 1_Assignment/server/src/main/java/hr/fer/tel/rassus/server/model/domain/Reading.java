package hr.fer.tel.rassus.server.model.domain;

import hr.fer.tel.rassus.server.model.request.CreateReadingRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double temperature;

    private Double pressure;

    private Double humidity;

    private Double co;

    private Double so2;

    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    public Reading(CreateReadingRequest request, Sensor sensor) {
        this.temperature = request.getTemperature();
        this.pressure = request.getPressure();
        this.humidity = request.getHumidity();
        this.co = request.getCo();
        this.so2 = request.getSo2();
        this.sensor = sensor;
    }
}
