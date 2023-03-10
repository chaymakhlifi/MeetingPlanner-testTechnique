package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.TypeReunionDb;
import com.example.meetingplanner.model.TypeReunion;

public class TypeReunionConverter {

    public static TypeReunion fromDb(TypeReunionDb typeReunionDb) {
        return TypeReunion
                .builder()
                .id(typeReunionDb.getId())
                .nom(typeReunionDb.getNom())
                .build();
    }
}
