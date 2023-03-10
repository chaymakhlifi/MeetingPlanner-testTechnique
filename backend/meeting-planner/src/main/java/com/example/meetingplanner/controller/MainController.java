package com.example.meetingplanner.controller;

import com.example.meetingplanner.model.Reservation;
import com.example.meetingplanner.model.TypeReunion;
import com.example.meetingplanner.model.request.ReservationCreateRequest;
import com.example.meetingplanner.service.MainBookingService;
import com.example.meetingplanner.service.db.ReservationDbService;
import com.example.meetingplanner.service.db.TypeReunionDbService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping
public class MainController {

  private final TypeReunionDbService typeReunionDbService;
  private final ReservationDbService reservationDbService;
  private final MainBookingService mainBookingService;

  @GetMapping("/type-reunion/list")
  public Set<TypeReunion> getTypeReunionList() {
    return typeReunionDbService.fetchAll();
  }

  @GetMapping("/reservation/list")
  public Set<Reservation> getReservationList() {
    return reservationDbService.fetchAll();
  }

  @PostMapping("/reservation/create")
  public Reservation postReservationCreate(@RequestBody ReservationCreateRequest body) {
    String idReservateur = "foobar";
    return mainBookingService.findAndBook(
        body.getIdTypeReunion(),
        body.getNombrePersonne(),
        body.getDebut(),
        body.getFin(),
        idReservateur);
  }
}
