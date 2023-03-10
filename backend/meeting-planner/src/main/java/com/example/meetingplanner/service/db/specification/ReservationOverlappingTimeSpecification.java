package com.example.meetingplanner.service.db.specification;

import com.example.meetingplanner.entity.ReservationDb;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;

public class ReservationOverlappingTimeSpecification implements Specification<ReservationDb> {

  private Instant debut;
  private Instant fin;
  // false -> returns entities which are NOT overlapping (does not return overlapping entities)
  // true -> returns entities where are overlapping (does not return non-overlapping entities)
  private boolean includesOverlapping;

  public ReservationOverlappingTimeSpecification(
      Instant debut, Instant fin, boolean includesOverlapping) {
    assert debut.isBefore(fin);
    this.debut = debut;
    this.fin = fin;
    this.includesOverlapping = includesOverlapping;
  }

  @Override
  public Predicate toPredicate(
      Root<ReservationDb> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

    Predicate allBefore =
        criteriaBuilder.lessThanOrEqualTo(root.<Instant>get("datetimeFin"), debut);
    Predicate allAfter =
        criteriaBuilder.greaterThanOrEqualTo(root.<Instant>get("datetimeDebut"), fin);
    Predicate notOverlapping = criteriaBuilder.or(allBefore, allAfter);

    return includesOverlapping ? criteriaBuilder.not(notOverlapping) : notOverlapping;
  }
}
