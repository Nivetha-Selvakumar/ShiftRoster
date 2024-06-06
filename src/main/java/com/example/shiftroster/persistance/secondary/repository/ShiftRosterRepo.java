package com.example.shiftroster.persistance.secondary.repository;

import com.example.shiftroster.persistance.secondary.entity.ShiftRosterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface ShiftRosterRepo extends JpaRepository<ShiftRosterEntity,Integer> {
}
