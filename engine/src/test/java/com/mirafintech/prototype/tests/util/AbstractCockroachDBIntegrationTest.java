package com.mirafintech.prototype.tests.util;

import com.mirafintech.prototype.tests.util.providers.Database;

/**
 * AbstractCockroachDBIntegrationTest - Abstract CockroachDB IntegrationTest
 *

 */
public abstract class AbstractCockroachDBIntegrationTest extends com.mirafintech.prototype.tests.util.AbstractTest {

    @Override
    protected Database database() {
        return Database.COCKROACHDB;
    }
}
