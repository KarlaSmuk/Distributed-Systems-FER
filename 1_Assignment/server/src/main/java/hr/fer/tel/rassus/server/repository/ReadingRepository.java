package hr.fer.tel.rassus.server.repository;

import hr.fer.tel.rassus.server.model.domain.Reading;
import hr.fer.tel.rassus.server.model.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {
    List<Reading> findAllBySensor(Sensor sensor);
}
