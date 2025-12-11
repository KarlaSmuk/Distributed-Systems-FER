package hr.fer.tel.rassus.stupidudp.model;

import lombok.*;

import java.util.ArrayList;
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

    private Integer vector = 0; // its always max bc it increases immediately after receiving reading or before sending new one

    private List<Sensor> neighbors = new ArrayList<>();

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
