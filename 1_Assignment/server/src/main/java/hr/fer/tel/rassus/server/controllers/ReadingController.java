package hr.fer.tel.rassus.server.controllers;

import hr.fer.tel.rassus.server.model.dto.ReadingDto;
import hr.fer.tel.rassus.server.model.request.CreateReadingRequest;
import hr.fer.tel.rassus.server.service.ReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/readings")
public class ReadingController {

    @Autowired
    private ReadingService readingService;

    // TODO 4.3  Spremanje očitanja pojedinog senzora
    @PostMapping("/sensor/{sensorId}")
    public ResponseEntity<ReadingDto> createReading(
            @RequestBody CreateReadingRequest request,
            @PathVariable String sensorId
    ) {
        ReadingDto savedReading = readingService.createReading(request, Long.parseLong(sensorId));

        if (savedReading == null) {
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/readings/{id}")
                .buildAndExpand(savedReading.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedReading);
    }

    // TODO 4.5  Popis očitanja pojedinog senzora
    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<List<ReadingDto>> getReadings(@PathVariable String sensorId) {
        List<ReadingDto> readings = readingService.getReadingsBySensor(Long.parseLong(sensorId));

        if (readings == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(readings);
    }

}