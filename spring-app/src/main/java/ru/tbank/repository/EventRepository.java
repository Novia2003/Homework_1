package ru.tbank.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.entity.EventEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> findAll(Specification<EventEntity> specification);

    static Specification<EventEntity> buildSpecification(
            String name, String placeName, LocalDate fromDate, LocalDate toDate
    ) {
        List<Specification<EventEntity>> specifications = new ArrayList<>();

        if (name != null) {
            specifications.add((Specification<EventEntity>) (event, query, criteriaBuilder) ->
                    criteriaBuilder.equal(event.get("name"), name));
        }

        if (placeName != null) {
            specifications.add((Specification<EventEntity>) (event, query, criteriaBuilder) ->
                    criteriaBuilder.equal(event.get("place").get("name"), placeName));
        }

        if (fromDate != null) {
            specifications.add((Specification<EventEntity>) (event, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(event.get("date"), fromDate));
        }

        if (toDate != null) {
            specifications.add((Specification<EventEntity>) (event, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(event.get("date"), toDate));
        }

        return specifications.stream().reduce(Specification::and).orElse(null);
    }
}
