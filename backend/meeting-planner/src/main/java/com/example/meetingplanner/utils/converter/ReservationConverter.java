package com.example.meetingplanner.utils.converter;

import com.example.meetingplanner.entity.ReservationDb;
import com.example.meetingplanner.model.Reservation;

import java.util.stream.Collectors;

public class ReservationConverter {

    public static Reservation fromDb(ReservationDb reservationDb) {
        return Reservation
                .builder()
                .id(reservationDb.getId())
                .idReservateur(reservationDb.getIdReservateur())
                .typeReunion(
                        TypeReunionConverter.fromDb(reservationDb.getTypeReunion())
                )
                .salle(
                        SalleConverter.fromDb(reservationDb.getSalle())
                )
                .materielsMobiles(
                        reservationDb
                                .getMaterielReserve()
                                .stream()
                                .map(MaterielConverter::fromDbMobile)
                                .collect(Collectors.toSet())
                )
                .debut(reservationDb.getDatetimeDebut())
                .fin(reservationDb.getDatetimeFin())
                .build();
    }
}
