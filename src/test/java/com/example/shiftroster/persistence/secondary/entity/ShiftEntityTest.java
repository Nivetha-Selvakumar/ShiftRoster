package com.example.shiftroster.persistence.secondary.entity;

import com.example.shiftroster.persistence.Enum.EnumStatus;
import com.example.shiftroster.persistence.Enum.ShiftType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.sql.Timestamp;

@ExtendWith(MockitoExtension.class)
class ShiftEntityTest {
    ShiftEntity shiftEntity = new ShiftEntity();

    @Test
    void shiftEntityTest() {
        shiftEntity.setId(1);
        shiftEntity.setShiftType(ShiftType.DAY);
        shiftEntity.setToTime(Time.valueOf("00:00:00"));
        shiftEntity.setFromTime(Time.valueOf("00:00:00"));
        shiftEntity.setAllowedInTime(Time.valueOf("00:00:00"));
        shiftEntity.setAllowedOutTime(Time.valueOf("00:00:00"));
        shiftEntity.setShiftName("ABC");
        shiftEntity.setStatus(EnumStatus.ACTIVE);
        shiftEntity.setCreatedBy("nive");
        shiftEntity.setUpdatedBy("nive");
        shiftEntity.setCreatedDate(Timestamp.valueOf("2024-09-09 00:00:00"));
        shiftEntity.setUpdatedDate(Timestamp.valueOf("2024-09-09 00:00:00"));

        int id = shiftEntity.getId();
        ShiftType shiftType = shiftEntity.getShiftType();
        Time allowedInTime = shiftEntity.getAllowedInTime();
        Time allowedOutTime = shiftEntity.getAllowedOutTime();
        Time startTime = shiftEntity.getFromTime();
        Time endTime = shiftEntity.getToTime();
        String name = shiftEntity.getShiftName();
        EnumStatus status = shiftEntity.getStatus();
        String createdBy = shiftEntity.getCreatedBy();
        String updatedBy = shiftEntity.getUpdatedBy();
        Timestamp createdDate = shiftEntity.getCreatedDate();
        Timestamp updatedDate = shiftEntity.getUpdatedDate();

        Assertions.assertEquals(id, shiftEntity.getId());
        Assertions.assertEquals(shiftType, shiftEntity.getShiftType());
        Assertions.assertEquals(allowedInTime, shiftEntity.getAllowedInTime());
        Assertions.assertEquals(allowedOutTime, shiftEntity.getAllowedOutTime());
        Assertions.assertEquals(startTime, shiftEntity.getFromTime());
        Assertions.assertEquals(endTime, shiftEntity.getToTime());
        Assertions.assertEquals(name, shiftEntity.getShiftName());
        Assertions.assertEquals(status, shiftEntity.getStatus());
        Assertions.assertEquals(createdBy, shiftEntity.getCreatedBy());
        Assertions.assertEquals(updatedBy, shiftEntity.getUpdatedBy());
        Assertions.assertEquals(createdDate, shiftEntity.getCreatedDate());
        Assertions.assertEquals(updatedDate, shiftEntity.getUpdatedDate());
    }
}
