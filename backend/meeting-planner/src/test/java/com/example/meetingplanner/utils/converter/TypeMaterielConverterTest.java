package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.TypeMaterielDb;
import com.example.meetingplanner.model.TypeMateriel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeMaterielConverterTest {

  @Test
  void fromDb() {

    // [GIVEN] entities from db

    TypeMaterielDb typeMaterielDb = new TypeMaterielDb();
    typeMaterielDb.setId(2);
    typeMaterielDb.setNom("nom type materiel");

    // [WHEN] converting

    TypeMateriel typeMateriel = TypeMaterielConverter.fromDb(typeMaterielDb);

    // [THEN] expected values in converted result

    TypeMateriel expected = TypeMateriel.builder().id(2).nom("nom type materiel").build();
    assertThat(typeMateriel).isEqualTo(expected);
  }
}
