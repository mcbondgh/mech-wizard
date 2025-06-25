package com.mech.app.dataproviders.customers;

import com.mech.app.dataproviders.cars.CarDataProvider;

import java.sql.Timestamp;
import java.util.List;

public class CustomersDataProvider {
    private int customerId, userId, shopId;
    private String name, mobileNumber, otherNumber, email, digitalAddress;
    private String gender, comments;
    private boolean status;
    private Timestamp dateJoined;

    public record CustomersRecord(String name, String mobileNumber, String otherNumber, String email, List<String> cars, boolean status){}

    public CustomersDataProvider(){}
    public CustomersDataProvider(int customerId, int userId, int shopId, String name, String mobileNumber,
                                 String otherNumber, String email, String digitalAddress,
                                 String gender, String comments, boolean status, Timestamp dateJoined) {
        this.customerId = customerId;
        this.userId = userId;
        this.shopId = shopId;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.otherNumber = otherNumber;
        this.email = email;
        this.digitalAddress = digitalAddress;
        this.gender = gender;
        this.comments = comments;
        this.status = status;
        this.dateJoined = dateJoined;
    }

    //SETERS AND GETTERS
    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOtherNumber() {
        return otherNumber;
    }

    public void setOtherNumber(String otherNumber) {
        this.otherNumber = otherNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDigitalAddress() {
        return digitalAddress;
    }

    public void setDigitalAddress(String digitalAddress) {
        this.digitalAddress = digitalAddress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Timestamp getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Timestamp dateJoined) {
        this.dateJoined = dateJoined;
    }
}// of class...
