package com.example.shiftroster.persistance.primary.repository;

import com.example.shiftroster.persistance.Enum.EnumRole;
import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.primary.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface EmployeeRepo extends JpaRepository<EmployeeEntity,Integer> {
    Optional<EmployeeEntity> findByIdAndEmpStatus(Integer empId, EnumStatus enumStatus);

    List<EmployeeEntity> findAllByRoleAndEmpStatus(EnumRole enumRole, EnumStatus enumStatus);

    List<EmployeeEntity> findAllByRoleAndEmpStatusAndAppraiserId(EnumRole enumRole, EnumStatus enumStatus, Integer id);
}
