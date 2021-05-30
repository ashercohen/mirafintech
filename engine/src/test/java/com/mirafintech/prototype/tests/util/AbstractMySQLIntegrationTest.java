package com.mirafintech.prototype.tests.util;

import com.mirafintech.prototype.tests.util.providers.Database;

/**
 * AbstractMySQLIntegrationTest - Abstract MySQL IntegrationTest
 *

 */
public abstract class AbstractMySQLIntegrationTest extends AbstractTest {

    @Override
    protected Database database() {
        return Database.MYSQL;
    }
}
