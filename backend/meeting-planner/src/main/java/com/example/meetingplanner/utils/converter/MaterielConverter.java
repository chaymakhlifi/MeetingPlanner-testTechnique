package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.MaterielFixeDb;
import com.example.meetingplanner.entity.MaterielMobileDb;
import com.example.meetingplanner.model.Materiel;

public class MaterielConverter {

    public static Materiel fromDbFixe(MaterielFixeDb materielFixeDb) {
        return Materiel
                .builder()
                .id(materielFixeDb.getId())
                .typeMateriel(TypeMaterielConverter.fromDb(materielFixeDb.getTypeMateriel()))
                .mobile(false)
                .build();
    }

    public static Materiel fromDbMobile(MaterielMobileDb materielMobileDb) {
        return Materiel
                .builder()
                .id(materielMobileDb.getId())
                .typeMateriel(TypeMaterielConverter.fromDb(materielMobileDb.getTypeMateriel()))
                .mobile(true)
                .build();
    }
}
