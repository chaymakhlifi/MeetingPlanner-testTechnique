package com.example.meetingplanner.model.request;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Builder
@Value
public class ReservationCreateRequest {
    Integer idTypeReunion;
    Integer nombrePersonne;
    Instant debut;
    Instant fin;
}
