package com.example.meetingplanner.model;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Builder
@Value
public class EntitiesToBook {
    Salle salle;
    Set<Materiel> materielsMobiles;
}
