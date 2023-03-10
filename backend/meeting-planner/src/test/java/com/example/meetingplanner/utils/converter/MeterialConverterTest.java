package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.MaterielFixeDb;
import com.example.meetingplanner.entity.MaterielMobileDb;
import com.example.meetingplanner.entity.SalleDb;
import com.example.meetingplanner.entity.TypeMaterielDb;
import com.example.meetingplanner.model.Materiel;
import com.example.meetingplanner.model.TypeMateriel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MeterialConverterTest {

  @Test
  void fromDbFixe() {

    // [GIVEN] entities from db

    TypeMaterielDb typeMaterielDb = new TypeMaterielDb();
    typeMaterielDb.setId(1);
    typeMaterielDb.setNom("nom type materiel");

    SalleDb salleDb = new SalleDb();
    salleDb.setId(2);
    salleDb.setNom("nom salle");

    MaterielFixeDb materielFixeDb = new MaterielFixeDb();
    materielFixeDb.setId(3);
    materielFixeDb.setSalle(salleDb);
    materielFixeDb.setTypeMateriel(typeMaterielDb);

    // [WHEN] converting

    Materiel materiel = MaterielConverter.fromDbFixe(materielFixeDb);

    // [THEN] expected values in converted result

    Materiel expected =
        Materiel.builder()
            .id(3)
            .mobile(false)
            .typeMateriel(TypeMateriel.builder().id(1).nom("nom type materiel").build())
            .build();

    assertThat(materiel).isEqualTo(expected);
  }

  @Test
  void fromDbMobile() {

    // [GIVEN] entities from db

    TypeMaterielDb typeMaterielDb = new TypeMaterielDb();
    typeMaterielDb.setId(1);
    typeMaterielDb.setNom("nom type materiel");

    MaterielMobileDb materielMobileDb = new MaterielMobileDb();
    materielMobileDb.setId(2);
    materielMobileDb.setTypeMateriel(typeMaterielDb);

    // [WHEN] converting

    Materiel materiel = MaterielConverter.fromDbMobile(materielMobileDb);

    // [THEN] expected values in converted result

    Materiel expected =
        Materiel.builder()
            .id(2)
            .mobile(true)
            .typeMateriel(TypeMateriel.builder().id(1).nom("nom type materiel").build())
            .build();

    assertThat(materiel).isEqualTo(expected);
  }
}
