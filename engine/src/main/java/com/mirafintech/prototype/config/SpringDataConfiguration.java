package com.mirafintech.prototype.config;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableJpaRepositories("com.mirafintech.prototype")
@ComponentScan("com.mirafintech.prototype") // required for non repository bean to be detected
public class SpringDataConfiguration {

    private static final DatabaseConfiguration DATABASE_CONFIGURATION;

    static {
        DATABASE_CONFIGURATION = new DatabaseConfiguration();
    }

    @Bean
    public DataSource getDataSource() {

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(DATABASE_CONFIGURATION.getDatabaseURL());
        dataSource.setUser(DATABASE_CONFIGURATION.getUserName());
        dataSource.setPassword(DATABASE_CONFIGURATION.getPassword());

        return dataSource;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        Properties jpaProperties = new Properties();
        /**
         * possible values: validate | update | create | create-drop
         * see more:
         * https://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html_single/#configuration-misc-properties
         */
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect");
        jpaProperties.put("hibernate.show_sql", "true");

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(getDataSource());
        factory.setPackagesToScan("com.mirafintech.mlinfra");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaProperties(jpaProperties);
        factory.afterPropertiesSet(); // signal beans setup is complete (spring lifecycle event)

        return factory.getObject();
    }
}
