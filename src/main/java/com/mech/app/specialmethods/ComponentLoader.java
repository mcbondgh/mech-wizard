package com.mech.app.specialmethods;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Arrays;
import java.util.List;

public class ComponentLoader {

    public static void loadMechanicSkill(ComboBox<String> comboBox) {
        var items = List.of(
                "Master Mechanic",
                "Receptionist",
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
        comboBox.setItems(items.stream().sorted().toList());
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

    public static Component emptyGridComponent(String content) {
        Div div = new Div(content);
        div.addClassName("empty-grid-component");
        div.getStyle().setPadding("20px")
                .setTextAlign(Style.TextAlign.CENTER)
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setFontSize("var(--lumo-font-size-l)")
                .setColor("var(--lumo-secondary-text-color)")
                .setMargin("auto");
        return div;
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

    public static void enableOrDisableComponent(Component target, boolean condition) {
        if (condition) {
                target.getElement().setEnabled(false);
                target.setClassName("disable-button-style");
            } else {
                target.removeClassName("disable-button-style");
                target.getElement().setEnabled(true);
        }
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
        comboBox.setItems(items.stream().sorted().toList());
    }

    public static void setRoles(ComboBox<String> comboBox) {
        comboBox.setItems(List.of(
                "Mechanic",
                "Receptionist",
                "Admin"
        ));
    }


}//end fo class...
