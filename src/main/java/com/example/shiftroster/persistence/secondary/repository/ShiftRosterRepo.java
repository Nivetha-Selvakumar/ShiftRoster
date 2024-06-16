package com.example.shiftroster.persistence.secondary.repository;

import com.example.shiftroster.persistence.secondary.entity.ShiftRosterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface ShiftRosterRepo extends JpaRepository<ShiftRosterEntity,Integer> {


    Optional<ShiftRosterEntity> findByEmpIdAndMonthAndYear(int integer, int month, int year);

    List<ShiftRosterEntity> findAllByEmpIdInAndMonthAndYear(List<Integer> employeeEntityList, int currentMonth, int currentYear);
}
