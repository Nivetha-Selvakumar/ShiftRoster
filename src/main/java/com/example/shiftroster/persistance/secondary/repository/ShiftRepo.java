package com.example.shiftroster.persistance.secondary.repository;


import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.secondary.entity.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface ShiftRepo extends JpaRepository<ShiftEntity,Integer> {

    Optional<ShiftEntity> findByShiftNameAndStatus(String shift, EnumStatus enumStatus);

//    List<ShiftEntity> findByStatus(EnumStatus enumStatus);
}
