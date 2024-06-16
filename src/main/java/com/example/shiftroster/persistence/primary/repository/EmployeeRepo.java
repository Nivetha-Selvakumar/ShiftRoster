package com.example.shiftroster.persistence.primary.repository;

import com.example.shiftroster.persistence.Enum.EnumRole;
import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.primary.entity.EmployeeEntity;
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

    List<EmployeeEntity> findAllByRoleAndEmpStatusAndAppraiserId(EnumRole enumRole, EnumStatus enumStatus, EmployeeEntity id);
}
