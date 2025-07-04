package com.mech.app.dataproviders.jobcards;

import java.sql.Timestamp;
import java.util.Map;

public class JobCardDataProvider {
    private int recordId, customerId;
    private String jobNumber, serviceType;
    private String problemDescription, technicianNote;
    private String jobStatus;
    private double progressValue;
    private String assignedTechnician;
    private String customerName, car, plateNumber;
    private Map<String, Object> carPartsAndCost;
    private Timestamp dateCreated;
    private Timestamp serviceDate;

    public record VehiclePartsRecord(int jobNo, String itemName, int quantity, double amount){}

    public record JobDescptionRecord(int recordId, int jobId, int serviceId, String serviceDesc, String jobDesc, Timestamp noteDate, Timestamp loggedDate){
        public String jobNo() {
            return String.format("JC/%d/%d", loggedDate.getMonth(), jobId);
        }
    }

    public record JobCardRecords(int jobId, int serviceId, String customerName, String vehicleName, String mechanic,
                                 String VIN, String serviceType, String serviceDesc, Timestamp loggedDate, String jobStatus, String progressValue) {
        private int serviceMonth() {
            return loggedDate.toLocalDateTime().toLocalDate().getMonthValue();
        }
        public String jobNo() {
            return String.format("JC/%d/%d", serviceMonth(), jobId);
        }
        public String serviceNo() {
            return String.format("SVR/%d/%d", serviceMonth(), serviceId);
        }
        public String jobStatusValue() {
            return switch (jobStatus) {
                case "new" -> "New";
                case "awaiting" -> "Awaiting parts";
                case "progress" -> "In progress";
                case "on_hold" -> "On hold";
                default -> "Completed";
            };
        }
    }

    public record JobCardPurchasesItems(int itemId, int jobId, String name, int qty, double price, Timestamp date){}

    public JobCardDataProvider() {}

}//end of class...
