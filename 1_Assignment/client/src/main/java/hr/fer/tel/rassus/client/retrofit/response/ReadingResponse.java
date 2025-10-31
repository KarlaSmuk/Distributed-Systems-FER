package hr.fer.tel.rassus.client.retrofit.response;

import lombok.Data;

@Data
public class ReadingResponse {

    private Long id;

    private Double temperature;

    private Double pressure;

    private Double humidity;

    private Double co;

    private Double no2;

    private Double so2;
}
