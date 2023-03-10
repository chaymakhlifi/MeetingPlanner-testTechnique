package com.example.meetingplanner.service.db;

import com.example.meetingplanner.AbstractIntegrationTest;
import com.example.meetingplanner.entity.ReservationDb;
import com.example.meetingplanner.entity.SalleDb;
import com.example.meetingplanner.entity.TypeReunionDb;
import com.example.meetingplanner.model.Salle;
import com.example.meetingplanner.repository.ReservationRepository;
import com.example.meetingplanner.repository.SalleRepository;
import com.example.meetingplanner.repository.TypeReunionRepository;
import com.example.meetingplanner.utils.converter.SalleConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Set;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

/** Test for {@link SalleDbService} */
@SpringBootTest
public class SalleDbServiceTest extends AbstractIntegrationTest {

  @Autowired private TypeReunionRepository typeReunionRepository;
  @Autowired private SalleRepository salleRepository;
  @Autowired private ReservationRepository reservationRepository;
  @Autowired private SalleDbService service;

  @BeforeEach
  void clearDb() {
    reservationRepository.deleteAll();
    salleRepository.deleteAll();
    typeReunionRepository.deleteAll();
  }

  /** Test for method {@link SalleDbService#searchAllAvailable} */
  @Nested
  class SearchAllAvailable {

    private final Instant DEBUT = Instant.EPOCH;
    private final Instant FIN = Instant.EPOCH.plus(10, HOURS);

    private final Instant DEBUT_1 = DEBUT.minus(10, HOURS);
    private final Instant FIN_1 = DEBUT.minus(9, HOURS);
    private final Instant DEBUT_2 = DEBUT.plus(1, HOURS);
    private final Instant FIN_2 = DEBUT.plus(2, HOURS);
    private final Instant DEBUT_3 = FIN.plus(10, HOURS);
    private final Instant FIN_3 = FIN.plus(11, HOURS);

    @Test
    void filterOnCapacity() {
      // [GIVEN] Existing type reunion
      TypeReunionDb typeReunionDb = saveTypeReunion();
      // [GIVEN] Some rooms with different capacities
      SalleDb salleDb1 = saveSalle("salle 1", 1);
      SalleDb salleDb2 = saveSalle("salle 2", 10);
      SalleDb salleDb3 = saveSalle("salle 3", 100);

      assertThat(typeReunionRepository.count()).isEqualTo(1);
      assertThat(salleRepository.count()).isEqualTo(3);
      assertThat(reservationRepository.count()).isEqualTo(0);

      // [WHEN] Calling method for some required capacity
      Set<Salle> result = service.searchAllAvailable(DEBUT, FIN, 10);

      // [THEN] Only rooms with enough capacity are returned
      Salle expected1 = SalleConverter.fromDb(salleDb2);
      Salle expected2 = SalleConverter.fromDb(salleDb3);
      assertThat(result).containsExactlyInAnyOrder(expected1, expected2);
    }

    // All overlaps:
    //
    //              DEBUT  ---------------->   FIN
    //
    //    D1    F1            D2     F2              D3     F3
    //
    // Not overlapping : D1-F1 / D3-F3
    // Overlapping : D1-F2 / D1-F3 / D2-F2 / D2-F3
    @Test
    void filterOnOverlappingReservation() {

      // [GIVEN] Existing type reunion
      TypeReunionDb typeReunionDb = saveTypeReunion();

      // [GIVEN] Some rooms for each kind of overlapping
      SalleDb salleD1F1 = saveSalle("salle D1-F1", 10);
      SalleDb salleD1F2 = saveSalle("salle D1-F2", 10);
      SalleDb salleD1F3 = saveSalle("salle D1-F3", 10);
      SalleDb salleD2F2 = saveSalle("salle D2-F2", 10);
      SalleDb salleD2F3 = saveSalle("salle D2-F3", 10);
      SalleDb salleD3F3 = saveSalle("salle D3-F3", 10);

      // [GIVEN] Reservations for each kind of overlapping
      saveReservation(salleD1F1, typeReunionDb, DEBUT_1, FIN_1);
      saveReservation(salleD1F2, typeReunionDb, DEBUT_1, FIN_2);
      saveReservation(salleD1F3, typeReunionDb, DEBUT_1, FIN_3);
      saveReservation(salleD2F2, typeReunionDb, DEBUT_2, FIN_2);
      saveReservation(salleD2F3, typeReunionDb, DEBUT_2, FIN_3);
      saveReservation(salleD3F3, typeReunionDb, DEBUT_3, FIN_3);

      assertThat(typeReunionRepository.count()).isEqualTo(1);
      assertThat(salleRepository.count()).isEqualTo(6);
      assertThat(reservationRepository.count()).isEqualTo(6);

      // [WHEN] Calling method
      Set<Salle> result = service.searchAllAvailable(DEBUT, FIN, 10);

      // [THEN] Only rooms without overlapping reservation are returned
      Salle expected1 = SalleConverter.fromDb(salleD1F1);
      Salle expected2 = SalleConverter.fromDb(salleD3F3);
      assertThat(result).containsExactlyInAnyOrder(expected1, expected2);
    }
  }

  private TypeReunionDb saveTypeReunion() {
    TypeReunionDb typeReunionDb = new TypeReunionDb();
    typeReunionDb.setNom("nom");
    return typeReunionRepository.save(typeReunionDb);
  }

  private SalleDb saveSalle(String nom, Integer capacite) {
    SalleDb salleDb = new SalleDb();
    salleDb.setNom(nom);
    salleDb.setCapacite(capacite);
    salleDb.setMaterielsFixes(Set.of());
    return salleRepository.save(salleDb);
  }

  private ReservationDb saveReservation(
      SalleDb salleDb, TypeReunionDb typeReunionDb, Instant debut, Instant fin) {
    ReservationDb reservationDb = new ReservationDb();
    reservationDb.setIdReservateur("id reservateur");
    reservationDb.setSalle(salleDb);
    reservationDb.setTypeReunion(typeReunionDb);
    reservationDb.setDatetimeDebut(debut);
    reservationDb.setDatetimeFin(fin);
    reservationDb.setMaterielReserve(Set.of());
    return reservationRepository.save(reservationDb);
  }
}
