package com.mech.app.components.service_requests;

import com.mech.app.components.CustomDialog;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.employees.EmployeesDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.servicesrequest.ServiceTypesRecord;
import com.mech.app.dataproviders.servicesrequest.ServicesDataProvider;
import com.mech.app.models.ServiceRequestModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.dom.Style;
import org.jetbrains.annotations.NotNull;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class ServiceRequestComponents {

    private static CustomDialog dialog;
    private static FormLayout parentLayout;
    private static final DAO DAO = new DAO();
    private static ServiceRequestModel DATA_MODEL = new ServiceRequestModel();
    ;

    public ServiceRequestComponents() {
    }

    private static List<EmployeesDataProvider.EmployeesRecord> mechanicsData() {
        return DAO.fetchAllMechanics()
                .stream().
                filter(item -> item.usersData().role().equalsIgnoreCase("mechanic"))
                .toList();
    }

    //create a card component to display service status and other relevant information
    //CARD COMPONENT IMPLEMENTATION....
    public static HorizontalLayout blockFourCard(String label, String value) {
        var labelItem = new Span(label);
        var valueItem = new H6(value == null ? "Unspecified" : value);

        switch (value) {
            case "normal" -> valueItem.getElement().getThemeList().add("badge pill small");
            case "very urgent" -> valueItem.getElement().getThemeList().add("badge error primary pill small");
            case "important" -> valueItem.getElement().getThemeList().add("badge warning pill small");
            case null -> {
            }
            default -> {
//                valueItem.getElement().getThemeList().add("badge warning primary pill small");
            }
        }
        HorizontalLayout layout = new HorizontalLayout(labelItem, valueItem);
        layout.setWidthFull();
        layout.addClassNames("p-5", "block-four-layout");
        return layout;
    }

    //DIALOG TO ASSIGN A MECHANIC
    public static void assignMechanicDialog(int serviceId, int userId, int shopId) {
        dialog = new CustomDialog();
        parentLayout = new FormLayout();
        parentLayout.addClassNames("service-component-form-layout");
        parentLayout.setWidth("500px");

        ComboBox<String> mechanicPicker = new ComboBox<>("Pick a Mechanic");
        mechanicPicker.setRequired(true);
        mechanicPicker.addClassNames("combo-box-style");

        //LOAD DIALOG BOX WITH MECHANIC NAMES
        var mechanicNames = mechanicsData().stream().map(EmployeesDataProvider.EmployeesRecord::name).toList();
        mechanicPicker.setItems(mechanicNames);

        var statusValue = new H6();
        var statusBox = new FlexLayout(new Span("Status: "), statusValue);
        statusBox.setWidthFull();

        var skillsValue = new H6();
        var skillBox = new FlexLayout(new Span("Skill: "), skillsValue);
        skillBox.setWidthFull();

        var jobCountBox = new FlexLayout(new Span("Jobs Assigned"));
        jobCountBox.setWidthFull();

        Span infoText = new Span("Mechanic Information");
        infoText.getStyle().set("color", "--subtitle-color")
                .setFontSize("14px")
                .setWidth("100%")
                .setTextAlign(Style.TextAlign.CENTER)
                .setBorderBottom("1px solid #ddd")
                .setPaddingBottom("5px");

        VerticalLayout detailsBox = new VerticalLayout(infoText, statusBox, skillBox, new Hr());
        detailsBox.addClassNames("mechanic-dialog-inner-bbox");

        String title = "Assign Mechanic";
        String subtitle = "Assign a qualified mechanic to service no: " + serviceId;

        //Add a value change listener to the mechanic picker to provide other relevant
        //information about the mechanic
        AtomicInteger mechanicId = new AtomicInteger(0);
        mechanicPicker.addValueChangeListener(event -> {
            var value = event.getValue();
            detailsBox.setEnabled(true);
            UI.getCurrent().access(() -> {
                for (var item : mechanicsData()) {
                    if (item.name().equalsIgnoreCase(value)) {
                        mechanicPicker.setHelperText("Mechanic Id: " + item.id());
                        mechanicId.set(item.id());
                        statusValue.setText(item.active() ? "Active" : "Suspended");
                        skillsValue.setText(item.skill());
                    }
                }
            });
        });
        Button submitButton = new Button("Assign Mechanic ", LineAwesomeIcon.CHECK_CIRCLE.create(), e -> {
            executeMechanicAssignmentOperation(mechanicId.get(), serviceId, mechanicPicker.getValue(), userId, shopId);
        });
        submitButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON);
        submitButton.addClassNames("default-button-style");

        detailsBox.setWidthFull();
        detailsBox.addAndExpand(submitButton);
        detailsBox.setEnabled(false);

        parentLayout.add(mechanicPicker, detailsBox);
        dialog.defaultDialogBox(parentLayout, title, subtitle);
    }

    private static void executeMechanicAssignmentOperation(int mechanicId, int serviceId, String mechName, int userID, int shopId) {
        dialog = new CustomDialog();
        var confirmDialog = dialog.showUpdateDialog("Assign Mechanic", MessageLoaders.confirmationMessage("assign mechanic to this service"));

        String logMsg = String.format("%s has been assigned to service number %s as special operator for the job", mechName, serviceId);
        NotificationRecords logRecord = new NotificationRecords("MECHANIC ASSIGNMENT", logMsg, userID, shopId);

        confirmDialog.addConfirmListener(e -> {
            UI ui = UI.getCurrent();
            ui.access(() -> {
                int responseStatus = DATA_MODEL.assignMechanicModel(mechanicId, serviceId);
                if (responseStatus > 0) {
                    //create a job card for this service after the request is booked...
                    new ServiceRequestModel().createJobCard(serviceId);
                    dialog.showSuccessNotification(MessageLoaders.successMessage());
                    DATA_MODEL.logNotification(logRecord);
                    ui.refreshCurrentRoute(false);
                } else
                    dialog.showErrorNotification(MessageLoaders.errorMessage("Failed to assign mechanic to this service."));
            });
        });

    }

    public static void updateServiceDialog(@NotNull ServicesDataProvider.BookedServicesRecord dataObj, int userId, int shopId) {
        dialog = new CustomDialog();
        parentLayout = new FormLayout();
        parentLayout.addClassNames("service-component-form-layout", "update-service-request-form-layout");

        String title = "Update Service No: " + dataObj.serviceId();
        String subtitle = "Update Booked service requests";

        //create form components to collect data...
        H5 serviceTitle = new H5("Service Information");
        parentLayout.add(serviceTitle);

        TextArea textArea = new TextArea("Job Description *", dataObj.desc(), "Job description goes here and is mandatory...");
        textArea.addClassNames("input-style");
        textArea.setRequired(true);

        ComboBox<String> urgencySelector = new ComboBox<>("Urgency Level");
        ComponentLoader.setUrgencyLevel(urgencySelector);
        urgencySelector.setClassName("combo-box-style");
        urgencySelector.setValue(dataObj.urgencyLevel());

        ComboBox<String> serviceSelector = new ComboBox<>("Service Type *");
        serviceSelector.addClassNames("combo-box-style");
        serviceSelector.setRequired(true);
        ComponentLoader.setServiceTypes(serviceSelector, shopId);
        serviceSelector.setValue(dataObj.serviceType());

        DatePicker datePicker = new DatePicker();
        datePicker.addClassName("date-picker-style");
        datePicker.setValue(LocalDate.parse(dataObj.preferredDate()));

        Checkbox checkbox = new Checkbox("Pickup/Drop-off Service");
        checkbox.addClassNames("check-box-style");
        checkbox.setValue(dataObj.pickupStatus());

//        ComboBox<String> mechanicPicker = new ComboBox<>("Select Mechanic");
//        mechanicPicker.addClassNames("combo-box-style");
//        //LOAD DIALOG BOX WITH MECHANIC NAMES
//        var mechanicNames = mechanicsData().stream().map(EmployeesDataProvider.EmployeesRecord::name).toList();
//        mechanicPicker.setItems(mechanicNames);
//        mechanicPicker.setValue(dataObj.mechanic());
//        mechanicPicker.setRequired(!(dataObj.mechanic() == null));

        var serviceTypeId = new AtomicInteger();
        var serviceResultValue = DATA_MODEL.getServiceTypeByShopId(shopId).stream().filter(item ->
                item.name().equalsIgnoreCase(dataObj.serviceType())).findFirst().get().recordId();
        serviceSelector.setHelperText("Service Id: " + serviceResultValue);
        serviceTypeId.set(serviceResultValue);
        serviceSelector.addValueChangeListener(e -> {
            DATA_MODEL.getServiceTypeByShopId(shopId).stream().filter(item -> item.name().equalsIgnoreCase(e.getValue()))
                    .findFirst().ifPresent(i -> {
                        serviceTypeId.setRelease(i.recordId());
                        serviceSelector.setHelperText("Service Id " + i.recordId());
                    });
        });

        Button updateButton = new Button("Update Service", LineAwesomeIcon.RECYCLE_SOLID.create(), e -> {
            boolean checkFields = serviceSelector.isEmpty() || textArea.isEmpty();
            if (checkFields) {
                new CustomDialog().showWarningNotification("Kindly fill out all required fields");
                serviceSelector.setInvalid(serviceSelector.isEmpty());
                textArea.setInvalid(textArea.isEmpty());
                return;
            }

            //CREATE A MAP TO PARSE DATA TO THE METHOD BELOW
            Map<String, Object> formData = new HashMap<>();
            formData.putIfAbsent("serviceId", dataObj.serviceId());
            formData.put("dateValue", datePicker.getValue());
            formData.put("pickupValue", checkbox.getValue());
            formData.put("desc", textArea.getValue());
            formData.put("typeId", serviceTypeId);
            formData.put("userId", userId);
            formData.put("shopId", shopId);
            formData.put("level", urgencySelector.getValue());
            formData.put("serviceNo", dataObj.serviceNo());
            processServiceUpdateOperation(formData);

        });
        updateButton.addClassNames("default-button-style");
        updateButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON);
        updateButton.setWidthFull();

        parentLayout.add(serviceSelector, datePicker, urgencySelector, checkbox, textArea, new Hr(), updateButton);
        dialog.defaultDialogBox(parentLayout, title, subtitle);
    }

    private static void processServiceUpdateOperation(Map<String, Object> dataObj) {

        var serviceNo = dataObj.get("serviceNo");
        var userId = Integer.parseInt(dataObj.get("userId").toString());
        var shopId = Integer.parseInt(dataObj.get("shopId").toString());

        String logMsg = String.format("Service request for service number %s has been updated today", serviceNo);
        dialog = new CustomDialog();
        var confirmDialog = dialog.showUpdateDialog("UPDATE SERVICE", MessageLoaders.confirmationMessage("update service details"));

        UI ui = UI.getCurrent();
        confirmDialog.addConfirmListener(e -> {
            if (DATA_MODEL.updateServiceRequest(dataObj) > 0) {
                NotificationRecords records = new NotificationRecords("SERVICE REQUEST UPDATED", logMsg, userId, shopId);
                DATA_MODEL.logNotification(records);
                ui.refreshCurrentRoute(false);
            } else dialog.showErrorNotification(MessageLoaders.errorMessage("Failed to update service request data."));
        });
    }

    public static void serviceTerminationDialog(int serviceId, String serviceNo, int userId, int shopId) {
        dialog = new CustomDialog();
        String title = String.format("Terminate Service Request: %s", serviceNo);
        String subtitle = "Terminate Service request by providing your reason for termination.";

        TextArea textArea = new TextArea("Provide your reason *", "Kindly provide your reason for terminating this service request.");
        textArea.setRequired(true);
        textArea.setInvalid(textArea.isEmpty());
        textArea.setMinRows(5);
        textArea.setWidthFull();

        var parentLayout = new VerticalLayout();
        parentLayout.addClassNames("cancel-dialog-parent-layout");
        parentLayout.setWidth("500px");

        var cancelButton = new Button("Terminate Request", VaadinIcon.CLOSE_SMALL.create(), e -> {
            dialog = new CustomDialog();
            if (!textArea.isInvalid()) {
                if (DATA_MODEL.cancelServiceRequest(serviceId, textArea.getValue()) > 0) {
                    String logMsg = String.format("Service request no. %s has been terminated with reasons for the operation.", serviceNo);
                    NotificationRecords recordObj = new NotificationRecords("REQUEST TERMINATION", logMsg, userId, shopId);
                    DATA_MODEL.logNotification(recordObj);
                    UI.getCurrent().refreshCurrentRoute(false);
                } else dialog.showErrorNotification("Failed to cancel this service request.");
            }

        });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        parentLayout.add(textArea, new Hr(), cancelButton);
        parentLayout.expand(textArea, cancelButton);
        dialog.defaultDialogBox(parentLayout, title, subtitle);
    }

}//end of class.
