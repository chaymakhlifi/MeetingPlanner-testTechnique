package com.example.meetingplanner.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TypeReunion {
    Integer id;
    String nom;
}
