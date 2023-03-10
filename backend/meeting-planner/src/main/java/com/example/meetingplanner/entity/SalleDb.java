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
@Table(name = "salle")
public class SalleDb {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "nom", nullable = false)
  private String nom;

  @Column(name = "capacite", nullable = false)
  private Integer capacite;

  @OneToMany(mappedBy = "salle", fetch = FetchType.LAZY)
  private Set<MaterielFixeDb> materielsFixes;
}
