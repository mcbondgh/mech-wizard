package com.mech.app.dataproviders.transactions;

import java.sql.Timestamp;

public class CustomerAccount {
//    private int recordId;
//    private int customerId;
//    private double accountBalance;
//    private Timestamp dateUpdated;
//    private int userId;

    public record CustomerAccountRecord(int recordId, int customerId, double accountBalance, Timestamp dateUpdated, int userId) {
        // This record class is used to encapsulate the customer account data.
    }

}
