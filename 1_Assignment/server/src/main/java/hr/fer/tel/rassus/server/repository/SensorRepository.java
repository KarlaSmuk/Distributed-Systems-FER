package hr.fer.tel.rassus.server.repository;

import hr.fer.tel.rassus.server.model.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
}
