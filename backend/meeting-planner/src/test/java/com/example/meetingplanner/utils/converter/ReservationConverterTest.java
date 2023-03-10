package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.*;
import com.example.meetingplanner.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationConverterTest {

  @Test
  void fromDb() {

    // [GIVEN] entities from db

    TypeMaterielDb typeMaterielDb = new TypeMaterielDb();
    typeMaterielDb.setId(1);
    typeMaterielDb.setNom("nom type materiel");

    MaterielMobileDb materielMobileDb = new MaterielMobileDb();
    materielMobileDb.setId(2);
    materielMobileDb.setTypeMateriel(typeMaterielDb);

    SalleDb salleDb = new SalleDb();
    salleDb.setId(3);
    salleDb.setNom("nom salle");
    salleDb.setCapacite(10);
    salleDb.setMaterielsFixes(Set.of());

    TypeReunionDb typeReunionDb = new TypeReunionDb();
    typeReunionDb.setId(4);
    typeReunionDb.setNom("nom type reunion");
    typeReunionDb.setTypeMaterielRequis(Set.of());

    ReservationDb reservationDb = new ReservationDb();
    reservationDb.setId(5);
    reservationDb.setIdReservateur("id reservateur");
    reservationDb.setDatetimeDebut(Instant.EPOCH);
    reservationDb.setDatetimeFin(Instant.EPOCH.plus(1, DAYS));
    reservationDb.setTypeReunion(typeReunionDb);
    reservationDb.setSalle(salleDb);
    reservationDb.setMaterielReserve(Set.of(materielMobileDb));

    // [WHEN] converting

    Reservation reservation = ReservationConverter.fromDb(reservationDb);

    // [THEN] expected values in converted result

    Reservation expected =
        Reservation.builder()
            .id(5)
            .idReservateur("id reservateur")
            .debut(Instant.EPOCH)
            .fin(Instant.EPOCH.plus(1, DAYS))
            .typeReunion(TypeReunion.builder().id(4).nom("nom type reunion").build())
            .salle(Salle.builder().id(3).nom("nom salle").capacite(10).materiels(Set.of()).build())
            .materielsMobiles(
                Set.of(
                    Materiel.builder()
                        .id(2)
                        .mobile(true)
                        .typeMateriel(TypeMateriel.builder().id(1).nom("nom type materiel").build())
                        .build()))
            .build();
    assertThat(reservation).isEqualTo(expected);
  }
}
