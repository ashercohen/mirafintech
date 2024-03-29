package com.mirafintech.prototype.tests.util;

import com.mirafintech.prototype.tests.util.providers.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DataSourceProviderIntegrationTest - Test against some common RDBMS providers
 *

 */
@RunWith(Parameterized.class)
public abstract class DataSourceProviderIntegrationTest extends AbstractTest {

    private final DataSourceProvider dataSourceProvider;

    public DataSourceProviderIntegrationTest(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Parameterized.Parameters
    public static Collection<DataSourceProvider[]> rdbmsDataSourceProvider() {
        List<DataSourceProvider[]> providers = new ArrayList<>();
        providers.add(new DataSourceProvider[]{new OracleDataSourceProvider()});
        providers.add(new DataSourceProvider[]{new SQLServerDataSourceProvider()});
        providers.add(new DataSourceProvider[]{new PostgreSQLDataSourceProvider()});
        providers.add(new DataSourceProvider[]{new MySQLDataSourceProvider()});
        return providers;
    }

    @Override
    protected DataSourceProvider dataSourceProvider() {
        return dataSourceProvider;
    }
}
