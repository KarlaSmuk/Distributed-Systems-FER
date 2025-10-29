package hr.fer.tel.rassus.server.model.request;

import lombok.Data;

@Data
public class CreateReadingRequest {

    private Double temperature;

    private Double pressure;

    private Double humidity;

    private Double co;

    private Double no2;
}
