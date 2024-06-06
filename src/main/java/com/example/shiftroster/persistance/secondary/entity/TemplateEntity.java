package com.example.shiftroster.persistance.secondary.entity;

import com.example.shiftroster.persistance.Enum.EnumDocType;
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
@Table(name="tbl_template")
public class TemplateEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "ref_id", nullable = false)
    private Integer refId;

    @Column(name = "ref_type" , nullable = false, length = 25)
    private String refType;

    @Enumerated(value = EnumType.STRING)
    @Column(name="doc_type",nullable = false,length = 10)
    private EnumDocType docType;

    @Column(name = "document_file" , nullable = false, length = 300)
    private String documentFile;

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
