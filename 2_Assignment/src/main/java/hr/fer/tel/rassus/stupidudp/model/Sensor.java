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

    private Integer vector = 0;

    private List<Sensor> neighbors;

    public Sensor(Long id, String address, Integer port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public void increaseVector(){
        this.vector++;
    }

    public void addNeighbor(Sensor sensor){
        this.neighbors.add(sensor);
    }
}
