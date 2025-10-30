package hr.fer.tel.rassus.client.retrofit.request;

import lombok.Data;

@Data
public class RegisterSensorRequest {

    private Double latitude;

    private Double longitude;

    private String ip;

    private Integer port;
}
