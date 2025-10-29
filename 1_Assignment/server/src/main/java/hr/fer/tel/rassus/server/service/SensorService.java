package hr.fer.tel.rassus.server.service;

import hr.fer.tel.rassus.server.model.domain.Sensor;
import hr.fer.tel.rassus.server.model.dto.SensorDto;
import hr.fer.tel.rassus.server.model.request.RegisterSensorRequest;
import hr.fer.tel.rassus.server.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static java.lang.Math.*;

@Service
public class SensorService {

    @Autowired
    private SensorRepository sensorRepository;

    public SensorDto registerSensor(RegisterSensorRequest request) {
        Sensor sensor = new Sensor(request.getLatitude(), request.getLongitude(), request.getIp(), request.getPort());

        Sensor savedSensor = sensorRepository.save(sensor);

        return new SensorDto(savedSensor);
    }

    public List<SensorDto> getAllSensors() {
        List<Sensor> sensors = sensorRepository.findAll();

        return sensors.stream().map(SensorDto::new).toList();
    }

    public SensorDto getSensorById(Long id) {
        Sensor sensor = sensorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found"));
        return new SensorDto(sensor);
    }

    public SensorDto findNearestNeighbour(Long sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found"));
        List<Sensor> allSensors = sensorRepository.findAll();

        Sensor nearestSensor = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Sensor s : allSensors) {
            if (s.getId().equals(sensorId)) continue;
            double distance = haversine(sensor.getLatitude(), sensor.getLongitude(), s.getLatitude(), s.getLongitude());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestSensor = s;
            }
        }

        if (nearestSensor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No other sensors registered");
        }

        return new SensorDto(nearestSensor);
    }

    // util function for Haversine
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // radius of Earth in km
        double dLat = toRadians(lat2 - lat1);
        double dLon = toRadians(lon2 - lon1);
        double a = sin(dLat / 2) * sin(dLat / 2) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return R * c;
    }
}
