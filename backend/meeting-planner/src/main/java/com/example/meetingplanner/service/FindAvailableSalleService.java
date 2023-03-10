package com.example.meetingplanner.service;

import com.example.meetingplanner.RestrictionsCovidProperties;
import com.example.meetingplanner.model.Salle;
import com.example.meetingplanner.service.db.SalleDbService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@AllArgsConstructor
@Service
public class FindAvailableSalleService {

  private final RestrictionsCovidProperties restrictionsCovidProperties;
  private final SalleDbService salleDbService;

  public Set<Salle> find(Integer nombrePersonne, Instant debut, Instant fin) {
    assert restrictionsCovidProperties.getRatioCapacite() > 0;
    return salleDbService.searchAllAvailable(
        // La salle doit être disponible 1h avant la réunion (pour désinfection)
        debut.minus(restrictionsCovidProperties.getMinuteLibreAvant(), ChronoUnit.MINUTES),
        fin,
        // La salle ne peut être occupée qu'à 70%
        (int) Math.ceil(nombrePersonne / restrictionsCovidProperties.getRatioCapacite()));
  }
}
