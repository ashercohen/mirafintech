package com.mirafintech.prototype.tests.util.providers.queries;


public class SQLServerQueries implements Queries {

    public static final Queries INSTANCE = new SQLServerQueries();

    @Override
    public String transactionId() {
        return "SELECT CONVERT(VARCHAR, CURRENT_TRANSACTION_ID())";
    }
}
