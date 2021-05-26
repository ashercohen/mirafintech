package com.mirafintech.prototype.tests.util.providers.queries;


public class PostgreSQLQueries implements Queries {

    public static final Queries INSTANCE = new PostgreSQLQueries();

    @Override
    public String transactionId() {
        return "SELECT CAST(txid_current() AS text)";
    }
}
