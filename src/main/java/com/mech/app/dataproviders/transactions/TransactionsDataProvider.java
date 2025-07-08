package com.mech.app.dataproviders.transactions;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class TransactionsDataProvider {
    private int customerId, recordId;
    private String transactionId;
    private String paymentMethod;
    private String notes;
    private double amount;
    private int userId;
    private String transactionType;
    private Timestamp transactionDate;

    public record Payments(int id, int jobId, Timestamp date, String reference, String payMethod, String ServiceCost,
                           String discount, String labourCost, String totalCost) {
        public String receiptNo() {
            return String.format("RCPT-%s", id);
        }
    }

    public record TransactionRecord(int jobId, int serviceId, int customerId, String customerName, String serviceType, Date serviceDate, double account_balance, double serviceCost, double itemsCost, String status){

        public String jobNo() {
            int month = serviceDate.toLocalDate().getMonthValue();
            return String.format("JC/%d/%d", month, jobId);
        }
        public String formattedDate() {
            return serviceDate.toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
        }

    }
    public record UpdateRecord(int jobId, int serviceId, int customerId, double accountBal, double serviceCost,
                               double payableAmount, double labourAmount, String reference, String payMethod) {}
}//END OF CLASS...
