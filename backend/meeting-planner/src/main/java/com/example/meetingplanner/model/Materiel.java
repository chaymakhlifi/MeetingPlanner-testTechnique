package com.example.meetingplanner.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Materiel {
    Integer id;
    TypeMateriel typeMateriel;
    Boolean mobile;
}
