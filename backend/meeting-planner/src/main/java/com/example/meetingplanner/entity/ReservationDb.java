package com.example.meetingplanner.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reservation")
public class ReservationDb {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "id_reservateur", nullable = false)
  private String idReservateur;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_salle", nullable = false)
  private SalleDb salle;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_type_reunion", nullable = false)
  private TypeReunionDb typeReunion;

  @Column(name = "datetime_debut", nullable = false)
  private Instant datetimeDebut;

  @Column(name = "datetime_fin", nullable = false)
  private Instant datetimeFin;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "reservation_materiel",
      joinColumns = {@JoinColumn(name = "id_reservation")},
      inverseJoinColumns = {@JoinColumn(name = "id_materiel_mobile")})
  private Set<MaterielMobileDb> materielReserve;
}
