package com.example.shiftroster.persistance.secondary.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name="tbl_shift_roster")
public class ShiftRosterEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "emp_id", nullable = false, length = 50)
    private Integer empId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", referencedColumnName = "id", nullable = false)
    private ShiftEntity shiftId;

    @Column(name = "day_01")
    private Integer day01;

    @Column(name = "day_02")
    private Integer day02;

    @Column(name = "day_03")
    private Integer day03;

    @Column(name = "day_04")
    private Integer day04;

    @Column(name = "day_05")
    private Integer day05;

    @Column(name = "day_06")
    private Integer day06;

    @Column(name = "day_07")
    private Integer day07;

    @Column(name = "day_08")
    private Integer day08;

    @Column(name = "day_09")
    private Integer day09;

    @Column(name = "day_10")
    private Integer day10;

    @Column(name = "day_11")
    private Integer day11;

    @Column(name = "day_12")
    private Integer day12;

    @Column(name = "day_13")
    private Integer day13;

    @Column(name = "day_14")
    private Integer day14;

    @Column(name = "day_15")
    private Integer day15;

    @Column(name = "day_16")
    private Integer day16;

    @Column(name = "day_17")
    private Integer day17;

    @Column(name = "day_18")
    private Integer day18;

    @Column(name = "day_19")
    private Integer day19;

    @Column(name = "day_20")
    private Integer day20;

    @Column(name = "day_21")
    private Integer day21;

    @Column(name = "day_22")
    private Integer day22;

    @Column(name = "day_23")
    private Integer day23;

    @Column(name = "day_24")
    private Integer day24;

    @Column(name = "day_25")
    private Integer day25;

    @Column(name = "day_26")
    private Integer day26;

    @Column(name = "day_27")
    private Integer day27;

    @Column(name = "day_28")
    private Integer day28;

    @Column(name = "day_29")
    private Integer day29;

    @Column(name = "day_30")
    private Integer day30;

    @Column(name = "day_31")
    private Integer day31;

    @Column(name = "month", nullable=false)
    private Integer month;

    @Column(name = "year", nullable=false)
    private Integer year;

    @Column(name = "created_date", nullable = false)
    @CreationTimestamp
    private Timestamp createdDate;

    @Column(name = "created_by",length = 20 ,nullable = false)
    private String createdBy;

    @Column(name = "updated_date", nullable = false)
    @UpdateTimestamp
    private Timestamp updatedDate;

    @Column(name = "updated_by",length = 20,nullable = false)
    private String updatedBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public ShiftEntity getShiftId() {
        return shiftId;
    }

    public void setShiftId(ShiftEntity shiftId) {
        this.shiftId = shiftId;
    }

    public Integer getDay01() {
        return day01;
    }

    public void setDay01(Integer day01) {
        this.day01 = day01;
    }

    public Integer getDay02() {
        return day02;
    }

    public void setDay02(Integer day02) {
        this.day02 = day02;
    }

    public Integer getDay03() {
        return day03;
    }

    public void setDay03(Integer day03) {
        this.day03 = day03;
    }

    public Integer getDay04() {
        return day04;
    }

    public void setDay04(Integer day04) {
        this.day04 = day04;
    }

    public Integer getDay05() {
        return day05;
    }

    public void setDay05(Integer day05) {
        this.day05 = day05;
    }

    public Integer getDay06() {
        return day06;
    }

    public void setDay06(Integer day06) {
        this.day06 = day06;
    }

    public Integer getDay07() {
        return day07;
    }

    public void setDay07(Integer day07) {
        this.day07 = day07;
    }

    public Integer getDay08() {
        return day08;
    }

    public void setDay08(Integer day08) {
        this.day08 = day08;
    }

    public Integer getDay09() {
        return day09;
    }

    public void setDay09(Integer day09) {
        this.day09 = day09;
    }

    public Integer getDay10() {
        return day10;
    }

    public void setDay10(Integer day10) {
        this.day10 = day10;
    }

    public Integer getDay11() {
        return day11;
    }

    public void setDay11(Integer day11) {
        this.day11 = day11;
    }

    public Integer getDay12() {
        return day12;
    }

    public void setDay12(Integer day12) {
        this.day12 = day12;
    }

    public Integer getDay13() {
        return day13;
    }

    public void setDay13(Integer day13) {
        this.day13 = day13;
    }

    public Integer getDay14() {
        return day14;
    }

    public void setDay14(Integer day14) {
        this.day14 = day14;
    }

    public Integer getDay15() {
        return day15;
    }

    public void setDay15(Integer day15) {
        this.day15 = day15;
    }

    public Integer getDay16() {
        return day16;
    }

    public void setDay16(Integer day16) {
        this.day16 = day16;
    }

    public Integer getDay17() {
        return day17;
    }

    public void setDay17(Integer day17) {
        this.day17 = day17;
    }

    public Integer getDay18() {
        return day18;
    }

    public void setDay18(Integer day18) {
        this.day18 = day18;
    }

    public Integer getDay19() {
        return day19;
    }

    public void setDay19(Integer day19) {
        this.day19 = day19;
    }

    public Integer getDay20() {
        return day20;
    }

    public void setDay20(Integer day20) {
        this.day20 = day20;
    }

    public Integer getDay21() {
        return day21;
    }

    public void setDay21(Integer day21) {
        this.day21 = day21;
    }

    public Integer getDay22() {
        return day22;
    }

    public void setDay22(Integer day22) {
        this.day22 = day22;
    }

    public Integer getDay23() {
        return day23;
    }

    public void setDay23(Integer day23) {
        this.day23 = day23;
    }

    public Integer getDay24() {
        return day24;
    }

    public void setDay24(Integer day24) {
        this.day24 = day24;
    }

    public Integer getDay25() {
        return day25;
    }

    public void setDay25(Integer day25) {
        this.day25 = day25;
    }

    public Integer getDay26() {
        return day26;
    }

    public void setDay26(Integer day26) {
        this.day26 = day26;
    }

    public Integer getDay27() {
        return day27;
    }

    public void setDay27(Integer day27) {
        this.day27 = day27;
    }

    public Integer getDay28() {
        return day28;
    }

    public void setDay28(Integer day28) {
        this.day28 = day28;
    }

    public Integer getDay29() {
        return day29;
    }

    public void setDay29(Integer day29) {
        this.day29 = day29;
    }

    public Integer getDay30() {
        return day30;
    }

    public void setDay30(Integer day30) {
        this.day30 = day30;
    }

    public Integer getDay31() {
        return day31;
    }

    public void setDay31(Integer day31) {
        this.day31 = day31;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
