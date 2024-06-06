package com.example.shiftroster.persistance.config;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.shiftroster.persistance.secondary",
        entityManagerFactoryRef = "secondaryEntityManagerFactory",
        transactionManagerRef = "secondaryTransactionManager"

)
public class SecondaryConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSourceProperties secondaryDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.secondary.configuration")
    public DataSource secondaryDataSource(){
        return secondaryDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

//    @Bean
//    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(){
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(false);
//        return new EntityManagerFactoryBuilder(vendorAdapter,new HashMap<>(),null);
//    }

    @Bean(name="secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityMangerFactory(EntityManagerFactoryBuilder builder){
        return builder.dataSource(secondaryDataSource()).packages("com.example.shiftroster.persistance.secondary").build();
    }

    @Bean(name = "secondaryTransationManager")
    public PlatformTransactionManager secondaryTransactionManager(
            final @Qualifier("secondaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory
    ){
        return new JpaTransactionManager(secondaryEntityManagerFactory.getObject());
    }
}
