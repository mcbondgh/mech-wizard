package com.mech.app.dataproviders.transactions;

import java.sql.Date;

public class TransactionsDataProvider {
    int recordId;
    private String jobNo, paymentMtd;
    private String status, customerName;

    public record transactionRecord(String jobNo, String customerName, String service, String car, Date serviceDate, String amount, String status){}
}
