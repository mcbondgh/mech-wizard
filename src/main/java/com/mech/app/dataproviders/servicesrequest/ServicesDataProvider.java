package com.mech.app.dataproviders.servicesrequest;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class ServicesDataProvider {

    public record ServiceRequestRecord(int customerId, int vehicleId, int serviceId,
                                       double serviceCost, String desc, Date preferredDate,
                                       String urgencyLevel, boolean allowPickup, String status, int userId ){};
    public record CompletedServicesRecord(
            String jobNo, String serviceType, String date, String rateStatus, String assignedTechnician,
            String plateNumber, String problemDescription, List<String> partsUsed, List<String> technicianNotes,
            String amount){};

    public record BookedServicesRecord(int serviceId, String customerName, String vehicle,
                                       String VIN, String serviceType, double cost, String desc,
                                       String urgencyLevel, String preferredDate, boolean pickupStatus,
                                       String serviceStatus, String mechanic, Timestamp loggedDate) {

        public String pickupValue() {
            return pickupStatus ? "YES" : "NO";
        }

        public String serviceNo() {
            return "SVR/"+ LocalDate.now().getMonthValue() +"/" + serviceId;
        }

        public String statusValue() {
            return switch (serviceStatus) {
                case "new" -> "New";
                case "assigned" -> "Job Assigned";
                case "completed" -> "Completed";
                case "cancelled" -> "Cancelled";
                default -> "Unknown Status";
            };
        }
        public BookedServicesRecord {

        }

    }



}//end of class...
