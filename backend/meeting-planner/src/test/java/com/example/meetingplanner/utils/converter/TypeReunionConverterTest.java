package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.TypeMaterielDb;
import com.example.meetingplanner.entity.TypeReunionDb;
import com.example.meetingplanner.model.TypeReunion;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeReunionConverterTest {

  @Test
  void fromDb() {

    // [GIVEN] entities from db

    TypeMaterielDb typeMaterielDb = new TypeMaterielDb();
    typeMaterielDb.setId(1);
    typeMaterielDb.setNom("nom type materiel");

    TypeReunionDb typeReunionDb = new TypeReunionDb();
    typeReunionDb.setId(2);
    typeReunionDb.setNom("nom type reunion");
    typeReunionDb.setTypeMaterielRequis(Set.of(typeMaterielDb));

    // [WHEN] converting

    TypeReunion typeReunion = TypeReunionConverter.fromDb(typeReunionDb);

    // [THEN] expected values in converted result

    TypeReunion expected = TypeReunion.builder().id(2).nom("nom type reunion").build();
    assertThat(typeReunion).isEqualTo(expected);
  }
}
