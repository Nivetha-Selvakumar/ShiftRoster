package com.example.shiftroster.persistance.secondary.repository;

import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.secondary.entity.ShiftRosterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface ShiftRosterRepo extends JpaRepository<ShiftRosterEntity,Integer> {

    List<ShiftRosterEntity> findAllByMonthAndYear(int month, int year);
}
