package com.mirafintech.prototype.tests.util;

import com.mirafintech.prototype.tests.util.providers.Database;

/**
 * AbstractPostgreSQLIntegrationTest - Abstract PostgreSQL IntegrationTest
 *

 */
public abstract class AbstractPostgreSQLIntegrationTest extends AbstractTest {

    @Override
    protected Database database() {
        return Database.POSTGRESQL;
    }
}
