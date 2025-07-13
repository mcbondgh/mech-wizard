package com.mech.app.dataproviders.customers;

import java.sql.Timestamp;

public record CustomerFeedbackRecords(String jobNo, String date, String name, String vehicle, String vin, String stars, String review, String response) {

    public String rateValue(){
        return switch(stars){
            case "1" -> "⭐";
            case "2" -> "⭐⭐";
            case "3" -> "⭐⭐⭐";
            case "4" -> "⭐⭐⭐⭐";
            case "5" -> "⭐⭐⭐⭐⭐";
            default -> "0";
        };
    }

    public String jobNoAndDate(){
        return "JC-" +jobNo + " - " + date;
    }
    public String vehicleAndVin(){
        return vehicle + " - " + vin;
    }
}
