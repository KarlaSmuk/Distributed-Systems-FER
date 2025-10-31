package hr.fer.tel.rassus.client.retrofit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReadingRequest {

    private Double temperature;

    private Double pressure;

    private Double humidity;

    private Double co;

    private Double no2;

    private Double so2;
}
