package hr.fer.tel.rassus.stupidudp.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Sensor {

    private Long id;

    private Integer port;

    private String address;

    private Integer vector;

    private List<Sensor> neighbors;
}
