package com.example.meetingplanner.service;

import com.example.meetingplanner.model.EntitiesToBook;
import com.example.meetingplanner.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/** Unit test for {@link MainBookingService} */
@ExtendWith(MockitoExtension.class)
public class MainBookingServiceTest {

  @Mock private ChooseEntitiesToBookService chooseEntitiesToBookService;
  @Mock private AddReservationService addReservationService;
  @InjectMocks private MainBookingService service;

  /** Unit test for method {@link MainBookingService#findAndBook} */
  @Nested
  class FindAndBook {

    private final EntitiesToBook ENTITIES_TO_BOOK = EntitiesToBook.builder().build();
    private final String ID_RESERVATEUR = "id reservateur";
    private final Integer ID_TYPE_REUNION = 1;
    private final Instant DEBUT = Instant.EPOCH;
    private final Instant FIN = Instant.EPOCH.plus(1, DAYS);
    private final Integer NOMBRE_PERSONNE = 10;
    private final Reservation RESERVATION = Reservation.builder().build();

    @BeforeEach
    void mockAddReservationService() {
      lenient()
          .doReturn(RESERVATION)
          .when(addReservationService)
          .addReservation(ENTITIES_TO_BOOK, ID_RESERVATEUR, ID_TYPE_REUNION, DEBUT, FIN);
    }

    @Test
    void whenNoResultFound() {
      // [GIVEN] No solution found for given parameter
      lenient()
          .doReturn(null)
          .when(chooseEntitiesToBookService)
          .choose(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN);

      // [WHEN] Calling method
      Reservation result =
          service.findAndBook(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN, ID_RESERVATEUR);

      // [THEN] No reservation made
      verify(addReservationService, times(0)).addReservation(any(), any(), any(), any(), any());
      // [THEN] Empty result
      assertThat(result).isNull();
    }

    @Test
    void whenResultFound() {
      // [GIVEN] A solution found for given parameter
      lenient()
          .doReturn(ENTITIES_TO_BOOK)
          .when(chooseEntitiesToBookService)
          .choose(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN);

      // [WHEN] Calling method
      Reservation result =
          service.findAndBook(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN, ID_RESERVATEUR);

      // [THEN] A reservation is made and saved
      verify(addReservationService, times(1))
          .addReservation(ENTITIES_TO_BOOK, ID_RESERVATEUR, ID_TYPE_REUNION, DEBUT, FIN);
      // [THEN] Result contains made reservation
      assertThat(result).isEqualTo(RESERVATION);
    }
  }
}
