package com.example.meetingplanner.service;

import com.example.meetingplanner.model.EntitiesToBook;
import com.example.meetingplanner.model.Reservation;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@AllArgsConstructor
@Service
public class MainBookingService {

  private final ChooseEntitiesToBookService chooseEntitiesToBookService;
  private final AddReservationService addReservationService;

  @Nullable
  public Reservation findAndBook(
      Integer idTypeReunion,
      Integer nombrePersonne,
      Instant debut,
      Instant fin,
      String idReservateur) {
    EntitiesToBook entitiesToBook =
        chooseEntitiesToBookService.choose(idTypeReunion, nombrePersonne, debut, fin);
    if (entitiesToBook == null) {
      return null;
    }
    return addReservationService.addReservation(
        entitiesToBook, idReservateur, idTypeReunion, debut, fin);
  }
}
