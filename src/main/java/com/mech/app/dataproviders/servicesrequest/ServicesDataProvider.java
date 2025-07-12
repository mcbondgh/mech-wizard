package com.mech.app.dataproviders.servicesrequest;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ServicesDataProvider {

    public record ServiceRequestRecord(int customerId, int vehicleId, int serviceId,
                                       double serviceCost, String desc, Date preferredDate,
                                       String urgencyLevel, boolean allowPickup, String status, int userId) {
    }

    ;

    public record CompletedServicesRecord(
            int jobId, String jobStatus, String serviceType, String clientName, Timestamp serviceDate, Timestamp dateCompleted, String assignedTechnician,
            String vehicle, String VIN, double serviceCost, int itemsCount, double itemsTotalCost,
            String stars, String comments, Timestamp feedbackDate) {

        private int serviceMonth() {
            return serviceDate.toLocalDateTime().getMonthValue();
        }

        public String jobNo() {
            return "JC/" + serviceMonth() + "/" + jobId;
        }

        public String getStars() {
            return switch (stars) {
                case "1" -> "⭐";
                case "2" -> "⭐⭐";
                case "3" -> "⭐⭐⭐";
                case "4" -> "⭐⭐⭐⭐";
                case "5" -> "⭐⭐⭐⭐⭐";
                case null, default -> "No rating";
            };
        }

        public String dateCompletedString() {
            return dateCompleted.toLocalDateTime().toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
        }
    }

    public record PaidServicesRecord(int jobId, String serviceType, String description, String vehicle, String VIN,
                                    String stars, String feedback, Timestamp serviceDate, Timestamp dateCompleted, double totalBill
                                     ){
        public String jobNo() {
            return "JC/" + serviceDate.toLocalDateTime().getMonthValue() + "/" + jobId;
        }
        public String rate() {
            return switch (stars) {
                case "1" -> "⭐";
                case "2" -> "⭐⭐";
                case "3" -> "⭐⭐⭐";
                case "4" -> "⭐⭐⭐⭐";
                case "5" -> "⭐⭐⭐⭐⭐";
                default -> "no rating";
            };
        }
    }

    public record BookedServicesRecord(int serviceId, int customerId, String customerName, String vehicle,
                                       String VIN, String serviceType, double cost, String desc,
                                       String urgencyLevel, String preferredDate, boolean pickupStatus,
                                       String serviceStatus, String mechanic, Timestamp loggedDate,
                                       String terminationNote) {
        public String pickupValue() {
            return pickupStatus ? "YES" : "NO";
        }
        public String serviceNo() {
            return "SVR/" + loggedDate.toLocalDateTime().toLocalDate().getMonthValue() + "/" + serviceId;
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
    }


}//end of class...
