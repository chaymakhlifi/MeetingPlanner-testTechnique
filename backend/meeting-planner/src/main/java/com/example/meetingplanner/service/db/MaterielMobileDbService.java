package com.example.meetingplanner.service.db;

import com.example.meetingplanner.entity.MaterielMobileDb;
import com.example.meetingplanner.entity.ReservationDb;
import com.example.meetingplanner.model.Materiel;
import com.example.meetingplanner.repository.MaterielMobileRepository;
import com.example.meetingplanner.repository.ReservationRepository;
import com.example.meetingplanner.service.db.specification.MaterielMobileAvailableSpecification;
import com.example.meetingplanner.service.db.specification.ReservationOverlappingTimeSpecification;
import com.example.meetingplanner.utils.converter.MaterielConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MaterielMobileDbService {

  private final MaterielMobileRepository materielMobileRepository;
  private final ReservationRepository reservationRepository;

  @Transactional
  public Set<Materiel> searchAllAvailable(Instant debut, Instant fin, Set<Integer> idTypeMateriel) {
    Set<Integer> idMaterielsReserves =
        reservationRepository
            .findAll(new ReservationOverlappingTimeSpecification(debut, fin, true))
            .stream()
            .map(ReservationDb::getMaterielReserve)
            .flatMap(Collection::stream)
            .map(MaterielMobileDb::getId)
            .collect(Collectors.toSet());

    // Matériels disponibles et respectant les contraintes
    return materielMobileRepository
        .findAll(new MaterielMobileAvailableSpecification(idTypeMateriel, idMaterielsReserves))
        .stream()
        .map(MaterielConverter::fromDbMobile)
        .collect(Collectors.toSet());
  }
}
