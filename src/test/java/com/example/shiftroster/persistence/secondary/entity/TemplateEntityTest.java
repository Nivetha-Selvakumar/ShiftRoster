package com.example.shiftroster.persistence.secondary.entity;

import com.example.shiftroster.persistence.Enum.EnumDocType;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;

@ExtendWith(MockitoExtension.class)
public class TemplateEntityTest {

    TemplateEntity templateEntity = new TemplateEntity();

    @Test
    public void testTemplateEntity(){
        templateEntity.setId(1);
        templateEntity.setRefId(1);
        templateEntity.setRefType("Shiftroster" );
        templateEntity.setDocType(EnumDocType.EXCEL);
        templateEntity.setDocumentFile(".xlx");
        templateEntity.setCreatedBy("nive");
        templateEntity.setUpdatedBy("nive");
        templateEntity.setCreatedDate(Timestamp.valueOf("2024-09-09 00:00:00"));
        templateEntity .setUpdatedDate(Timestamp.valueOf("2024-09-09 00:00:00"));

        int id = templateEntity.getId();
        int refId = templateEntity.getRefId();
        String refType = templateEntity.getRefType();;
        EnumDocType docType = templateEntity.getDocType();
        String docFile = templateEntity.getDocumentFile();
        String createdBy = templateEntity.getCreatedBy();
        String updatedBy = templateEntity.getUpdatedBy();
        Timestamp createdDate = templateEntity.getCreatedDate();
        Timestamp updatedDate = templateEntity.getUpdatedDate();

        Assertions.assertEquals(id, templateEntity.getId());
        Assertions.assertEquals(refId, templateEntity.getId());
        Assertions.assertEquals(refType, templateEntity.getRefType());
        Assertions.assertEquals(docType, templateEntity.getDocType());
        Assertions.assertEquals(docFile, templateEntity.getDocumentFile());
        Assertions.assertEquals(createdBy, templateEntity.getCreatedBy());
        Assertions.assertEquals(updatedBy, templateEntity.getUpdatedBy());
        Assertions.assertEquals(createdDate, templateEntity.getCreatedDate());
        Assertions.assertEquals(updatedDate, templateEntity.getUpdatedDate());
    }
}
