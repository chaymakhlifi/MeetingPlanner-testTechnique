package com.example.meetingplanner.service;

import com.example.meetingplanner.model.*;
import com.example.meetingplanner.service.db.ReservationDbService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

/** Unit test for {@link AddReservationService} */
@ExtendWith(MockitoExtension.class)
public class AddReservationServiceTest {

  @Mock private ReservationDbService reservationDbService;
  @Captor private ArgumentCaptor<Reservation> reservationCaptor;
  @InjectMocks private AddReservationService service;

  /** Unit test for method {@link AddReservationService#addReservation} */
  @Nested
  class AddReservation {

    @Test
    void saveExpectedValue() {

      // [GIVEN] Values to save in a reservation
      String idReservateur = "id reservateur";
      Integer idTypeReunion = 1;
      Salle salle = Salle.builder().id(2).build();
      Materiel materiel = Materiel.builder().id(3).mobile(true).build();
      EntitiesToBook entitiesToBook =
          EntitiesToBook.builder().salle(salle).materielsMobiles(Set.of(materiel)).build();
      Instant debut = Instant.EPOCH;
      Instant fin = Instant.EPOCH.plus(1, DAYS);
      // [GIVEN] ReservationDbService working
      Reservation returnedReservation = Reservation.builder().id(10).build();
      lenient()
          .doReturn(returnedReservation)
          .when(reservationDbService)
          .save(any(Reservation.class));

      // [WHEN] Calling method
      Reservation result =
          service.addReservation(entitiesToBook, idReservateur, idTypeReunion, debut, fin);

      // [THEN] ReservationDbService saved expected value
      Reservation expectedSavedReservation =
          Reservation.builder()
              .idReservateur(idReservateur)
              .typeReunion(TypeReunion.builder().id(idTypeReunion).build())
              .salle(salle)
              .materielsMobiles(Set.of(materiel))
              .debut(debut)
              .fin(fin)
              .build();
      verify(reservationDbService).save(reservationCaptor.capture());
      Reservation savedReservation = reservationCaptor.getValue();
      assertThat(savedReservation).isEqualTo(expectedSavedReservation);

      // [THEN] Returns saved entity
      assertThat(result).isEqualTo(returnedReservation);
    }
  }
}
