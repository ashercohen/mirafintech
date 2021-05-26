package com.mirafintech.prototype.tests.util;

import org.hibernate.dialect.PostgreSQL82Dialect;

import java.sql.Types;


public class CockroachDBDialect extends PostgreSQL82Dialect {

	public CockroachDBDialect() {
		super();
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "smallint" );
		registerColumnType( Types.INTEGER, "integer" );

		registerColumnType( Types.FLOAT, "double precision" );
		registerColumnType( Types.DOUBLE, "double precision" );

		registerColumnType( Types.BLOB, "blob" );
		registerColumnType( Types.OTHER, "interval" );
	}
}
