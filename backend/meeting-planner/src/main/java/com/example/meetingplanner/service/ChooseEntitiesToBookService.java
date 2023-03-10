package com.example.meetingplanner.service;

import com.example.meetingplanner.model.EntitiesToBook;
import com.example.meetingplanner.model.Materiel;
import com.example.meetingplanner.model.Salle;
import com.example.meetingplanner.model.TypeMateriel;
import com.example.meetingplanner.service.db.TypeReunionDbService;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ChooseEntitiesToBookService {

  private final TypeReunionDbService typeReunionDbService;
  private final FindAvailableSalleService findAvailableSalleService;
  private final FindAvailableMaterielService findAvailableMaterielService;

  @Nullable
  public EntitiesToBook choose(
      Integer idTypeReunion, Integer nombrePersonne, Instant debut, Instant fin) {
    Set<Integer> idTypeMaterielRequis =
        typeReunionDbService.fetchAllTypeMaterielRequis(idTypeReunion).stream()
            .map(TypeMateriel::getId)
            .collect(Collectors.toSet());
    Set<Salle> salles = findAvailableSalleService.find(nombrePersonne, debut, fin);
    Set<Materiel> materiels = findAvailableMaterielService.find(idTypeMaterielRequis, debut, fin);
    return chooseFromAvailableEntities(idTypeMaterielRequis, salles, materiels);
  }

  @Nullable
  private EntitiesToBook chooseFromAvailableEntities(
      Set<Integer> idTypeMaterielRequis, Set<Salle> salles, Set<Materiel> materiels) {

    // Types de matériel requis dans la salle, car non disponible parmi le matériel mobile
    Set<Integer> idTypeMaterielFixeRequis =
        findIdTypeMaterielFixeRequis(idTypeMaterielRequis, materiels);

    Optional<Salle> bestSalle =
        salles.stream()
            // Salles pouvant respecter le type de réunion en fonction du matériel fixe présent et
            // du matériel mobile disponible
            .filter(salle -> hasAllRequiredMateriel(salle, idTypeMaterielFixeRequis))
            // Salle ayant la plus petite capacité
            .min(Comparator.comparing(Salle::getCapacite));

    return bestSalle
        .map(
            salle ->
                EntitiesToBook.builder()
                    .salle(salle)
                    .materielsMobiles(chooseMaterielMobile(salle, idTypeMaterielRequis, materiels))
                    .build())
        .orElse(null);
  }

  private Set<Integer> findIdTypeMaterielFixeRequis(
      Set<Integer> idTypeMaterielRequis, Set<Materiel> materiels) {
    // Types de matériel mobile disponibles
    Set<Integer> idTypeMaterielMobileDisponible =
        materiels.stream()
            .map(Materiel::getTypeMateriel)
            .map(TypeMateriel::getId)
            .collect(Collectors.toSet());
    // Types de matériel fixe requis dans la salle, car non disponible parmi le matériel mobile
    return Sets.difference(idTypeMaterielRequis, idTypeMaterielMobileDisponible);
  }

  private Set<Integer> findIdTypeMaterielMobileRequis(
      Set<Integer> idTypeMaterielRequis, Salle salle) {
    // Types de matériel fixe disponibles
    Set<Integer> idTypeMaterielFixeDisponible =
        salle.getMateriels().stream()
            .map(Materiel::getTypeMateriel)
            .map(TypeMateriel::getId)
            .collect(Collectors.toSet());
    // Types de matériel mobile requis, car non disponible parmi le matériel fixe présent dans la
    // salle
    return Sets.difference(idTypeMaterielRequis, idTypeMaterielFixeDisponible);
  }

  private boolean hasAllRequiredMateriel(Salle salle, Set<Integer> idTypeMaterielFixeRequis) {
    return salle.getMateriels().stream()
        .map(Materiel::getTypeMateriel)
        .map(TypeMateriel::getId)
        .collect(Collectors.toSet())
        .containsAll(idTypeMaterielFixeRequis);
  }

  private Set<Materiel> chooseMaterielMobile(
      Salle salle, Set<Integer> idTypeMaterielRequis, Set<Materiel> materiels) {

    // Type matériel mobile requis
    return findIdTypeMaterielMobileRequis(idTypeMaterielRequis, salle).stream()
        .map(
            // Recupère pour chaque type de materiel, un materiel mobile quelconque de ce type
            idTypeMateriel ->
                materiels.stream()
                    .filter(materiel -> idTypeMateriel.equals(materiel.getTypeMateriel().getId()))
                    .findAny()
                    .orElseThrow())
        .collect(Collectors.toSet());
  }
}
