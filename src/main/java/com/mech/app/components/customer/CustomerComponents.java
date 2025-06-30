package com.mech.app.components.customer;

import com.mech.app.components.CustomDialog;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.servicesrequest.ServicesDataProvider;
import com.mech.app.models.CustomerModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.sql.Date;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void serviceRequestDialog(CustomersDataProvider dataProvider) {
//        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.addClassNames("service-request-dialog");
        contentLayout.setWidthFull();

        Paragraph nameLabel = new Paragraph("Customer Name: ");
        nameLabel.addClassName("sr-customer-label");
        H4 nameValue = new H4(dataProvider.getName());
        HorizontalLayout nameAndLabelLayout = new HorizontalLayout(nameLabel, nameValue);
        nameAndLabelLayout.setWidthFull();
        nameAndLabelLayout.addClassName("request-name-and-label-layout");

        Span sectionHeader1 = new Span("Vehicle Information");
        ComboBox<String> vehicleSelector = new ComboBox<>("Select Vehicle");
        vehicleSelector.addClassNames("combo-box-style");

        TextField modelField = new TextField("Model", "Model", "C24");
        TextField plateNumber = new TextField("Plate Number", dataProvider.getEmail(), "GT-0000");
        modelField.addClassNames("input-style");
        plateNumber.setClassName("input-style");
        modelField.setReadOnly(true);
        plateNumber.setReadOnly(true);
        vehicleSelector.setRequired(true);

        //load customer vehicle params
        AtomicInteger vehicleId = new AtomicInteger();
        var vehicleInformation = DATA_MODEL.fetchCustomerCarInformation();
        var brands = vehicleInformation.stream().filter(
                        item -> item.getCustomerId() == dataProvider.getCustomerId())
                .map(CarDataProvider::getBrand).toList();

        vehicleSelector.setItems(brands);

        vehicleSelector.addValueChangeListener(e -> {
            String selectedBrand = e.getValue();
            if (selectedBrand == null) return;
            vehicleInformation.stream()
                    .filter(item -> selectedBrand.equals(item.getBrand()))
                    .findFirst()
                    .ifPresent(item -> {
                        plateNumber.setValue(item.getPlateNumber());
                        modelField.setValue(item.getModel());
                        vehicleSelector.setHelperText("Vehicle Id: " + item.getRecordId());
                        vehicleId.setRelease(item.getRecordId());
                    });
        });

        FormLayout formLayout1 = new FormLayout();
        formLayout1.add(sectionHeader1, 3);
        formLayout1.add(vehicleSelector, modelField, plateNumber);
        formLayout1.addClassNames("service-request-dialog-form-layout", "request-dialog-form1");

        Span sectionHeader2 = new Span("Service Details");
        ComboBox<String> serviceSelector = new ComboBox<>("Pick a service");
        serviceSelector.setRequired(true);
        ComponentLoader.setServiceTypes(serviceSelector, dataProvider.getShopId());

        ComboBox<String> urgencyLevel = new ComboBox<>("Urgency Level");
        ComponentLoader.setUrgencyLevel(urgencyLevel);
        urgencyLevel.setValue("Normal");

        var servicePrice = new NumberField("Service Cost");
        servicePrice.setReadOnly(true);
        servicePrice.addClassNames("input-style", "money-field");
        servicePrice.setPrefixComponent(new H5("Ghc"));

        //set service price based on selected service type...
        AtomicInteger serviceId = new AtomicInteger();
        serviceSelector.addValueChangeListener(e -> {
            String selectedItem = e.getValue();
            if (selectedItem.isEmpty()) return;
            //filter to get data associated to the selected service type...
            DATA_MODEL.getServiceTypeByShopId(dataProvider.getShopId())
                    .stream()
                    .filter(item -> item.name().equals(selectedItem))
                    .findFirst()
                    .ifPresent(item -> {
                        serviceId.set(item.recordId());
                        servicePrice.setValue(item.cost());
                    });
            serviceSelector.setHelperText("Service Id: " + serviceId.get());
        });

        TextArea problemDescField = new TextArea("Please describe the issue with the vehicle in detail");
        problemDescField.setRequired(true);
        var consentChecker = new Checkbox("Pickup Or Drop Off");
        consentChecker.addClassNames("check-box-style");

        DatePicker datePicker = new DatePicker("Preferred Date(Optional)");
        datePicker.setInitialPosition(LocalDate.now());
        datePicker.setMin(LocalDate.now());
        datePicker.addClassNames("date-picker-style");

        FormLayout formLayout2 = new FormLayout();
        formLayout2.addClassNames("service-request-dialog-form-layout", "request-dialog-form2");
        formLayout2.add(sectionHeader2, 2);
        formLayout2.add(serviceSelector, servicePrice);
        formLayout2.add(problemDescField, 2);
        formLayout2.add(datePicker, urgencyLevel);
        formLayout2.add(consentChecker, 2);

        //CREATE RESPONSIVE STEPS
        FormLayout.ResponsiveStep oneCul = new FormLayout.ResponsiveStep("0", 1);
        FormLayout.ResponsiveStep twoCul = new FormLayout.ResponsiveStep("768px", 2);
        FormLayout.ResponsiveStep threeCul = new FormLayout.ResponsiveStep("768px", 3);

        //implementation of button click event to process and save form data to service requst table..
        Button requestButton = new Button("Submit Request", VaadinIcon.CHECK_CIRCLE_O.create(),
                e -> {
                    dialog = new CustomDialog();
                    if (serviceSelector.isEmpty() || vehicleSelector.isEmpty() || problemDescField.isEmpty()) {
                        dialog.showWarningNotification("Please fill out all required fields");
                        serviceSelector.setInvalid(serviceSelector.isEmpty());
                        vehicleSelector.setInvalid(vehicleSelector.isEmpty());
                        problemDescField.setInvalid(problemDescField.isEmpty());
                    } else {
                        var requestDataProvider = new ServicesDataProvider.ServiceRequestRecord(
                                dataProvider.getCustomerId(), vehicleId.get(), serviceId.get(), servicePrice.getValue(),
                                problemDescField.getValue(), Date.valueOf(datePicker.getValue()), urgencyLevel.getValue(),
                                consentChecker.getValue(), "new", dataProvider.getUserId());

                        var saveDialog = dialog.showSaveDialog("BOOK SERVICE REQUEST", MessageLoaders.confirmationMessage("submit booking request for this client"));
                        saveDialog.addConfirmListener(event -> {
                            UI.getCurrent().access(() -> {

                                //make a call to the data model
                                int response = DATA_MODEL.bookServiceRequest(requestDataProvider);
                                if (response > 0) {
                                    dialog.showSuccessNotification(MessageLoaders.successMessage());
                                    String msgBody = "New service request booked for " + dataProvider.getName() +
                                            " for car with registration no: " + plateNumber.getValue();
                                    var logger = new NotificationRecords("SERVICE REQUEST BOOKING", msgBody, dataProvider.getUserId(), dataProvider.getShopId());
                                    DATA_MODEL.logNotification(logger);

                                    //clear required fields for new booking if so demands...
                                    serviceSelector.setValue(null);
                                    vehicleSelector.clear();
                                    servicePrice.clear();
                                    problemDescField.clear();
                                } else dialog.showErrorNotification("Failed to book this service request for client");
                            });
                        });
                    }
                });

        requestButton.addClassNames("default-button-style");
        requestButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

//        consentChecker.addValueChangeListener(e -> {
//            if (e.getValue()) {
//                requestButton.setEnabled(true);
//                requestButton.addClassNames("disable-button-style");
//            } else requestButton.setEnabled(false);
//            requestButton.removeClassName("disable-button-style");
//        });

        //set forml-ayout breakpoint
        formLayout2.setResponsiveSteps(oneCul, twoCul);
        formLayout1.setResponsiveSteps(oneCul, threeCul);

        contentLayout.add(nameAndLabelLayout, formLayout1, formLayout2, requestButton);

        dialog = new CustomDialog();
        dialog.defaultDialogBox(contentLayout, "New Service Request", "Fill in the details below to log a new service request");
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
        grid.setHeight("500px");
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


