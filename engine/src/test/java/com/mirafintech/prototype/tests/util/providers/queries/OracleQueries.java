package com.mirafintech.prototype.tests.util.providers.queries;


public class OracleQueries implements Queries {

    public static final Queries INSTANCE = new OracleQueries();

    @Override
    public String transactionId() {
        return "SELECT RAWTOHEX(tx.xid) " +
                "FROM v$transaction tx " +
                "JOIN v$session s ON tx.ses_addr = s.saddr";
    }
}
