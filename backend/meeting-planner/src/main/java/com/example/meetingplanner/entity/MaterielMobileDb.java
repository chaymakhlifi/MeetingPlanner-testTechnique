package com.example.meetingplanner.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "materiel_mobile")
public class MaterielMobileDb {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * Type de matériel
   *
   * <p>Ex : Ecran, Pieuvre, etc...
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_type_materiel", nullable = false)
  private TypeMaterielDb typeMateriel;
}
