package com.example.project.projectbackend.repository;

import com.example.project.projectbackend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}