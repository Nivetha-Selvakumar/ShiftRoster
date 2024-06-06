package com.example.shiftroster.persistance.primary.entity;



import com.example.shiftroster.persistance.Enum.EnumRole;
import com.example.shiftroster.persistance.Enum.EnumStatus;
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
@Table(name="tbl_employee")
public class EmployeeEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "emp_name", nullable = false, length = 25 )
    private String empName;

    @Column(name = "emp_code", nullable = false, length = 10)
    private String empCode;

    @Enumerated(value = EnumType.STRING)
    @Column(name="role",nullable = false,length = 10)
    private EnumRole role;

    @Column(name = "email", nullable = false,length = 20)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appraiser", referencedColumnName = "Id", nullable = true)
    private EmployeeEntity appraiser;

    @Enumerated(value = EnumType.STRING)
    @Column(name="emp_status",nullable = false,length = 10)
    private EnumStatus empStatus;

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
