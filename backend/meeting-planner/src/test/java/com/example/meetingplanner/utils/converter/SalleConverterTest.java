package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.MaterielFixeDb;
import com.example.meetingplanner.entity.SalleDb;
import com.example.meetingplanner.entity.TypeMaterielDb;
import com.example.meetingplanner.model.Materiel;
import com.example.meetingplanner.model.Salle;
import com.example.meetingplanner.model.TypeMateriel;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SalleConverterTest {

  @Test
  void fromDb() {

    // [GIVEN] entities from db

    TypeMaterielDb typeMaterielDb = new TypeMaterielDb();
    typeMaterielDb.setId(1);
    typeMaterielDb.setNom("nom type materiel");

    MaterielFixeDb materielFixeDb = new MaterielFixeDb();
    materielFixeDb.setId(2);
    materielFixeDb.setTypeMateriel(typeMaterielDb);

    SalleDb salleDb = new SalleDb();
    salleDb.setId(4);
    salleDb.setNom("nom salle");
    salleDb.setCapacite(100);
    salleDb.setMaterielsFixes(Set.of(materielFixeDb));

    // [WHEN] converting

    Salle salle = SalleConverter.fromDb(salleDb);

    // [THEN] expected values in converted result

    Salle expected =
        Salle.builder()
            .id(4)
            .nom("nom salle")
            .capacite(100)
            .materiels(
                Set.of(
                    Materiel.builder()
                        .id(2)
                        .mobile(false)
                        .typeMateriel(TypeMateriel.builder().id(1).nom("nom type materiel").build())
                        .build()))
            .build();
    assertThat(salle).isEqualTo(expected);
  }
}
