package com.mech.app.components.customer;

import com.mech.app.components.CustomDialog;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.models.CustomerModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.customers.AddCustomerView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.awt.*;

public class CustomerComponents {
    private static CustomDialog dialog;
    private static VerticalLayout contentLayout;
    private final CustomerModel DATA_MODEL = new CustomerModel();

    public CustomerComponents() {
        contentLayout = new VerticalLayout();
        dialog = new CustomDialog();
        contentLayout.addClassNames("dialog-content-layout");
    }

    // THIS COMPONENT WHEN INVOKED WILL OPEN A DIALOG MERELY TO SHOW THE CUSTOMER ACCOUNT DETAILS
    public void CustomerAccountDialog(CustomersDataProvider dataProvider) {

    }





    // THIS COMPONENT WHEN INVOKED WILL OPEN A DIALOG MERELY TO SHOW THE CUSTOMER CAR DETAILS
    public void addCarAction(int customerId) {
        ComboBox<String> carComboBox = new ComboBox<>("Select Car");
        TextField modelField = new TextField("Model");
        TextField yearField = new TextField("Year");
        TextField plateField = new TextField("Plate Number");
        var addButton = new Button("Add", LineAwesomeIcon.PLUS_SOLID.create());
        addButton.addClassNames("default-button-style");

        carComboBox.setRequired(true);
        modelField.setRequired(true);
        yearField.setRequired(true);
        plateField.setRequired(true);

        yearField.setMaxLength(4);
        yearField.setPattern("\\d{4}");
        yearField.setHelperText("Enter a valid year (e.g., 2023)");


        ComponentLoader.setCarBrands(carComboBox);
        var formLayout = new FormLayout(carComboBox, modelField, yearField, plateField, addButton);
        FormLayout.ResponsiveStep single = new FormLayout.ResponsiveStep("0", 1);
        FormLayout.ResponsiveStep multi = new FormLayout.ResponsiveStep("768px", 5);
        formLayout.setResponsiveSteps(single, multi);
        formLayout.addClassNames("form-layout-style");
        
        contentLayout.add(formLayout, carsGrid(customerId));
        contentLayout.addClassName("customer-car-dialog");
//        dialog = new CustomDialog();
        dialog.defaultDialogBox(contentLayout, "Customer Cars", "Add a new or update car for customer ID: " + customerId);
    }

    private Grid<CarDataProvider> carsGrid(int customerId) {
        var carsData = DATA_MODEL.fetchCustomerCarsById(customerId);
        Grid<CarDataProvider> grid = new Grid<>(CarDataProvider.class, false);
        grid.addClassName("alternative-grid-style");
        grid.addColumn(CarDataProvider::getBrand).setHeader("Brand").setSortable(true);
        grid.addColumn(CarDataProvider::getModel).setHeader("Model").setSortable(true);
        grid.addColumn(CarDataProvider::getCarYear).setHeader("Year").setSortable(true);
        grid.addColumn(CarDataProvider::getPlateNumber).setHeader("Plate Number").setSortable(true);
        grid.setHeightFull();
        grid.setItemDetailsRenderer(carActionsRenderer());
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setItems(carsData);
        return grid;
    }

    private Renderer<CarDataProvider> carActionsRenderer() {
        return new ComponentRenderer<>(data -> {
            VerticalLayout layout = new VerticalLayout(new H4("Update Car"));

            return layout;
        });
    }

}//end of class...


