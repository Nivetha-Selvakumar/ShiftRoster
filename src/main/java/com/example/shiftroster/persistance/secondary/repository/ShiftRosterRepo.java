package com.example.shiftroster.persistance.secondary.repository;

import com.example.shiftroster.persistance.secondary.entity.ShiftRosterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface ShiftRosterRepo extends JpaRepository<ShiftRosterEntity,Integer> {

    List<ShiftRosterEntity> findAllByMonthAndYear(int month, int year);

    Optional<ShiftRosterEntity> findByEmpIdAndMonthAndYear(int integer, int month, int year);
}
