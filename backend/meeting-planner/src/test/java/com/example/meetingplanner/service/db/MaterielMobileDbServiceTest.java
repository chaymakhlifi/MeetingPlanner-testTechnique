package com.example.meetingplanner.service.db;

import com.example.meetingplanner.AbstractIntegrationTest;
import com.example.meetingplanner.entity.*;
import com.example.meetingplanner.model.Materiel;
import com.example.meetingplanner.model.TypeMateriel;
import com.example.meetingplanner.repository.*;
import com.example.meetingplanner.utils.converter.MaterielConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Set;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

/** Test for {@link MaterielMobileDbService} */
@SpringBootTest
public class MaterielMobileDbServiceTest extends AbstractIntegrationTest {

  @Autowired private TypeReunionRepository typeReunionRepository;
  @Autowired private SalleRepository salleRepository;
  @Autowired private TypeMaterielRepository typeMaterielRepository;
  @Autowired private MaterielMobileRepository materielMobileRepository;
  @Autowired private ReservationRepository reservationRepository;
  @Autowired private MaterielMobileDbService service;

  @BeforeEach
  void clearDb() {
    reservationRepository.deleteAll();
    materielMobileRepository.deleteAll();
    typeMaterielRepository.deleteAll();
    salleRepository.deleteAll();
    typeReunionRepository.deleteAll();
  }

  /** Test for method {@link MaterielMobileDbService#searchAllAvailable} */
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
    void whenNoMaterielExisting() {
      // [GIVEN] No materiel in db
      assertThat(materielMobileRepository.count()).isEqualTo(0);
      // [WHEN] Calling method
      Set<Materiel> result = service.searchAllAvailable(DEBUT, FIN, Set.of(1));
      // [THEN] No result
      assertThat(result).isEmpty();
    }

    @Test
    void whenMaterielExistingAndNoReservation() {
      // [GIVEN] Some existing type materiel
      TypeMaterielDb typeMaterielDb1 = new TypeMaterielDb();
      typeMaterielDb1.setNom("type 1");
      typeMaterielDb1 = typeMaterielRepository.save(typeMaterielDb1);
      TypeMaterielDb typeMaterielDb2 = new TypeMaterielDb();
      typeMaterielDb2.setNom("type 2");
      typeMaterielDb2 = typeMaterielRepository.save(typeMaterielDb2);
      TypeMaterielDb typeMaterielDb3 = new TypeMaterielDb();
      typeMaterielDb3.setNom("type 3");
      typeMaterielDb3 = typeMaterielRepository.save(typeMaterielDb3);
      // [GIVEN] Some existing materiel
      // 2 of type 1
      // 0 of type 2
      // 1 of type 3
      MaterielMobileDb materielMobileDb1 = new MaterielMobileDb();
      materielMobileDb1.setTypeMateriel(typeMaterielDb1);
      materielMobileDb1 = materielMobileRepository.save(materielMobileDb1);
      MaterielMobileDb materielMobileDb2 = new MaterielMobileDb();
      materielMobileDb2.setTypeMateriel(typeMaterielDb1);
      materielMobileDb2 = materielMobileRepository.save(materielMobileDb2);
      MaterielMobileDb materielMobileDb3 = new MaterielMobileDb();
      materielMobileDb3.setTypeMateriel(typeMaterielDb3);
      materielMobileDb3 = materielMobileRepository.save(materielMobileDb3);

      assertThat(typeMaterielRepository.count()).isEqualTo(3);
      assertThat(materielMobileRepository.count()).isEqualTo(3);
      assertThat(reservationRepository.count()).isEqualTo(0);

      // [WHEN] Calling method for type 1 and 2
      Set<Materiel> result =
          service.searchAllAvailable(
              DEBUT, FIN, Set.of(typeMaterielDb1.getId(), typeMaterielDb2.getId()));

      // [THEN] Result contains all entities for type 1 (no entity exist for type 2, and type 3 was
      // not requested)
      TypeMateriel expectedTypeMateriel =
          TypeMateriel.builder().id(typeMaterielDb1.getId()).nom("type 1").build();
      Materiel expectedMateriel1 =
          Materiel.builder()
              .id(materielMobileDb1.getId())
              .mobile(true)
              .typeMateriel(expectedTypeMateriel)
              .build();
      Materiel expectedMateriel2 =
          Materiel.builder()
              .id(materielMobileDb2.getId())
              .mobile(true)
              .typeMateriel(expectedTypeMateriel)
              .build();
      assertThat(result).containsExactlyInAnyOrder(expectedMateriel1, expectedMateriel2);
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
    void whenMaterielExistingButReserved() {

      // [GIVEN] Existing type materiel
      TypeMaterielDb typeMaterielDb = new TypeMaterielDb();
      typeMaterielDb.setNom("type");
      typeMaterielDb = typeMaterielRepository.save(typeMaterielDb);

      // [GIVEN] Multiple materiel for this type
      // D1-F1
      MaterielMobileDb materielD1F1 = new MaterielMobileDb();
      materielD1F1.setTypeMateriel(typeMaterielDb);
      materielD1F1 = materielMobileRepository.save(materielD1F1);
      // D1-F2
      MaterielMobileDb materielD1F2 = new MaterielMobileDb();
      materielD1F2.setTypeMateriel(typeMaterielDb);
      materielD1F2 = materielMobileRepository.save(materielD1F2);
      // D1-F3
      MaterielMobileDb materielD1F3 = new MaterielMobileDb();
      materielD1F3.setTypeMateriel(typeMaterielDb);
      materielD1F3 = materielMobileRepository.save(materielD1F3);
      // D2-F2
      MaterielMobileDb materielD2F2 = new MaterielMobileDb();
      materielD2F2.setTypeMateriel(typeMaterielDb);
      materielD2F2 = materielMobileRepository.save(materielD2F2);
      // D2-F3
      MaterielMobileDb materielD2F3 = new MaterielMobileDb();
      materielD2F3.setTypeMateriel(typeMaterielDb);
      materielD2F3 = materielMobileRepository.save(materielD2F3);
      // D3-F3
      MaterielMobileDb materielD3F3 = new MaterielMobileDb();
      materielD3F3.setTypeMateriel(typeMaterielDb);
      materielD3F3 = materielMobileRepository.save(materielD3F3);

      // [GIVEN] Existing reservation for each kind of overlapping/non-overlapping pattern
      TypeReunionDb typeReunionDb = new TypeReunionDb();
      typeReunionDb.setNom("nom");
      typeReunionDb = typeReunionRepository.save(typeReunionDb);
      SalleDb salleDb = new SalleDb();
      salleDb.setNom("nom");
      salleDb.setCapacite(1000);
      salleDb = salleRepository.save(salleDb);
      // D1-F1
      ReservationDb reservationD1F1 =
          buildAndSaveReservation(salleDb, typeReunionDb, DEBUT_1, FIN_1, Set.of(materielD1F1));
      // D1-F2
      ReservationDb reservationD1F2 =
          buildAndSaveReservation(salleDb, typeReunionDb, DEBUT_1, FIN_2, Set.of(materielD1F2));
      // D1-F3
      ReservationDb reservationD1F3 =
          buildAndSaveReservation(salleDb, typeReunionDb, DEBUT_1, FIN_3, Set.of(materielD1F3));
      // D2-F2
      ReservationDb reservationD2F2 =
          buildAndSaveReservation(salleDb, typeReunionDb, DEBUT_2, FIN_2, Set.of(materielD2F2));
      // D2-F3
      ReservationDb reservationD2F3 =
          buildAndSaveReservation(salleDb, typeReunionDb, DEBUT_2, FIN_3, Set.of(materielD2F3));
      // D3-F3
      ReservationDb reservationD3F3 =
          buildAndSaveReservation(salleDb, typeReunionDb, DEBUT_3, FIN_3, Set.of(materielD3F3));

      // [WHEN] Calling method
      Set<Materiel> result = service.searchAllAvailable(DEBUT, FIN, Set.of(typeMaterielDb.getId()));

      // [THEN] Result contains only materiel for non-overlapping reservation
      Materiel expected1 = MaterielConverter.fromDbMobile(materielD1F1);
      Materiel expected2 = MaterielConverter.fromDbMobile(materielD3F3);
      assertThat(result).containsExactlyInAnyOrder(expected1, expected2);
    }
  }

  private ReservationDb buildAndSaveReservation(
      SalleDb salleDb,
      TypeReunionDb typeReunionDb,
      Instant debut,
      Instant fin,
      Set<MaterielMobileDb> materielMobileDbs) {
    ReservationDb reservationDb = new ReservationDb();
    reservationDb.setIdReservateur("id reservateur");
    reservationDb.setSalle(salleDb);
    reservationDb.setTypeReunion(typeReunionDb);
    reservationDb.setDatetimeDebut(debut);
    reservationDb.setDatetimeFin(fin);
    reservationDb.setMaterielReserve(materielMobileDbs);
    return reservationRepository.save(reservationDb);
  }
}
