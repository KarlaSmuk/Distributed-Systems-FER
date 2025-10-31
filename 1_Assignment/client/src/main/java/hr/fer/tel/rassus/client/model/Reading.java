package hr.fer.tel.rassus.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Reading {

    private Double temperature;

    private Double pressure;

    private Double humidity;

    private Double co;

    private Double no2;

    private Double so2;
}
