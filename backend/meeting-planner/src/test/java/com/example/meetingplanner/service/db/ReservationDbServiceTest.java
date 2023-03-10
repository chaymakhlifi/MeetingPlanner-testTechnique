package com.example.meetingplanner.service.db;

import com.example.meetingplanner.AbstractIntegrationTest;
import com.example.meetingplanner.entity.MaterielMobileDb;
import com.example.meetingplanner.entity.SalleDb;
import com.example.meetingplanner.entity.TypeMaterielDb;
import com.example.meetingplanner.entity.TypeReunionDb;
import com.example.meetingplanner.model.Materiel;
import com.example.meetingplanner.model.Reservation;
import com.example.meetingplanner.model.Salle;
import com.example.meetingplanner.model.TypeReunion;
import com.example.meetingplanner.repository.*;
import com.example.meetingplanner.utils.converter.MaterielConverter;
import com.example.meetingplanner.utils.converter.SalleConverter;
import com.example.meetingplanner.utils.converter.TypeReunionConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Set;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/** Test for {@link ReservationDbService} */
@SpringBootTest
public class ReservationDbServiceTest extends AbstractIntegrationTest {

  @Autowired private TypeReunionRepository typeReunionRepository;
  @Autowired private SalleRepository salleRepository;
  @Autowired private TypeMaterielRepository typeMaterielRepository;
  @Autowired private MaterielMobileRepository materielMobileRepository;
  @Autowired private ReservationRepository reservationRepository;
  @Autowired private ReservationDbService service;

  @BeforeEach
  void clearDb() {
    reservationRepository.deleteAll();
    materielMobileRepository.deleteAll();
    typeMaterielRepository.deleteAll();
    salleRepository.deleteAll();
    typeReunionRepository.deleteAll();
  }

  /** Test for method {@link ReservationDbService#save} */
  @Nested
  class Save {

    @Test
    void whenAlright() {
      // [GIVEN] Existing type reunion
      TypeReunionDb typeReunionDb = saveTypeReunion();
      TypeReunion typeReunion = TypeReunionConverter.fromDb(typeReunionDb);

      // [GIVEN] Existing salle
      SalleDb salleDb = saveSalle();
      Salle salle = SalleConverter.fromDb(salleDb);

      // [GIVEN] Existing materiel
      MaterielMobileDb materielMobileDb1 = saveMateriel();
      MaterielMobileDb materielMobileDb2 = saveMateriel();
      Materiel materiel1 = MaterielConverter.fromDbMobile(materielMobileDb1);
      Materiel materiel2 = MaterielConverter.fromDbMobile(materielMobileDb2);

      assertThat(typeReunionRepository.count()).isEqualTo(1);
      assertThat(salleRepository.count()).isEqualTo(1);
      assertThat(materielMobileRepository.count()).isEqualTo(2);
      assertThat(reservationRepository.count()).isEqualTo(0);

      // [WHEN] Calling method to create a reservation
      Reservation toCreate =
          Reservation.builder()
              .idReservateur("id reservateur")
              .typeReunion(typeReunion)
              .salle(salle)
              .materielsMobiles(Set.of(materiel1, materiel2))
              .debut(Instant.EPOCH)
              .fin(Instant.EPOCH.plus(1, HOURS))
              .build();
      Reservation result = service.save(toCreate);

      // [THEN] Entity was created in db
      assertThat(reservationRepository.count()).isEqualTo(1);
      // [THEN] Other tables were unchanged
      assertThat(typeReunionRepository.count()).isEqualTo(1);
      assertThat(salleRepository.count()).isEqualTo(1);
      assertThat(materielMobileRepository.count()).isEqualTo(2);
      // [THEN] Result has expected values
      assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(toCreate);
      assertThat(result.getId()).isNotNull();
    }

    @Test
    void whenTypeReunionNotExisting() {
      // [GIVEN] Not existing type reunion
      TypeReunion typeReunion = TypeReunion.builder().id(1000).nom("not existing").build();

      // [GIVEN] Existing salle
      SalleDb salleDb = saveSalle();
      Salle salle = SalleConverter.fromDb(salleDb);

      // [GIVEN] Existing materiel
      MaterielMobileDb materielMobileDb1 = saveMateriel();
      MaterielMobileDb materielMobileDb2 = saveMateriel();
      Materiel materiel1 = MaterielConverter.fromDbMobile(materielMobileDb1);
      Materiel materiel2 = MaterielConverter.fromDbMobile(materielMobileDb2);

      assertThat(typeReunionRepository.count()).isEqualTo(0);
      assertThat(salleRepository.count()).isEqualTo(1);
      assertThat(materielMobileRepository.count()).isEqualTo(2);
      assertThat(reservationRepository.count()).isEqualTo(0);

      // [WHEN] Calling method to create a reservation
      Reservation toCreate =
          Reservation.builder()
              .idReservateur("id reservateur")
              .typeReunion(typeReunion)
              .salle(salle)
              .materielsMobiles(Set.of(materiel1, materiel2))
              .debut(Instant.EPOCH)
              .fin(Instant.EPOCH.plus(1, HOURS))
              .build();
      Throwable thrown = catchThrowable(() -> service.save(toCreate));

      // [THEN] An exception was thrown
      assertThat(thrown).isNotNull();
      // [THEN] Tables are untouched
      assertThat(typeReunionRepository.count()).isEqualTo(0);
      assertThat(salleRepository.count()).isEqualTo(1);
      assertThat(materielMobileRepository.count()).isEqualTo(2);
      assertThat(reservationRepository.count()).isEqualTo(0);
    }

    @Test
    void whenSalleNotExisting() {
      // [GIVEN] Existing type reunion
      TypeReunionDb typeReunionDb = saveTypeReunion();
      TypeReunion typeReunion = TypeReunionConverter.fromDb(typeReunionDb);

      // [GIVEN] Not existing salle
      Salle salle = Salle.builder().id(1000).capacite(10).materiels(Set.of()).build();

      // [GIVEN] Existing materiel
      MaterielMobileDb materielMobileDb1 = saveMateriel();
      MaterielMobileDb materielMobileDb2 = saveMateriel();
      Materiel materiel1 = MaterielConverter.fromDbMobile(materielMobileDb1);
      Materiel materiel2 = MaterielConverter.fromDbMobile(materielMobileDb2);

      assertThat(typeReunionRepository.count()).isEqualTo(1);
      assertThat(salleRepository.count()).isEqualTo(0);
      assertThat(materielMobileRepository.count()).isEqualTo(2);
      assertThat(reservationRepository.count()).isEqualTo(0);

      // [WHEN] Calling method to create a reservation
      Reservation toCreate =
          Reservation.builder()
              .idReservateur("id reservateur")
              .typeReunion(typeReunion)
              .salle(salle)
              .materielsMobiles(Set.of(materiel1, materiel2))
              .debut(Instant.EPOCH)
              .fin(Instant.EPOCH.plus(1, HOURS))
              .build();
      Throwable thrown = catchThrowable(() -> service.save(toCreate));

      // [THEN] An exception was thrown
      assertThat(thrown).isNotNull();
      // [THEN] Tables are untouched
      assertThat(typeReunionRepository.count()).isEqualTo(1);
      assertThat(salleRepository.count()).isEqualTo(0);
      assertThat(materielMobileRepository.count()).isEqualTo(2);
      assertThat(reservationRepository.count()).isEqualTo(0);
    }

    @Test
    void whenMaterielNotExisting() {
      // [GIVEN] Existing type reunion
      TypeReunionDb typeReunionDb = saveTypeReunion();
      TypeReunion typeReunion = TypeReunionConverter.fromDb(typeReunionDb);

      // [GIVEN] Existing salle
      SalleDb salleDb = saveSalle();
      Salle salle = SalleConverter.fromDb(salleDb);

      // [GIVEN] Not existing materiel
      MaterielMobileDb materielMobileDb = saveMateriel();
      Materiel existingMateriel = MaterielConverter.fromDbMobile(materielMobileDb);
      Materiel notExistingMateriel =
          Materiel.builder()
              .id(1000)
              .mobile(true)
              .typeMateriel(existingMateriel.getTypeMateriel())
              .build();

      assertThat(typeReunionRepository.count()).isEqualTo(1);
      assertThat(salleRepository.count()).isEqualTo(1);
      assertThat(materielMobileRepository.count()).isEqualTo(1);
      assertThat(reservationRepository.count()).isEqualTo(0);

      // [WHEN] Calling method to create a reservation
      Reservation toCreate =
          Reservation.builder()
              .idReservateur("id reservateur")
              .typeReunion(typeReunion)
              .salle(salle)
              .materielsMobiles(Set.of(existingMateriel, notExistingMateriel))
              .debut(Instant.EPOCH)
              .fin(Instant.EPOCH.plus(1, HOURS))
              .build();
      Throwable thrown = catchThrowable(() -> service.save(toCreate));

      // [THEN] An exception was thrown
      assertThat(thrown).isNotNull();
      // [THEN] Tables are untouched
      assertThat(typeReunionRepository.count()).isEqualTo(1);
      assertThat(salleRepository.count()).isEqualTo(1);
      assertThat(materielMobileRepository.count()).isEqualTo(1);
      assertThat(reservationRepository.count()).isEqualTo(0);
    }
  }

  private SalleDb saveSalle() {
    SalleDb salleDb = new SalleDb();
    salleDb.setNom("nom");
    salleDb.setCapacite(10);
    salleDb.setMaterielsFixes(Set.of());
    return salleRepository.save(salleDb);
  }

  private MaterielMobileDb saveMateriel() {
    TypeMaterielDb typeMaterielDb = new TypeMaterielDb();
    typeMaterielDb.setNom("nom");
    typeMaterielDb = typeMaterielRepository.save(typeMaterielDb);

    MaterielMobileDb materielMobileDb = new MaterielMobileDb();
    materielMobileDb.setTypeMateriel(typeMaterielDb);
    return materielMobileRepository.save(materielMobileDb);
  }

  private TypeReunionDb saveTypeReunion() {
    TypeReunionDb typeReunionDb = new TypeReunionDb();
    typeReunionDb.setNom("nom");
    typeReunionDb.setTypeMaterielRequis(Set.of());
    return typeReunionRepository.save(typeReunionDb);
  }
}
