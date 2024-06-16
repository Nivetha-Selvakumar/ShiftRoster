package com.example.shiftroster.persistence.secondary.repository;

import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.secondary.entity.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface ShiftRepo extends JpaRepository<ShiftEntity,Integer> {

    Optional<ShiftEntity> findByShiftNameAndStatus(String shift, EnumStatus enumStatus);

}
