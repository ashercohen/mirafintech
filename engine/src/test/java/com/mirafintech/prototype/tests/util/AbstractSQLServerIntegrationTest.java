package com.mirafintech.prototype.tests.util;

import com.mirafintech.prototype.tests.util.providers.Database;

/**
 * AbstractSQLServerIntegrationTest - Abstract SQL Server IntegrationTest
 *

 */
public abstract class AbstractSQLServerIntegrationTest extends AbstractTest {

    @Override
    protected Database database() {
        return Database.SQLSERVER;
    }
}
