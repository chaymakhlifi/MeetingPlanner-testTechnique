package com.example.meetingplanner.scenario;

import com.example.meetingplanner.AbstractIntegrationTest;
import com.example.meetingplanner.controller.MainController;
import com.example.meetingplanner.entity.*;
import com.example.meetingplanner.model.Reservation;
import com.example.meetingplanner.model.TypeReunion;
import com.example.meetingplanner.model.request.ReservationCreateRequest;
import com.example.meetingplanner.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

/** Scénarios de tests d'intégration */
@SpringBootTest
public class ScenarioTest extends AbstractIntegrationTest {

  private static final Instant START_OF_DAY = Instant.parse("2021-05-03T00:00:00Z");

  @Autowired private MaterielFixeRepository materielFixeRepository;
  @Autowired private MaterielMobileRepository materielMobileRepository;
  @Autowired private ReservationRepository reservationRepository;
  @Autowired private SalleRepository salleRepository;
  @Autowired private TypeMaterielRepository typeMaterielRepository;
  @Autowired private TypeReunionRepository typeReunionRepository;
  @Autowired private MainController mainController;

  private Integer idVc;
  private Integer idSpec;
  private Integer idRs;
  private Integer idRc;

  @BeforeEach
  void init() {
    // Clear db
    reservationRepository.deleteAll();
    materielFixeRepository.deleteAll();
    materielMobileRepository.deleteAll();
    salleRepository.deleteAll();
    typeReunionRepository.deleteAll();
    typeMaterielRepository.deleteAll();

    // Init db
    // 4 types materiel
    TypeMaterielDb ecran = saveTypeMateriel("écran");
    TypeMaterielDb pieuvre = saveTypeMateriel("pieuvre");
    TypeMaterielDb webcam = saveTypeMateriel("webcam");
    TypeMaterielDb tableau = saveTypeMateriel("tableau");
    // 4 types reunion
    TypeReunionDb vc = saveTypeReunion("visioconférence", Set.of(ecran, pieuvre, webcam));
    idVc = vc.getId();
    TypeReunionDb spec = saveTypeReunion("séance de partage et d'études de cas", Set.of(tableau));
    idSpec = spec.getId();
    TypeReunionDb rs = saveTypeReunion("réunion simple", Set.of());
    idRs = rs.getId();
    TypeReunionDb rc = saveTypeReunion("réunion couplée", Set.of(tableau, ecran, pieuvre));
    idRc = rc.getId();
    // 12 salles
    SalleDb e1001 = saveSalleWithMaterielFixe("E1001", 23, Set.of());
    SalleDb e1002 = saveSalleWithMaterielFixe("E1002", 10, Set.of(ecran));
    SalleDb e1003 = saveSalleWithMaterielFixe("E1003", 8, Set.of(pieuvre));
    SalleDb e1004 = saveSalleWithMaterielFixe("E1004", 4, Set.of(tableau));
    SalleDb e2001 = saveSalleWithMaterielFixe("E2001", 4, Set.of());
    SalleDb e2002 = saveSalleWithMaterielFixe("E2002", 15, Set.of(ecran, pieuvre));
    SalleDb e2003 = saveSalleWithMaterielFixe("E2003", 7, Set.of());
    SalleDb e2004 = saveSalleWithMaterielFixe("E2004", 9, Set.of(tableau));
    SalleDb e3001 = saveSalleWithMaterielFixe("E3001", 13, Set.of(ecran, webcam, pieuvre));
    SalleDb e3002 = saveSalleWithMaterielFixe("E3002", 8, Set.of());
    SalleDb e3003 = saveSalleWithMaterielFixe("E3003", 9, Set.of(ecran, pieuvre));
    SalleDb e3004 = saveSalleWithMaterielFixe("E3004", 4, Set.of());
    // 15 materiel mobile
    saveMultipleMaterielMobile(pieuvre, 4);
    saveMultipleMaterielMobile(ecran, 5);
    saveMultipleMaterielMobile(webcam, 4);
    saveMultipleMaterielMobile(tableau, 2);

    assertThat(typeMaterielRepository.count()).isEqualTo(4);
    assertThat(typeReunionRepository.count()).isEqualTo(4);
    assertThat(salleRepository.count()).isEqualTo(12);
    assertThat(materielFixeRepository.count()).isEqualTo(11);
    assertThat(materielMobileRepository.count()).isEqualTo(15);
    assertThat(reservationRepository.count()).isEqualTo(0);
  }

  /**
   * Scénario 1
   *
   * <p>L'utilisateur consulte la liste des type de réunion, puis fait une réservation pour l'un de
   * ces types de réunion.
   */
  @Test
  void scenario1() {
    // L'utilisateur consulte la liste des type de réunion possible
    Set<TypeReunion> typeReunions = mainController.getTypeReunionList();

    // L'utilisateur voit 4 types possibles de réunion. Il en choisit un.
    assertThat(typeReunions.size()).isEqualTo(4);
    TypeReunion typeReunionChoisi =
        typeReunions.stream()
            .filter(typeReunion -> typeReunion.getNom().equals("réunion simple"))
            .findFirst()
            .orElseThrow();

    // L'utilisateur demande à réserver une salle pour ce type de réunion
    ReservationCreateRequest requestBody =
        createRequestBodyReservation(
            typeReunionChoisi.getId(),
            // 9h -> 11h
            9,
            11,
            // 10 personnes
            10);

    Reservation reservation = mainController.postReservationCreate(requestBody);

    // L'utilisateur a reçu une réservation valide.
    assertThat(reservation).isNotNull();
    assertThat(reservation.getDebut()).isEqualTo(START_OF_DAY.plus(9, HOURS));
    assertThat(reservation.getFin()).isEqualTo(START_OF_DAY.plus(11, HOURS));
    assertThat(reservation.getSalle().getCapacite()).isGreaterThan(10);
    assertThat(reservation.getTypeReunion().getNom()).isEqualTo("réunion simple");

    // Cette réservation est bien présente en base de données.
    assertThat(reservationRepository.findById(reservation.getId())).isNotNull();
  }

  /**
   * Scénario 2
   *
   * <p>L'ensemble des réservations proposées dans le document de présentation du projet sont jouées
   * successivement. On souhaite obtenir au moins 90% de réservations réussies. (90% correspond au
   * taux de réussite pour la version 1 de l'algorithme de choix de salle - de futurs algorithme
   * devraient avoir un score au moins équivalent)
   */
  @Test
  void scenario2() {

    List<ReservationCreateRequest> requests =
        List.of(
            createRequestBodyReservation(idVc, 9, 10, 8),
            createRequestBodyReservation(idVc, 9, 10, 6),
            createRequestBodyReservation(idRc, 11, 12, 4),
            createRequestBodyReservation(idRs, 11, 12, 2),
            createRequestBodyReservation(idSpec, 11, 12, 9),
            createRequestBodyReservation(idRc, 9, 10, 7),
            createRequestBodyReservation(idVc, 8, 9, 9),
            createRequestBodyReservation(idSpec, 8, 9, 10),
            createRequestBodyReservation(idSpec, 9, 10, 5),
            createRequestBodyReservation(idRs, 9, 10, 4),
            createRequestBodyReservation(idRc, 9, 10, 8),
            createRequestBodyReservation(idVc, 11, 12, 12),
            createRequestBodyReservation(idSpec, 11, 12, 5),
            createRequestBodyReservation(idVc, 8, 9, 3),
            createRequestBodyReservation(idSpec, 8, 9, 2),
            createRequestBodyReservation(idVc, 8, 9, 12),
            createRequestBodyReservation(idVc, 10, 11, 6),
            createRequestBodyReservation(idRs, 11, 12, 2),
            createRequestBodyReservation(idRs, 9, 10, 4),
            createRequestBodyReservation(idRc, 9, 10, 7));
    assertThat(requests.size()).isEqualTo(20);

    long countSuccess =
        requests.stream()
            .map(mainController::postReservationCreate)
            .filter(Objects::nonNull)
            .count();

    // At least 18 success out of the 20 requests
    assertThat(countSuccess).isGreaterThanOrEqualTo(18);
  }

  /////////////
  // private //
  /////////////

  private TypeMaterielDb saveTypeMateriel(String nom) {
    TypeMaterielDb typeMaterielDb = new TypeMaterielDb();
    typeMaterielDb.setNom(nom);
    return typeMaterielRepository.save(typeMaterielDb);
  }

  private TypeReunionDb saveTypeReunion(String nom, Set<TypeMaterielDb> typeMaterielDbs) {
    TypeReunionDb typeReunionDb = new TypeReunionDb();
    typeReunionDb.setNom(nom);
    typeReunionDb.setTypeMaterielRequis(typeMaterielDbs);
    return typeReunionRepository.save(typeReunionDb);
  }

  private SalleDb saveSalleWithMaterielFixe(
      String nom, Integer capacite, Set<TypeMaterielDb> typeMaterielDbs) {
    SalleDb salleDb = new SalleDb();
    salleDb.setNom(nom);
    salleDb.setCapacite(capacite);
    salleDb = salleRepository.save(salleDb);

    SalleDb finalSalleDb = salleDb;
    typeMaterielDbs.forEach(
        typeMaterielDb -> {
          MaterielFixeDb materielFixeDb = new MaterielFixeDb();
          materielFixeDb.setTypeMateriel(typeMaterielDb);
          materielFixeDb.setSalle(finalSalleDb);
          materielFixeRepository.save(materielFixeDb);
        });

    return salleDb;
  }

  private void saveMultipleMaterielMobile(TypeMaterielDb typeMaterielDb, Integer quantity) {
    for (int i = 0; i < quantity; i++) {
      MaterielMobileDb materielMobileDb = new MaterielMobileDb();
      materielMobileDb.setTypeMateriel(typeMaterielDb);
      materielMobileRepository.save(materielMobileDb);
    }
  }

  private ReservationCreateRequest createRequestBodyReservation(
      Integer idTypeReunion, Integer heureDebut, Integer heureFin, Integer nombrePersonne) {
    return ReservationCreateRequest.builder()
        .idTypeReunion(idTypeReunion)
        .debut(START_OF_DAY.plus(heureDebut, HOURS))
        .fin(START_OF_DAY.plus(heureFin, HOURS))
        .nombrePersonne(nombrePersonne)
        .build();
  }
}
