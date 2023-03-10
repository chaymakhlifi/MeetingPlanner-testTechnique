package com.example.meetingplanner.service.db;

import com.example.meetingplanner.entity.MaterielMobileDb;
import com.example.meetingplanner.entity.ReservationDb;
import com.example.meetingplanner.entity.SalleDb;
import com.example.meetingplanner.entity.TypeReunionDb;
import com.example.meetingplanner.model.Materiel;
import com.example.meetingplanner.model.Reservation;
import com.example.meetingplanner.repository.MaterielMobileRepository;
import com.example.meetingplanner.repository.ReservationRepository;
import com.example.meetingplanner.repository.SalleRepository;
import com.example.meetingplanner.repository.TypeReunionRepository;
import com.example.meetingplanner.utils.converter.ReservationConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReservationDbService {

  private final ReservationRepository reservationRepository;
  private final SalleRepository salleRepository;
  private final TypeReunionRepository typeReunionRepository;
  private final MaterielMobileRepository materielMobileRepository;

  public Set<Reservation> fetchAll() {
    return reservationRepository.findAll().stream()
        .map(ReservationConverter::fromDb)
        .collect(Collectors.toSet());
  }

  @Transactional
  public Reservation save(Reservation reservation) {

    // Salle must already exist
    SalleDb salleDb = salleRepository.findById(reservation.getSalle().getId()).orElseThrow();

    // Type reunion must already exist
    TypeReunionDb typeReunionDb =
        typeReunionRepository.findById(reservation.getTypeReunion().getId()).orElseThrow();

    // Materiels must already exist
    Set<MaterielMobileDb> materielMobileDbs =
        reservation.getMaterielsMobiles().stream()
            .map(Materiel::getId)
            .map(materielMobileRepository::findById)
            .map(Optional::orElseThrow)
            .collect(Collectors.toSet());

    // Saving entity
    ReservationDb reservationDb = new ReservationDb();
    reservationDb.setIdReservateur(reservation.getIdReservateur());
    reservationDb.setSalle(salleDb);
    reservationDb.setTypeReunion(typeReunionDb);
    reservationDb.setDatetimeDebut(reservation.getDebut());
    reservationDb.setDatetimeFin(reservation.getFin());
    reservationDb.setMaterielReserve(materielMobileDbs);
    return ReservationConverter.fromDb(reservationRepository.save(reservationDb));
  }
}
