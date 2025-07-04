package com.mech.app.specialmethods;

import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.servicesrequest.ServiceTypesRecord;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Arrays;
import java.util.List;

public class ComponentLoader {
    private static DAO DATA_ACCESS_MODEL = new DAO();

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

    public static void setServiceTypes(ComboBox<String> comboBox, int shopId) {
        // Sort the items alphabetically before setting them
        var result = DATA_ACCESS_MODEL.getServiceTypeByShopId(shopId).stream().map(ServiceTypesRecord::name).toList();
        comboBox.setItems(result);
    }

    public static List<String> getJobStatusList() {
        return List.of("On hold", "In Progress", "Awaiting parts", "Completed");
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


    public static void setUrgencyLevel(ComboBox<String> urgencyLevel) {
        urgencyLevel.setItems(List.of("Normal", "Important", "Very Urgent"));
    }
}//end fo class...
