package com.mirafintech.prototype.tests.util.providers;

import com.mirafintech.prototype.tests.util.providers.queries.Queries;

import javax.sql.DataSource;
import java.util.Properties;


public interface DataSourceProvider {

	enum IdentifierStrategy {
		IDENTITY,
		SEQUENCE
	}

	String hibernateDialect();

	DataSource dataSource();

	Class<? extends DataSource> dataSourceClassName();

	Properties dataSourceProperties();

	String url();

	String username();

	String password();

	Database database();

	Queries queries();
}
