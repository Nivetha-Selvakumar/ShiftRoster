package com.example.shiftroster.persistance.primary.entity;


import com.example.shiftroster.persistance.Enum.EnumRole;
import com.example.shiftroster.persistance.Enum.EnumStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
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
    @JoinColumn(name = "appraiser", referencedColumnName = "id")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public EnumRole getRole() {
        return role;
    }

    public void setRole(EnumRole role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmployeeEntity getAppraiser() {
        return appraiser;
    }

    public void setAppraiser(EmployeeEntity appraiser) {
        this.appraiser = appraiser;
    }

    public EnumStatus getEmpStatus() {
        return empStatus;
    }

    public void setEmpStatus(EnumStatus empStatus) {
        this.empStatus = empStatus;
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
