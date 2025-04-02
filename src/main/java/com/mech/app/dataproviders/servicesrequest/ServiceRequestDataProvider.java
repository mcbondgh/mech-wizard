package com.mech.app.dataproviders.servicesrequest;

import com.mech.app.dataproviders.customers.CustomersDataProvider;

import java.sql.Timestamp;

public class ServiceRequestDataProvider {
    int customerId, recordId;
    String serviceType, requestNumber;
    String plateNumber, problemDescription;
    String requestPriority;
    Timestamp dateBooked;
    CustomersDataProvider customersDataProvider;

    public record ServiceRequestRecord(String request){};

}//end of class...
