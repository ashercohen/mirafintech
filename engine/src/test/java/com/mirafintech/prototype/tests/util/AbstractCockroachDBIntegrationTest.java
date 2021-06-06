package com.mirafintech.prototype.tests.util;

import com.mirafintech.prototype.tests.util.providers.Database;

/**
 * AbstractCockroachDBIntegrationTest - Abstract CockroachDB IntegrationTest
 *

 */
public abstract class AbstractCockroachDBIntegrationTest extends AbstractTest {

    @Override
    protected Database database() {
        return Database.COCKROACHDB;
    }
}
