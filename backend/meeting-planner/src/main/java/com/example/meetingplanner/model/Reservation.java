package com.example.meetingplanner.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Set;

@Builder
@Value
public class Reservation {
    Integer id;
    String idReservateur;
    TypeReunion typeReunion;
    Salle salle;
    Set<Materiel> materielsMobiles;
    Instant debut;
    Instant fin;
}
