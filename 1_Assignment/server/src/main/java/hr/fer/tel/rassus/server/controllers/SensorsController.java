package hr.fer.tel.rassus.server.controllers;

import hr.fer.tel.rassus.server.model.dto.SensorDto;
import hr.fer.tel.rassus.server.model.request.RegisterSensorRequest;
import hr.fer.tel.rassus.server.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sensors")
public class SensorsController {

    @Autowired
    private SensorService sensorService;

    @PostMapping("/register")
    public ResponseEntity<SensorDto> registerSensor(@RequestBody RegisterSensorRequest request) {
        try {
            SensorDto savedSensor = sensorService.registerSensor(request);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/sensors/{id}")
                    .buildAndExpand(savedSensor.getId())
                    .toUri();

            return ResponseEntity.created(location).body(savedSensor);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDto> getSensorById(@PathVariable String id) {
        return ResponseEntity.ok(sensorService.getSensorById(UUID.fromString(id)));
    }

    @GetMapping
    public ResponseEntity<List<SensorDto>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getAllSensors());
    }

    @GetMapping("/{id}/nearest-neighbour")
    public ResponseEntity<SensorDto> getNearestNeighbour(@PathVariable String id) {
        return ResponseEntity.ok(sensorService.findNearestNeighbour(UUID.fromString(id)));
    }

}
