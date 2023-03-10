package com.example.meetingplanner.repository;

import com.example.meetingplanner.entity.SalleDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SalleRepository
    extends JpaRepository<SalleDb, Integer>, JpaSpecificationExecutor<SalleDb> {}
