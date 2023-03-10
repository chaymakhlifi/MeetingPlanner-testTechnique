package com.example.meetingplanner.repository;

import com.example.meetingplanner.entity.TypeReunionDb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeReunionRepository extends JpaRepository<TypeReunionDb, Integer> {}
