package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.SalleDb;
import com.example.meetingplanner.model.Salle;

import java.util.stream.Collectors;

public class SalleConverter {

    public static Salle fromDb(SalleDb salleDb) {
        return Salle
                .builder()
                .id(salleDb.getId())
                .nom(salleDb.getNom())
                .capacite(salleDb.getCapacite())
                .materiels(
                        salleDb
                                .getMaterielsFixes()
                                .stream()
                                .map(MaterielConverter::fromDbFixe)
                                .collect(Collectors.toSet()))
                .build();
    }
}
