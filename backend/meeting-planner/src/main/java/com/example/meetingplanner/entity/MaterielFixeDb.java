package com.example.meetingplanner.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "materiel_fixe")
public class MaterielFixeDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Type de matériel
     * <p>
     * Ex : Ecran, Pieuvre, etc...
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_materiel", nullable = false)
    private TypeMaterielDb typeMateriel;

    /**
     * Salle dans laquel ce matériel est présent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_salle", nullable = false)
    private SalleDb salle;
}
