package com.example.shiftroster.persistance.secondary.entity;

import com.example.shiftroster.persistance.primary.entity.EmployeeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="tbl_shift_roster")
public class ShiftRosterEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Transient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", referencedColumnName = "Id", nullable = false)
    private EmployeeEntity empId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", referencedColumnName = "Id", nullable = false)
    private com.example.shiftroster.persistance.secondary.entity.ShiftEntity shiftId;

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
}
