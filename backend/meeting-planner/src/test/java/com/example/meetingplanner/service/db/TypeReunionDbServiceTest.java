package com.example.meetingplanner.service.db;

import com.example.meetingplanner.AbstractIntegrationTest;
import com.example.meetingplanner.entity.TypeMaterielDb;
import com.example.meetingplanner.entity.TypeReunionDb;
import com.example.meetingplanner.model.TypeMateriel;
import com.example.meetingplanner.model.TypeReunion;
import com.example.meetingplanner.repository.TypeMaterielRepository;
import com.example.meetingplanner.repository.TypeReunionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/** Test for {@link TypeReunionDbService} */
@SpringBootTest
public class TypeReunionDbServiceTest extends AbstractIntegrationTest {

  @Autowired private TypeReunionRepository typeReunionRepository;
  @Autowired private TypeMaterielRepository typeMaterielRepository;
  @Autowired private TypeReunionDbService service;

  @BeforeEach
  void clearDb() {
    typeReunionRepository.deleteAll();
    typeMaterielRepository.deleteAll();
  }

  /** Unit test for method {@link TypeReunionDbService#fetchAll} */
  @Nested
  class FetchAll {

    @Test
    void whenNoResult() {
      // [GIVEN] No entity in db
      assertThat(typeReunionRepository.count()).isEqualTo(0);
      // [WHEN] Calling method
      Set<TypeReunion> result = service.fetchAll();
      // [THEN] Empty result
      assertThat(result).isEmpty();
    }

    @Test
    void whenSomeResult() {
      // [GIVEN] Some entities in db
      TypeReunionDb typeReunionDb1 = new TypeReunionDb();
      typeReunionDb1.setNom("nom 1");
      Integer id1 = typeReunionRepository.save(typeReunionDb1).getId();
      TypeReunionDb typeReunionDb2 = new TypeReunionDb();
      typeReunionDb2.setId(2);
      typeReunionDb2.setNom("nom 2");
      Integer id2 = typeReunionRepository.save(typeReunionDb2).getId();
      assertThat(typeReunionRepository.count()).isEqualTo(2);
      // [WHEN] Calling method
      Set<TypeReunion> result = service.fetchAll();
      // [THEN] Found 2 expected entities
      TypeReunion expected1 = TypeReunion.builder().id(id1).nom("nom 1").build();
      TypeReunion expected2 = TypeReunion.builder().id(id2).nom("nom 2").build();
      assertThat(result).containsExactlyInAnyOrder(expected1, expected2);
    }
  }

  /** Unit test for method {@link TypeReunionDbService#fetchAllTypeMaterielRequis} */
  @Nested
  class FetchAllTypeMaterielRequis {

    @Test
    void whenNotFound() {
      // [GIVEN] No entity in db
      assertThat(typeReunionRepository.count()).isEqualTo(0);
      // [WHEN] Calling method for an id
      Set<TypeMateriel> result = service.fetchAllTypeMaterielRequis(1);
      // [THEN] Result is empty
      // TODO: should throw instead?
      assertThat(result).isEmpty();
    }

    @Test
    void whenFoundWithoutTypeMaterielRequis() {
      // [GIVEN] An entity without type materiel
      TypeReunionDb typeReunionDb = new TypeReunionDb();
      typeReunionDb.setNom("nom");
      typeReunionDb.setTypeMaterielRequis(Set.of());
      Integer id = typeReunionRepository.save(typeReunionDb).getId();
      assertThat(typeReunionRepository.count()).isEqualTo(1);
      // [WHEN] Calling method for this id
      Set<TypeMateriel> result = service.fetchAllTypeMaterielRequis(id);
      // [THEN] Result is empty
      assertThat(result).isEmpty();
    }

    @Test
    void whenFoundWithSomeTypeMaterielRequis() {
      // [GIVEN] 2 type materiel in db
      TypeMaterielDb typeMaterielDb1 = new TypeMaterielDb();
      typeMaterielDb1.setNom("nom materiel 1");
      typeMaterielDb1 = typeMaterielRepository.save(typeMaterielDb1);
      TypeMaterielDb typeMaterielDb2 = new TypeMaterielDb();
      typeMaterielDb2.setNom("nom materiel 2");
      typeMaterielDb2 = typeMaterielRepository.save(typeMaterielDb2);
      // [GIVEN] An entity with some type materiel for this id
      TypeReunionDb typeReunionDb = new TypeReunionDb();
      typeReunionDb.setNom("nom type reunion");
      typeReunionDb.setTypeMaterielRequis(Set.of(typeMaterielDb1, typeMaterielDb2));
      Integer idTypeReunion = typeReunionRepository.save(typeReunionDb).getId();

      assertThat(typeMaterielRepository.count()).isEqualTo(2);
      assertThat(typeReunionRepository.count()).isEqualTo(1);

      // [WHEN] Calling method for this id
      Set<TypeMateriel> result = service.fetchAllTypeMaterielRequis(idTypeReunion);

      // [THEN] Result is empty
      TypeMateriel expected1 =
          TypeMateriel.builder().id(typeMaterielDb1.getId()).nom("nom materiel 1").build();
      TypeMateriel expected2 =
          TypeMateriel.builder().id(typeMaterielDb2.getId()).nom("nom materiel 2").build();
      assertThat(result).containsExactlyInAnyOrder(expected1, expected2);
    }
  }
}
