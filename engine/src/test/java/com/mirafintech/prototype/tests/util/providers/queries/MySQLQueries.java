package com.mirafintech.prototype.tests.util.providers.queries;


public class MySQLQueries implements Queries {

    public static final Queries INSTANCE = new MySQLQueries();

    @Override
    public String transactionId() {
        return "SELECT trx_id FROM information_schema.innodb_trx";
    }
}
