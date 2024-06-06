package com.example.shiftroster.persistance.secondary.repository;

import com.example.shiftroster.persistance.Enum.EnumDocType;
import com.example.shiftroster.persistance.secondary.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface TemplateRepo extends JpaRepository<TemplateEntity,Integer> {

    String findByDocTypeAndRefType(EnumDocType enumDocType, String templateType);
}
