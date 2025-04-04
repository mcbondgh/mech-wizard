package com.mech.app.specialmethods;

import com.vaadin.flow.component.combobox.ComboBox;

import javax.swing.*;
import java.util.List;

public class ComponentLoader {

    public static void loadMechanicPositions(ComboBox<String> comboBox) {
        var items = List.of(
                "Master Mechanic",
                "Lead Technician",
                "Service Technician",
                "Electrician",
                "EV Specialist",
                "Apprentice Mechanic",
                "Diagnostic Specialist",
                "Alignment Technician",
                "Tire Technician",
                "Lube Technician",
                "Quality Control Inspector"
        );
        comboBox.setItems(items);
    }

    public static void setCarBrands(ComboBox<String> comboBox) {
        var items = List.of(
                "Toyota", "Honda", "Hyundai", "Kia", "Ford",
                "Mercedes-Benz", "Nissan", "Kantanka", "Volkswagen", "BMW", "Audi",
                "Chevrolet", "Mitsubishi", "Renault", "Suzuki", "Volvo", "Jeep",
                "Land Rover", "Lexus", "Opel", "Porsche", "Acura", "Dodge", "Jaguar"
        );
        comboBox.setItems(items.stream().sorted().toList());

    }

    public static void setServiceTypes(ComboBox<String> comboBox) {
        var items = List.of(
                "Oil and Filter Change",
                "Brake Repair and Service",
                "Tire Rotation and Alignment",
                "Engine Diagnostics and Repair",
                "Electrical Works", "Other",
                "Air Conditioning (A/C) Repair",
                "Exhaust System Repair",
                "General Vehicle Inspection"
        );
        comboBox.setItems(items.stream().sorted().toList());
    }

    public static List<String> getJobStatusList() {
        return List.of("New", "In Progress", "Waiting for parts", "Completed");
    }

    public static List<String> getPaymentMethods() {
        return List.of("Cash", "Mobile Money", "Card | POS");
    }

    public static void setStatusTypes(ComboBox<String> comboBox) {
        var items = List.of(
            "Active",
            "Suspended",
            "On Leave"
        );
        comboBox.setItems(items);
    }

    public static void setCardTypes(ComboBox<String> comboBox) {
        var items = List.of(
                "Voter Id",
                "Ghana Card",
                "Passport"
        );
        items.stream().sorted();
        comboBox.setItems(items);
    }

    public static void setRoles(ComboBox<String> comboBox) {
        comboBox.setItems(List.of(
                "Customer",
                "Mechanic",
                "Accountant",
                "Admin"
        ));
    }


}//end fo class...
