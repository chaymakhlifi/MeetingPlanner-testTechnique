package com.example.meetingplanner.service.db;

import com.example.meetingplanner.entity.ReservationDb;
import com.example.meetingplanner.entity.SalleDb;
import com.example.meetingplanner.model.Salle;
import com.example.meetingplanner.repository.ReservationRepository;
import com.example.meetingplanner.repository.SalleRepository;
import com.example.meetingplanner.service.db.specification.ReservationOverlappingTimeSpecification;
import com.example.meetingplanner.service.db.specification.SalleAvailableSpecification;
import com.example.meetingplanner.utils.converter.SalleConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SalleDbService {

  private final SalleRepository salleRepository;
  private final ReservationRepository reservationRepository;

  @Transactional
  public Set<Salle> searchAllAvailable(Instant debut, Instant fin, Integer nombrePersonne) {
    // Liste des salles non disponibles
    Set<Integer> idSallesOccupees =
        reservationRepository
            .findAll(new ReservationOverlappingTimeSpecification(debut, fin, true))
            .stream()
            .map(ReservationDb::getSalle)
            .map(SalleDb::getId)
            .collect(Collectors.toSet());

    // Salles disponibles et respectant les contraintes
    return salleRepository
        .findAll(new SalleAvailableSpecification(nombrePersonne, idSallesOccupees))
        .stream()
        .map(SalleConverter::fromDb)
        .collect(Collectors.toSet());
  }
}
