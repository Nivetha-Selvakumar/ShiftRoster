package com.example.shiftroster.persistance.validation;

import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.Exception.CommonException;
import com.example.shiftroster.persistance.Exception.NotFoundException;
import com.example.shiftroster.persistance.primary.entity.EmployeeEntity;
import com.example.shiftroster.persistance.primary.repository.EmployeeRepo;
import com.example.shiftroster.persistance.secondary.entity.ShiftEntity;
import com.example.shiftroster.persistance.secondary.entity.ShiftRosterEntity;
import com.example.shiftroster.persistance.secondary.repository.ShiftRepo;
import com.example.shiftroster.persistance.secondary.repository.ShiftRosterRepo;
import com.example.shiftroster.persistance.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BusinessValidation {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    ShiftRosterRepo shiftRosterRepo;


    public EmployeeEntity employeeValidation(String empId) throws CommonException {
        return employeeRepo.findByIdAndEmpStatus(Integer.valueOf(empId), EnumStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException(AppConstant.EMPLOYEE_NOT_FOUND));
    }

    public List<ShiftRosterEntity> getShiftRosterList(int currentMonth, int currentYear) {
        List<ShiftRosterEntity> shiftRosterEntityList = shiftRosterRepo.findAllByMonthAndYear(currentMonth, currentYear);
        if(shiftRosterEntityList != null){
            return shiftRosterEntityList;
        }else{
            return null;
        }
    }
}
