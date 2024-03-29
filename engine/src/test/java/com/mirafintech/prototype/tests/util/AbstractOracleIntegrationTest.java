package com.mirafintech.prototype.tests.util;

import com.mirafintech.prototype.tests.util.providers.Database;

/**
 * AbstractOracleXEIntegrationTest - Abstract Orcale XE IntegrationTest
 *

 */
public abstract class AbstractOracleIntegrationTest extends AbstractTest {

    @Override
    protected Database database() {
        return Database.ORACLE;
    }
}
