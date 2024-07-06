package com.example.shiftroster.persistence.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.Environment;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PrimaryConfigTest {
//    @Autowired
//    private DataSourceProperties primaryDataSourceProperties;
//
//    @Autowired
//    private DataSource primaryDataSource;
//
//    @Autowired
//    private LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory;
//
//    @Autowired
//    private PlatformTransactionManager primaryTransactionManager;
//
//    @Test
//    public void testDataSourceProperties() {
//        assertNotNull(primaryDataSourceProperties);
//        assertNotNull(primaryDataSourceProperties.getUrl());
//        assertEquals("com.zaxxer.hikari.HikariDataSource", primaryDataSourceProperties.getType().getName());
//    }
//
//    @Test
//    public void testPrimaryDataSource() {
//        assertNotNull(primaryDataSource);
//        assertTrue(primaryDataSource instanceof HikariDataSource);
//        // Add more assertions based on your configuration and expected properties
//    }
//
//    @Test
//    public void testEntityManagerFactory() {
//        assertNotNull(primaryEntityManagerFactory);
//        assertNotNull(primaryEntityManagerFactory.getDataSource());
//        // Add more assertions if needed
//    }
//
//    @Test
//    public void testTransactionManager() {
//        assertNotNull(primaryTransactionManager);
//        // Verify transaction manager setup
//    }
//
//    // Optionally add more tests for specific configurations or edge cases
//
//    // Mock or test other configurations as needed
//
//    // Example mock environment for test
//    @Configuration
//    public static class TestConfig {
//        @Bean
//        public Environment environment() {
//            return mock(Environment.class);
//        }
//    }

}
