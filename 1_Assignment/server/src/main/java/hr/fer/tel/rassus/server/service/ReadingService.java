package hr.fer.tel.rassus.server.service;

import hr.fer.tel.rassus.server.model.domain.Reading;
import hr.fer.tel.rassus.server.model.domain.Sensor;
import hr.fer.tel.rassus.server.model.dto.ReadingDto;
import hr.fer.tel.rassus.server.model.request.CreateReadingRequest;
import hr.fer.tel.rassus.server.repository.ReadingRepository;
import hr.fer.tel.rassus.server.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadingService {

    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private SensorRepository sensorRepository;

    public ReadingDto createReading(CreateReadingRequest request, Long sensorId) {

        Sensor sensor = sensorRepository.findById(sensorId).orElse(null);
        if (sensor == null) return null;

        Reading reading = new Reading(request, sensor);

        System.out.println("Reading: no2 = " + reading.getNo2() + ", so2 = " + reading.getSo2());

        readingRepository.save(reading);

        return new ReadingDto(reading);
    }

    public List<ReadingDto> getReadingsBySensor(long sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId).orElse(null);
        if (sensor == null) return null;

        List<Reading> readings = readingRepository.findAllBySensor(sensor);

        return readings.stream()
                .map(ReadingDto::new)
                .toList();
    }
}
