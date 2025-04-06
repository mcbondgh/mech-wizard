package com.mech.app.dataproviders.servicesrequest;

import com.mech.app.dataproviders.customers.CustomersDataProvider;

import java.sql.Timestamp;
import java.util.List;

public class ServiceRequestDataProvider {
    int customerId, recordId;
    String serviceType, requestNumber;
    String plateNumber, problemDescription;
    String requestPriority;
    Timestamp dateBooked;
    CustomersDataProvider customersDataProvider;

    public record ServiceRequestRecord(String request){};
    public record completedServicesRecord(
            String jobNo, String serviceType, String date, String rateStatus, String assignedTechnician,
            String plateNumber, String problemDescription, List<String> partsUsed, List<String> technicianNotes,
            String amount){};



}//end of class...
