package com.example.meetingplanner.repository;

import com.example.meetingplanner.entity.MaterielMobileDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MaterielMobileRepository
    extends JpaRepository<MaterielMobileDb, Integer>, JpaSpecificationExecutor<MaterielMobileDb> {}
