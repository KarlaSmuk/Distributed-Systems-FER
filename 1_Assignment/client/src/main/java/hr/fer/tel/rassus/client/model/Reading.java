package hr.fer.tel.rassus.client.model;

import lombok.Data;

@Data
public class Reading {

    private Double temperature;

    private Double pressure;

    private Double humidity;

    private Double co;

    private Double no2;

    public Reading(double temperature, double pressure, double humidity, double co, Double no2_so2) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.co = co;
        this.no2 = no2_so2;
    }
}
