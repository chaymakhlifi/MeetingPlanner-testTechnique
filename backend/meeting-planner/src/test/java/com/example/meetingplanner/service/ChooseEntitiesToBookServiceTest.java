package com.example.meetingplanner.service;

import com.example.meetingplanner.model.EntitiesToBook;
import com.example.meetingplanner.model.Materiel;
import com.example.meetingplanner.model.Salle;
import com.example.meetingplanner.model.TypeMateriel;
import com.example.meetingplanner.service.db.TypeReunionDbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

/** Unit test for {@link ChooseEntitiesToBookService} */
@ExtendWith(MockitoExtension.class)
public class ChooseEntitiesToBookServiceTest {

  @Mock private TypeReunionDbService typeReunionDbService;
  @Mock private FindAvailableSalleService findAvailableSalleService;
  @Mock private FindAvailableMaterielService findAvailableMaterielService;
  @InjectMocks private ChooseEntitiesToBookService service;

  /** Unit test for method {@link ChooseEntitiesToBookService#choose} */
  @Nested
  class Choose {

    private final Integer ID_TYPE_REUNION = 1;
    private final Integer NOMBRE_PERSONNE = 10;
    private final Instant DEBUT = Instant.EPOCH;
    private final Instant FIN = Instant.EPOCH.plus(1, DAYS);

    @BeforeEach
    void initDefaultMock() {
      // [GIVEN] TypeReunionDbService returning no TypeMateriel
      lenient().doReturn(Set.of()).when(typeReunionDbService).fetchAllTypeMaterielRequis(any());

      // [GIVEN] FindAvailableSalleService returning no salle
      lenient().doReturn(Set.of()).when(findAvailableSalleService).find(any(), any(), any());

      // [GIVEN] FindAvailableMaterielService returning no materiel
      lenient().doReturn(Set.of()).when(findAvailableMaterielService).find(any(), any(), any());
    }

    // Pas de salle trouvée -> null
    @Test
    void notEnoughSpace() {
      // [WHEN] Calling method
      EntitiesToBook result = service.choose(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN);
      // [THEN] No result
      assertThat(result).isNull();
    }

    // Plusieurs salle de capacite suffisante + pas de matériel requis -> salle de plus petite
    // taille
    @Test
    void multipleEnoughSpace() {
      // [GIVEN] Multiple possible rooms
      Salle salle1 = Salle.builder().id(1).capacite(12).materiels(Set.of()).build();
      Salle salle2 = Salle.builder().id(2).capacite(20).materiels(Set.of()).build();
      lenient()
          .doReturn(Set.of(salle1, salle2))
          .when(findAvailableSalleService)
          .find(any(), any(), any());
      // [WHEN] Calling method
      EntitiesToBook result = service.choose(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN);
      // [THEN] Result has smallest capacity
      EntitiesToBook expected =
          EntitiesToBook.builder().salle(salle1).materielsMobiles(Set.of()).build();
      assertThat(result).isEqualTo(expected);
    }

    // Plusieurs salle de capacité suffisante + matériel requis (présent dans salle mais pas dans
    // matériel mobile) -> trouve bonne salle
    @Test
    void requiredMaterielInMaterielFixe() {
      // [GIVEN] Materiel requis
      TypeMateriel typeMaterielRequis = TypeMateriel.builder().id(3).build();
      lenient()
          .doReturn(Set.of(typeMaterielRequis))
          .when(typeReunionDbService)
          .fetchAllTypeMaterielRequis(ID_TYPE_REUNION);
      // [GIVEN] Multiple rooms, only one with equipment
      Salle salle1 = Salle.builder().id(1).capacite(12).materiels(Set.of()).build();
      Salle salle2 =
          Salle.builder()
              .id(2)
              .capacite(20)
              .materiels(Set.of(Materiel.builder().id(4).typeMateriel(typeMaterielRequis).build()))
              .build();
      lenient()
          .doReturn(Set.of(salle1, salle2))
          .when(findAvailableSalleService)
          .find(any(), any(), any());
      // [WHEN] Calling method
      EntitiesToBook result = service.choose(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN);
      // [THEN] Result has room with required equipment
      EntitiesToBook expected =
          EntitiesToBook.builder().salle(salle2).materielsMobiles(Set.of()).build();
      assertThat(result).isEqualTo(expected);
    }

    // Salle capacité suffisante + matériel requis (pas présent dans salle, mais présent dans
    // matériel mobile) -> trouve bon matériel mobile
    @Test
    void requiredMaterielInMaterielMobile() {
      // [GIVEN] Materiel requis
      TypeMateriel typeMaterielRequis = TypeMateriel.builder().id(1).build();
      lenient()
          .doReturn(Set.of(typeMaterielRequis))
          .when(typeReunionDbService)
          .fetchAllTypeMaterielRequis(ID_TYPE_REUNION);
      // [GIVEN] Salle
      Salle salle = Salle.builder().id(1).capacite(10).materiels(Set.of()).build();
      lenient().doReturn(Set.of(salle)).when(findAvailableSalleService).find(any(), any(), any());
      // [GIVEN] Materiel mobile
      TypeMateriel typeMaterielAutre = TypeMateriel.builder().id(2).build();
      Materiel materiel1 = Materiel.builder().id(1).typeMateriel(typeMaterielRequis).build();
      Materiel materiel2 = Materiel.builder().id(2).typeMateriel(typeMaterielAutre).build();
      lenient()
          .doReturn(Set.of(materiel1, materiel2))
          .when(findAvailableMaterielService)
          .find(any(), any(), any());

      // [WHEN] Calling method
      EntitiesToBook result = service.choose(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN);

      // [THEN] Result has right equipment
      EntitiesToBook expected =
          EntitiesToBook.builder().salle(salle).materielsMobiles(Set.of(materiel1)).build();
      assertThat(result).isEqualTo(expected);
    }

    // Idem précédent, mais partiellement présent dans salle et dans matériel mobile -> ne prend que
    // le matériel nécessaire
    @Test
    void requiredMaterielInBothFixeAndMobile() {
      // [GIVEN] 2 required equipement
      TypeMateriel typeMaterielRequis1 = TypeMateriel.builder().id(1).build();
      TypeMateriel typeMaterielRequis2 = TypeMateriel.builder().id(2).build();
      lenient()
          .doReturn(Set.of(typeMaterielRequis1, typeMaterielRequis2))
          .when(typeReunionDbService)
          .fetchAllTypeMaterielRequis(ID_TYPE_REUNION);
      // [GIVEN] Multiple rooms, only one with first required type equipment
      Salle salle1 = Salle.builder().id(1).capacite(12).materiels(Set.of()).build();
      Salle salle2 =
          Salle.builder()
              .id(2)
              .capacite(20)
              .materiels(Set.of(Materiel.builder().id(4).typeMateriel(typeMaterielRequis1).build()))
              .build();
      lenient()
          .doReturn(Set.of(salle1, salle2))
          .when(findAvailableSalleService)
          .find(any(), any(), any());
      // [GIVEN] Multiple equipments, only one with second required type equipment
      TypeMateriel typeMaterielAutre = TypeMateriel.builder().id(3).build();
      Materiel materiel1 = Materiel.builder().id(1).typeMateriel(typeMaterielRequis2).build();
      Materiel materiel2 = Materiel.builder().id(2).typeMateriel(typeMaterielAutre).build();
      lenient()
          .doReturn(Set.of(materiel1, materiel2))
          .when(findAvailableMaterielService)
          .find(any(), any(), any());

      // [WHEN] Calling method
      EntitiesToBook result = service.choose(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN);

      // [THEN] Result has set of room + equipment satisfying required types
      EntitiesToBook expected =
          EntitiesToBook.builder().salle(salle2).materielsMobiles(Set.of(materiel1)).build();
      assertThat(result).isEqualTo(expected);
    }

    // Idem précédent, mais pas de solution existente pour satisfaire la contrainte de matériel ->
    // null
    @Test
    void requiredMaterielNotFound() {
      // [GIVEN] 2 required equipement
      TypeMateriel typeMaterielRequis1 = TypeMateriel.builder().id(1).build();
      TypeMateriel typeMaterielRequis2 = TypeMateriel.builder().id(2).build();
      lenient()
          .doReturn(Set.of(typeMaterielRequis1, typeMaterielRequis2))
          .when(typeReunionDbService)
          .fetchAllTypeMaterielRequis(ID_TYPE_REUNION);
      // [GIVEN] Room cannot provide second type
      Salle salle = Salle.builder().id(1).capacite(10).materiels(Set.of()).build();
      lenient().doReturn(Set.of(salle)).when(findAvailableSalleService).find(any(), any(), any());
      // [GIVEN] Mobile equipment cannot provide second type
      Materiel materiel = Materiel.builder().id(1).typeMateriel(typeMaterielRequis1).build();
      lenient()
          .doReturn(Set.of(materiel))
          .when(findAvailableMaterielService)
          .find(any(), any(), any());

      // [WHEN] Calling method
      EntitiesToBook result = service.choose(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN);

      // [THEN] No solution found
      assertThat(result).isNull();
    }

    // Plusieurs solutions possibles -> trouve la "meilleure" (version 1 : occuper le moins
    // d'espace)
    @Test
    void bestAmongMultipleSolution() {
      // [GIVEN] 2 required equipement
      TypeMateriel typeMaterielRequis1 = TypeMateriel.builder().id(1).build();
      TypeMateriel typeMaterielRequis2 = TypeMateriel.builder().id(2).build();
      TypeMateriel typeMaterielAutre = TypeMateriel.builder().id(3).build();
      lenient()
          .doReturn(Set.of(typeMaterielRequis1, typeMaterielRequis2))
          .when(typeReunionDbService)
          .fetchAllTypeMaterielRequis(ID_TYPE_REUNION);
      // [GIVEN] Multiple rooms
      Salle salle1 = Salle.builder().id(1).capacite(12).materiels(Set.of()).build();
      Salle salle2 =
          Salle.builder()
              .id(2)
              .capacite(14)
              .materiels(Set.of(Materiel.builder().id(4).typeMateriel(typeMaterielRequis1).build()))
              .build();
      Salle salle3 =
          Salle.builder()
              .id(3)
              .capacite(20)
              .materiels(
                  Set.of(
                      Materiel.builder().id(5).typeMateriel(typeMaterielRequis1).build(),
                      Materiel.builder().id(6).typeMateriel(typeMaterielRequis2).build()))
              .build();
      lenient()
          .doReturn(Set.of(salle1, salle2, salle3))
          .when(findAvailableSalleService)
          .find(any(), any(), any());
      // [GIVEN] Multiple equipments
      Materiel materiel1 = Materiel.builder().id(1).typeMateriel(typeMaterielRequis1).build();
      Materiel materiel2 = Materiel.builder().id(2).typeMateriel(typeMaterielRequis2).build();
      Materiel materiel3 = Materiel.builder().id(3).typeMateriel(typeMaterielAutre).build();
      lenient()
          .doReturn(Set.of(materiel1, materiel2, materiel3))
          .when(findAvailableMaterielService)
          .find(any(), any(), any());
      // --> multiple solutions exists:
      // salle1 + materiel1 + materiel2
      // salle2 + materiel2
      // salle3

      // [WHEN] Calling method
      EntitiesToBook result = service.choose(ID_TYPE_REUNION, NOMBRE_PERSONNE, DEBUT, FIN);

      // [THEN] Result has the "best" solution
      // (version 1 : smallest room used)
      EntitiesToBook expected =
          EntitiesToBook.builder()
              .salle(salle1)
              .materielsMobiles(Set.of(materiel1, materiel2))
              .build();
      assertThat(result).isEqualTo(expected);
    }
  }
}
