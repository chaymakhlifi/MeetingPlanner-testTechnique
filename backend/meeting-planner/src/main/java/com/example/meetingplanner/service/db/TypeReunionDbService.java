package com.example.meetingplanner.service.db;

import com.example.meetingplanner.entity.TypeReunionDb;
import com.example.meetingplanner.model.TypeMateriel;
import com.example.meetingplanner.model.TypeReunion;
import com.example.meetingplanner.repository.TypeReunionRepository;
import com.example.meetingplanner.utils.converter.TypeMaterielConverter;
import com.example.meetingplanner.utils.converter.TypeReunionConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TypeReunionDbService {

  private final TypeReunionRepository typeReunionRepository;

  @Transactional
  public Set<TypeReunion> fetchAll() {
    return typeReunionRepository.findAll().stream()
        .map(TypeReunionConverter::fromDb)
        .collect(Collectors.toSet());
  }

  @Transactional
  public Set<TypeMateriel> fetchAllTypeMaterielRequis(Integer idTypeReunion) {
    return typeReunionRepository
        .findById(idTypeReunion)
        .map(TypeReunionDb::getTypeMaterielRequis)
        .map(
            typeMaterielDbs ->
                typeMaterielDbs.stream()
                    .map(TypeMaterielConverter::fromDb)
                    .collect(Collectors.toSet()))
        .orElse(Collections.emptySet());
  }
}
