package com.example.shiftroster.persistance.primary.repository;

import com.example.shiftroster.persistance.primary.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface EmployeeRepo extends JpaRepository<EmployeeEntity,Integer> {
}
