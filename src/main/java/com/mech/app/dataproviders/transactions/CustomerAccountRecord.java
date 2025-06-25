package com.mech.app.dataproviders.transactions;

import java.sql.Timestamp;

public record CustomerAccountRecord(int recordId, int customerId, double accountBalance, Timestamp dateUpdated, int userId) {
}
