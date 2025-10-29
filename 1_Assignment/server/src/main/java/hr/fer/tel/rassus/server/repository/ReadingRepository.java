package hr.fer.tel.rassus.server.repository;

import hr.fer.tel.rassus.server.model.domain.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {
  //  TODO
}
