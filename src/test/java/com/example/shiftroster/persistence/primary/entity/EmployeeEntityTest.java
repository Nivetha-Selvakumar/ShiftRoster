package com.example.shiftroster.persistence.primary.entity;

import com.example.shiftroster.persistence.Enum.EnumRole;
import com.example.shiftroster.persistence.Enum.EnumStatus;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EmployeeEntityTest {

    EmployeeEntity employeeEntity = new EmployeeEntity();

    @Test
    public void testEmployee(){

        employeeEntity.setId(1);
        employeeEntity.setEmpName("nive");
        employeeEntity.setRole(EnumRole.EMPLOYEE);
        employeeEntity.setEmail("nive@gmail.com");
        employeeEntity.setEmpCode("s23");
        employeeEntity.setEmpStatus(EnumStatus.ACTIVE);
        employeeEntity.setAppraiserId(employeeEntity);

        int id = employeeEntity.getId();
        String empName = employeeEntity.getEmpName();
        EnumStatus enumStatus = employeeEntity.getEmpStatus();
        EnumRole enumRole = employeeEntity.getRole();
        String email = employeeEntity.getEmail();
        String empCode = employeeEntity.getEmpCode();
        EmployeeEntity appraiser  = employeeEntity.getAppraiserId();

        Assertions.assertEquals(id,employeeEntity.getId());
        Assertions.assertEquals(empName,employeeEntity.getEmpName());
        Assertions.assertEquals(enumStatus,employeeEntity.getEmpStatus());
        Assertions.assertEquals(enumRole,employeeEntity.getRole());
        Assertions.assertEquals(email,employeeEntity.getEmail());
        Assertions.assertEquals(empCode,employeeEntity.getEmpCode());
        Assertions.assertEquals(appraiser,employeeEntity.getAppraiserId());
    }
}

