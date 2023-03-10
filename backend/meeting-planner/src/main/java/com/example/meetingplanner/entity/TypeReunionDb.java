package com.example.meetingplanner.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "type_reunion")
public class TypeReunionDb {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "nom", nullable = false)
  private String nom;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "type_reunion_materiel_requis",
      joinColumns = {@JoinColumn(name = "id_type_reunion")},
      inverseJoinColumns = {@JoinColumn(name = "id_type_materiel")})
  private Set<TypeMaterielDb> typeMaterielRequis;
}
