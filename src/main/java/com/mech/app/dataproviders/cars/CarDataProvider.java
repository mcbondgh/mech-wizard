package com.mech.app.dataproviders.cars;

public class CarDataProvider {
    private int recordIId, customerId;
    private String brand, model, plateNumber;
    private int carYear;
    private boolean isDeleted, isUpdated;

    public CarDataProvider() {
    }

    public CarDataProvider(int recordIId, int customerId, String brand, String model, String plateNumber, int carYear, boolean isDeleted, boolean isUpdated) {
        this.recordIId = recordIId;
        this.customerId = customerId;
        this.brand = brand;
        this.model = model;
        this.plateNumber = plateNumber;
        this.carYear = carYear;
        this.isDeleted = isDeleted;
        this.isUpdated = isUpdated;
    }
    public CarDataProvider(int customerId, String brand, String model, String plateNumber, int carYear) {
        this.brand = brand;
        this.customerId = customerId;
        this.model = model;
        this.plateNumber = plateNumber;
        this.carYear = carYear;
    }


    public int getRecordIId() {
        return recordIId;
    }

    public void setRecordIId(int recordIId) {
        this.recordIId = recordIId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getCarYear() {
        return carYear;
    }

    public void setCarYear(int carYear) {
        this.carYear = carYear;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }
};
