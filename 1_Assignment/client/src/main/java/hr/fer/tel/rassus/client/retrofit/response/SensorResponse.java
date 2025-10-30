package hr.fer.tel.rassus.client.retrofit.response;

import lombok.Data;

@Data
public class SensorResponse {

    private Long id;

    private Double latitude;

    private Double longitude;

    private String ip;

    private Integer port;
}
