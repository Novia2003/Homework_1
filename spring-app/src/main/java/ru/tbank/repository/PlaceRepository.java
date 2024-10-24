package ru.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tbank.entity.PlaceEntity;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    @Query(value = "SELECT p FROM PlaceEntity p LEFT JOIN FETCH p.events WHERE p.id = :id")
    Optional<PlaceEntity> findByIdWithEvents(@Param("id") Long id);
}
