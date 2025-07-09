package com.mech.app.dataproviders.users;

public class UsersDataProvider {
    int record_id;
    int shop_id;
    boolean status;
    int emp_id;
    String username;
    String password;
    String role;

    public record usersRecord(int id, String username, String password, String role) {}

    public record LoginUserRecord(int id, int shopId, String username, String role, String name, String status, String password){}

    public UsersDataProvider() {
    }



    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public int getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(int emp_id) {
        this.emp_id = emp_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
