package com.example.meetingplanner.service.db.specification;

import com.example.meetingplanner.entity.SalleDb;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Set;

@AllArgsConstructor
public class SalleAvailableSpecification implements Specification<SalleDb> {

  private Integer nombrePlace;
  private Set<Integer> idSallesOccupees;

  @Override
  public Predicate toPredicate(
      Root<SalleDb> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

    Predicate sufficientSpace =
        criteriaBuilder.greaterThanOrEqualTo(root.<Integer>get("capacite"), nombrePlace);
    Predicate notAlreadyOccupied =
        idSallesOccupees.size() > 0
            ? criteriaBuilder.not(root.<Integer>get("id").in(idSallesOccupees))
            : criteriaBuilder.conjunction();

    return criteriaBuilder.and(sufficientSpace, notAlreadyOccupied);
  }
}
