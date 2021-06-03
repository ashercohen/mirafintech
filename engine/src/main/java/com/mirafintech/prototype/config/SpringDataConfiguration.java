package com.mirafintech.prototype.config;

import org.hsqldb.jdbc.JDBCDataSource;
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
@ComponentScan("com.mirafintech.prototype") // required for non repository beans to be detected
public class SpringDataConfiguration {

    private static final DatabaseConfiguration DATABASE_CONFIGURATION;

    private static boolean USE_POSTGRES = true;

    static {
        DATABASE_CONFIGURATION = new DatabaseConfiguration();
    }

    @Bean
    public DataSource getDataSource() {

        if (USE_POSTGRES) {
            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setURL(DATABASE_CONFIGURATION.getDatabaseURL());
            dataSource.setUser(DATABASE_CONFIGURATION.getUserName());
            dataSource.setPassword(DATABASE_CONFIGURATION.getPassword());
            return dataSource;
        } else {
            JDBCDataSource dataSource = new JDBCDataSource();
            dataSource.setUrl("jdbc:hsqldb:mem:test");
            dataSource.setUser("sa");
            dataSource.setPassword("");
            return dataSource;
        }
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
        jpaProperties.put("hibernate.hbm2ddl.auto", "create");
        jpaProperties.put("hibernate.show_sql", "true");

        if (USE_POSTGRES) {
            jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect");
        } else {
            jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
            jpaProperties.put("spring.datasource.driver-class-name", "org.hsqldb.jdbc.JDBCDriver");
        }

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(getDataSource());
        factory.setPackagesToScan("com.mirafintech.prototype");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaProperties(jpaProperties);
        factory.afterPropertiesSet(); // signal beans setup is complete (spring lifecycle event)

        return factory.getObject();
    }
}
