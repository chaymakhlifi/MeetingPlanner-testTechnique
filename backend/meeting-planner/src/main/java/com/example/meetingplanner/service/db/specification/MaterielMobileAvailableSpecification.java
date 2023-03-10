package com.example.meetingplanner.service.db.specification;

import com.example.meetingplanner.entity.MaterielMobileDb;
import com.example.meetingplanner.entity.TypeMaterielDb;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Set;

@AllArgsConstructor
public class MaterielMobileAvailableSpecification implements Specification<MaterielMobileDb> {

  private Set<Integer> idTypesMateriels;
  private Set<Integer> idMaterielsReserves;

  @Override
  public Predicate toPredicate(
      Root<MaterielMobileDb> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

    Predicate ofRequestedType =
        idTypesMateriels.size() > 0
            ? root.<TypeMaterielDb>get("typeMateriel").<Integer>get("id").in(idTypesMateriels)
            // always false
            : criteriaBuilder.disjunction();
    Predicate notAlreadyBooked =
        idMaterielsReserves.size() > 0
            ? criteriaBuilder.not(root.<Integer>get("id").in(idMaterielsReserves))
            // always true
            : criteriaBuilder.conjunction();

    return criteriaBuilder.and(ofRequestedType, notAlreadyBooked);
  }
}
