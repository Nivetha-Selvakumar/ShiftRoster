package com.example.shiftroster.persistence.secondary.entity;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ShiftRosterEnityTest {

    ShiftRosterEntity shiftRosterEntity = new ShiftRosterEntity();

    @Test
    public void testShiftRoster(){
        shiftRosterEntity.setId(1);
        shiftRosterEntity.setEmpId(1);
        shiftRosterEntity.setYear(1);
        shiftRosterEntity.setMonth(1);
        shiftRosterEntity.setCreatedDate(Timestamp.valueOf("2024-09-09 00:00:00"));
        shiftRosterEntity.setUpdatedDate(Timestamp.valueOf("2024-04-04 00:00:00"));
        shiftRosterEntity.setCreatedBy("Nive");
        shiftRosterEntity.setUpdatedBy("Nive");
        shiftRosterEntity.setDay01(1);
        shiftRosterEntity.setDay02(2);
        shiftRosterEntity.setDay03(3);
        shiftRosterEntity.setDay04(4);
        shiftRosterEntity.setDay05(5);
        shiftRosterEntity.setDay06(6);
        shiftRosterEntity.setDay07(7);
        shiftRosterEntity.setDay08(8);
        shiftRosterEntity.setDay09(9);
        shiftRosterEntity.setDay10(10);
        shiftRosterEntity.setDay11(11);
        shiftRosterEntity.setDay12(12);
        shiftRosterEntity.setDay13(13);
        shiftRosterEntity.setDay14(14);
        shiftRosterEntity.setDay15(15);
        shiftRosterEntity.setDay16(16);
        shiftRosterEntity.setDay17(17);
        shiftRosterEntity.setDay18(18);
        shiftRosterEntity.setDay19(19);
        shiftRosterEntity.setDay20(20);
        shiftRosterEntity.setDay21(21);
        shiftRosterEntity.setDay22(22);
        shiftRosterEntity.setDay23(23);
        shiftRosterEntity.setDay24(24);
        shiftRosterEntity.setDay25(25);
        shiftRosterEntity.setDay26(26);
        shiftRosterEntity.setDay27(27);
        shiftRosterEntity.setDay28(28);
        shiftRosterEntity.setDay29(29);
        shiftRosterEntity.setDay30(30);
        shiftRosterEntity.setDay31(31);

        int id = shiftRosterEntity.getId();
        Integer employeeId = shiftRosterEntity.getEmpId();
        int year = shiftRosterEntity.getYear();
        int month = shiftRosterEntity.getMonth();
        Timestamp createdDate = shiftRosterEntity.getCreatedDate();
        Timestamp updatedDate = shiftRosterEntity.getUpdatedDate();
        String createdBy = shiftRosterEntity.getCreatedBy();
        String updatedBy = shiftRosterEntity.getUpdatedBy();
        int day1= shiftRosterEntity.getDay01();
        int day2=shiftRosterEntity.getDay02();
        int day3= shiftRosterEntity.getDay03();
        int day4=shiftRosterEntity.getDay04();
        int day5= shiftRosterEntity.getDay05();
        int day6=shiftRosterEntity.getDay06();
        int day7= shiftRosterEntity.getDay07();
        int day8=shiftRosterEntity.getDay08();
        int day9= shiftRosterEntity.getDay09();
        int day10=shiftRosterEntity.getDay10();
        int day11= shiftRosterEntity.getDay11();
        int day12=shiftRosterEntity.getDay12();
        int day13= shiftRosterEntity.getDay13();
        int day14=shiftRosterEntity.getDay14();
        int day15= shiftRosterEntity.getDay15();
        int day16=shiftRosterEntity.getDay16();
        int day17= shiftRosterEntity.getDay17();
        int day18=shiftRosterEntity.getDay18();
        int day19= shiftRosterEntity.getDay19();
        int day20=shiftRosterEntity.getDay20();
        int day21= shiftRosterEntity.getDay21();
        int day22=shiftRosterEntity.getDay22();
        int day23= shiftRosterEntity.getDay23();
        int day24=shiftRosterEntity.getDay24();
        int day25= shiftRosterEntity.getDay25();
        int day26=shiftRosterEntity.getDay26();
        int day27= shiftRosterEntity.getDay27();
        int day28=shiftRosterEntity.getDay28();
        int day29= shiftRosterEntity.getDay29();
        int day30=shiftRosterEntity.getDay30();
        int day31= shiftRosterEntity.getDay31();

        Assertions.assertEquals(Optional.of(id), Optional.of(shiftRosterEntity.getId()));
        Assertions.assertEquals(employeeId,shiftRosterEntity.getEmpId());
        Assertions.assertEquals(year,shiftRosterEntity.getYear());
        Assertions.assertEquals(month,shiftRosterEntity.getMonth());
        Assertions.assertEquals(createdBy,shiftRosterEntity.getCreatedBy());
        Assertions.assertEquals(updatedBy,shiftRosterEntity.getCreatedBy());
        Assertions.assertEquals(createdDate,shiftRosterEntity.getCreatedDate());
        Assertions.assertEquals(updatedDate,shiftRosterEntity.getUpdatedDate());
        Assertions.assertEquals(day1,shiftRosterEntity.getDay01());
        Assertions.assertEquals(day2,shiftRosterEntity.getDay02());
        Assertions.assertEquals(day3,shiftRosterEntity.getDay03());
        Assertions.assertEquals(day4,shiftRosterEntity.getDay04());
        Assertions.assertEquals(day5,shiftRosterEntity.getDay05());
        Assertions.assertEquals(day6,shiftRosterEntity.getDay06());
        Assertions.assertEquals(day7,shiftRosterEntity.getDay07());
        Assertions.assertEquals(day8,shiftRosterEntity.getDay08());
        Assertions.assertEquals(day9,shiftRosterEntity.getDay09());
        Assertions.assertEquals(day10,shiftRosterEntity.getDay10());
        Assertions.assertEquals(day11,shiftRosterEntity.getDay11());
        Assertions.assertEquals(day12,shiftRosterEntity.getDay12());
        Assertions.assertEquals(day13,shiftRosterEntity.getDay13());
        Assertions.assertEquals(day14,shiftRosterEntity.getDay14());
        Assertions.assertEquals(day15,shiftRosterEntity.getDay15());
        Assertions.assertEquals(day16,shiftRosterEntity.getDay16());
        Assertions.assertEquals(day17,shiftRosterEntity.getDay17());
        Assertions.assertEquals(day18,shiftRosterEntity.getDay18());
        Assertions.assertEquals(day19,shiftRosterEntity.getDay19());
        Assertions.assertEquals(day20,shiftRosterEntity.getDay20());
        Assertions.assertEquals(day21,shiftRosterEntity.getDay21());
        Assertions.assertEquals(day22,shiftRosterEntity.getDay22());
        Assertions.assertEquals(day23,shiftRosterEntity.getDay23());
        Assertions.assertEquals(day24,shiftRosterEntity.getDay24());
        Assertions.assertEquals(day25,shiftRosterEntity.getDay25());
        Assertions.assertEquals(day26,shiftRosterEntity.getDay26());
        Assertions.assertEquals(day27,shiftRosterEntity.getDay27());
        Assertions.assertEquals(day28,shiftRosterEntity.getDay28());
        Assertions.assertEquals(day29,shiftRosterEntity.getDay29());
        Assertions.assertEquals(day30,shiftRosterEntity.getDay30());
        Assertions.assertEquals(day31,shiftRosterEntity.getDay31());
    }
}
