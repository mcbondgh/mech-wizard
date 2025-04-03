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

    public record partsDataProvider(String jobNo, String itemName, int quantity, double amount){}
    public JobCardDataProvider() {}

    public JobCardDataProvider(int recordId, String jobNumber, String serviceType, String problemDescription, String technicianNote, String jobStatus, double progressValue, String assignedTechnician, String customerName, String car, String plateNumber, Map<String, Object> carPartsAndCost, Timestamp serviceDate) {
        this.recordId = recordId;
        this.jobNumber = jobNumber;
        this.serviceType = serviceType;
        this.problemDescription = problemDescription;
        this.technicianNote = technicianNote;
        this.jobStatus = jobStatus;
        this.progressValue = progressValue;
        this.assignedTechnician = assignedTechnician;
        this.customerName = customerName;
        this.car = car;
        this.plateNumber = plateNumber;
        this.carPartsAndCost = carPartsAndCost;
        this.serviceDate = serviceDate;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getTechnicianNote() {
        return technicianNote;
    }

    public void setTechnicianNote(String technicianNote) {
        this.technicianNote = technicianNote;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public double getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(double progressValue) {
        this.progressValue = progressValue;
    }

    public String getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setAssignedTechnician(String assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Map<String, Object> getCarPartsAndCost() {
        return carPartsAndCost;
    }

    public void setCarPartsAndCost(Map<String, Object> carPartsAndCost) {
        this.carPartsAndCost = carPartsAndCost;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Timestamp getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Timestamp serviceDate) {
        this.serviceDate = serviceDate;
    }
}//end of class...
