package com.mirafintech.prototype.tests.util.providers;

import com.mirafintech.prototype.tests.util.providers.queries.PostgreSQLQueries;
import com.mirafintech.prototype.tests.util.providers.queries.Queries;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.util.Properties;


public class PostgreSQLDataSourceProvider implements DataSourceProvider {

    private static final String DATABASE = "mirafintech"; // "high_performance_java_persistence";
    private static final String HOST = "localhost";
    private static final String USERNAME = "mirafintech"; // "postgres";
    private static final String PASSWORD = "mirafintech"; // "admin";

    @Override
    public String hibernateDialect() {
        return PostgreSQL95Dialect.class.getName();
    }

    @Override
    public DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setDatabaseName(DATABASE);
        dataSource.setServerName(HOST);
        dataSource.setUser(USERNAME);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    @Override
    public Class<? extends DataSource> dataSourceClassName() {
        return PGSimpleDataSource.class;
    }

    @Override
    public Properties dataSourceProperties() {
        Properties properties = new Properties();
        properties.setProperty("databaseName", DATABASE);
        properties.setProperty("serverName", HOST);
        properties.setProperty("user", username());
        properties.setProperty("password", password());
        return properties;
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String username() {
        return USERNAME;
    }

    @Override
    public String password() {
        return PASSWORD;
    }

    @Override
    public Database database() {
        return Database.POSTGRESQL;
    }

    @Override
    public Queries queries() {
        return PostgreSQLQueries.INSTANCE;
    }
}
