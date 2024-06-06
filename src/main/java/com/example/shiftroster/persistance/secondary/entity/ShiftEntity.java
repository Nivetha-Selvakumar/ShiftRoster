package com.example.shiftroster.persistance.secondary.entity;

import com.example.shiftroster.persistance.Enum.EnumStatus;
import com.example.shiftroster.persistance.Enum.ShiftType;
import com.example.shiftroster.persistance.primary.entity.EmployeeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="tbl_shift")
public class ShiftEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Transient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", referencedColumnName = "Id", nullable = false)
    private EmployeeEntity empId;

    @Column(name = "shift_name", nullable = false, length = 25 )
    private String shiftName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "shift_type", nullable = false, length = 10)
    private ShiftType shiftType;

    @Column(name = "from_time", nullable = false)
    private Time fromTime;

    @Column(name = "to_time", nullable = false)
    private Time toTime;

    @Column(name = "allowed_in_time", nullable = false)
    private Time allowedInTime;

    @Column(name = "allowed_out_time", nullable = false)
    private Time allowedOutTime;

    @Enumerated(value = EnumType.STRING)
    @Column(name="status",nullable = false,length = 10)
    private EnumStatus status;

    @Column(name = "created_date", nullable = false)
    @CreationTimestamp
    private Timestamp createdDate;

    @Column(name = "created_by",length = 25 ,nullable = false)
    private String createdBy;

    @Column(name = "updated_date", nullable = false)
    @UpdateTimestamp
    private Timestamp updatedDate;

    @Column(name = "updated_by",length = 25,nullable = false)
    private String updatedBy;


}
