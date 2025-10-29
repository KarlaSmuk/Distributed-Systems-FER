package hr.fer.tel.rassus.server.model.dto;

import hr.fer.tel.rassus.server.model.domain.Reading;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadingDto {

    private Long id;

    private Double temperature;

    private Double pressure;

    private Double humidity;

    private Double co;

    private Double so2;

    private SensorDto sensor;

    public ReadingDto(Reading reading) {
        this.id = reading.getId();
        this.temperature = reading.getTemperature();
        this.pressure = reading.getPressure();
        this.humidity = reading.getHumidity();
        this.co = reading.getCo();
        this.so2 = reading.getSo2();
        this.sensor = new SensorDto(reading.getSensor());
    }
}
