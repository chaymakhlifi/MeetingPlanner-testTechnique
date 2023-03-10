package com.example.meetingplanner.repository;

import com.example.meetingplanner.entity.ReservationDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReservationRepository
    extends JpaRepository<ReservationDb, Integer>, JpaSpecificationExecutor<ReservationDb> {}
