package com.mech.app.dataproviders.employees;

import com.mech.app.dataproviders.users.UsersDataProvider;

import java.sql.Date;
import java.sql.Timestamp;

public class EmployeesDataProvider {
    private int recordId, shopId, userId;
    private String fullName, mobileNumber, email, digitalAddress;
    private String gender, employeeSkill, cardNumber;
    private String cardType;
    private Date dateJoined;
    private String description;
    private Timestamp dateCreated;
    private boolean active, deleted;
    private UsersDataProvider usersData;

    public record EmployeesRecord(int id, String name, String number, String email,
                                  String address, String gender, String skill, String cardType,
                                  String cardNumber, Timestamp date, String description, boolean active,
                                  UsersDataProvider.usersRecord usersData
                                  ){}

    public int getShopId() {
        return shopId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    public String getEmployeeSkill() {
        return employeeSkill;
    }

    public void setEmployeeSkill(String employeeSkill) {
        this.employeeSkill = employeeSkill;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public UsersDataProvider getUsersData() {
        return usersData;
    }
    public void setUsersData(UsersDataProvider data) {
        usersData = data;
    }
}
