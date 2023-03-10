package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.TypeMaterielDb;
import com.example.meetingplanner.model.TypeMateriel;

public class TypeMaterielConverter {

    public static TypeMateriel fromDb(TypeMaterielDb typeMaterielDb) {
        return TypeMateriel
                .builder()
                .id(typeMaterielDb.getId())
                .nom(typeMaterielDb.getNom())
                .build();
    }
}
