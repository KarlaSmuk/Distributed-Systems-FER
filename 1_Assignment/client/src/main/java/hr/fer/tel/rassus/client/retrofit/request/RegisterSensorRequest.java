package hr.fer.tel.rassus.client.retrofit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterSensorRequest {

    private Double latitude;

    private Double longitude;

    private String ip;

    private Integer port;
}
