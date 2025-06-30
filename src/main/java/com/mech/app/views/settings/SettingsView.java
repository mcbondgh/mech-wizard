package com.mech.app.views.settings;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.FormColumns;
import com.mech.app.components.HeaderComponent;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.servicesrequest.ServiceTypesRecord;
import com.mech.app.models.settings.SettingsModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
//@Menu(order = 10, icon = LineAwesomeIconUrl.TOOLS_SOLID)
public class SettingsView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final TabSheet tabSheet = new TabSheet();
    private FormColumns responsiveForm;
    private final Grid<ServiceTypesRecord> serviceTypesGrid = new Grid<>();
    private static SettingsModel SETTINGS_MODE = new SettingsModel();
    private AtomicInteger SHOP_ID = new AtomicInteger(0);
    private AtomicInteger USER_ID = new AtomicInteger(0);

    public SettingsView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
        getContent().addClassName("page-body");
        SHOP_ID.set(SessionManager.DEFAULT_SHOP_ID);
        USER_ID.set(SessionManager.DEFAULT_USER_ID);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    public void onAttach(AttachEvent attachEvent) {
        tabSheet.addClassNames("tab-sheet-style");
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_BORDERED, TabSheetVariant.LUMO_TABS_SMALL, TabSheetVariant.LUMO_TABS_MINIMAL);
        getContent().add(headerSection(), bodySection());
    }

    /*******************************************************************************************************************
     HEADER SECTION
     *******************************************************************************************************************/
    private Component headerSection() {
        return new HeaderComponent().textHeader("Settings", "Manage your account and application preferences");
    }

    /*******************************************************************************************************************
     BODY SECTION
     *******************************************************************************************************************/
    private VerticalLayout bodySection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassNames("settings-body-container");
        layout.setWidthFull();

        Tab profileTab = new Tab("Profile");
        profileTab.addClassNames("settings-tab");
        profileTab.addComponentAsFirst(LineAwesomeIcon.USER_CLOCK_SOLID.create());
        tabSheet.add(profileTab, profileTabComponent());

        Tab companyTab = new Tab("Company");
        companyTab.addClassNames("settings-tab");
        companyTab.addComponentAsFirst(LineAwesomeIcon.BUILDING_SOLID.create());
        tabSheet.add(companyTab, companyTabComponent());

        Tab securityTab = new Tab("Security");
        securityTab.addComponentAsFirst(LineAwesomeIcon.LOCK_SOLID.create());
        tabSheet.add(securityTab, securityTabComponent());
        securityTab.addClassNames("settings-tab");

        Tab systemTab = new Tab("Service Types");
        systemTab.addClassNames("settings-tab");
        systemTab.addComponentAsFirst(LineAwesomeIcon.LIST_OL_SOLID.create());
        tabSheet.add(systemTab, serviceTypesTabComponent());

        layout.add(tabSheet);
        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT RENDERING SECTION
     *******************************************************************************************************************/
    private VerticalLayout profileTabComponent() {
        H3 headerText = new H3("Profile Settings");
        VerticalLayout layout = new VerticalLayout();
        Image image = new Image(LineAwesomeIconUrl.USER, "LOGO");
        image.addClassName("profile-image-style");

        TextField nameField = new TextField("Full Name", "Ama Mohammed Selassi");
        TextField numberField = new TextField("Mobile Number", "029999999");
        EmailField emailField = new EmailField("Email Address", "ama1234@example.com");
        TextField addressField = new TextField("Digital Address", "EK-0001-0001");
        PasswordField firstPassField = new PasswordField("Password", "*********");
        PasswordField secondPassField = new PasswordField("Confirm Password", "********");
        Button saveButton = new Button("Save Changes", LineAwesomeIcon.SAVE.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload imageUpload = new Upload(buffer);

        Section imageSection = new Section(image, imageUpload);

        //add class names to components
        nameField.addClassNames("input-style", "profile-name-field");
        numberField.addClassNames("input-style", "profile-number-field");
        emailField.addClassNames("input-style", "profile-email-field");
        addressField.addClassNames("input-style", "profile-address-field");
        saveButton.addClassNames("default-button-style");
        imageSection.addClassName("settings-profile-container");
        firstPassField.addClassNames("input-style", "profile-password-field");
        secondPassField.addClassNames("input-style", "profile-password-field");

//        FormLayout formLayout = new FormLayout();
//        responsiveForm = new FormColumns(formLayout );
//        responsiveForm.threeColumns();

        Section formLayout = new Section(nameField, numberField, emailField,
                addressField, firstPassField, secondPassField, saveButton);

        formLayout.addClassName("settings-profile-formlayout");

        layout.addClassNames("tab-content-container", "profile-tab-content");
        layout.setWidthFull();

        layout.add(headerText, imageSection, formLayout);
        return layout;
    }

    private VerticalLayout companyTabComponent() {
        H3 headerText = new H3("Company Settings");
        VerticalLayout layout = new VerticalLayout(headerText);
        layout.addClassNames("tab-content-container", "company-tab-content");

        var nameField = new TextField("Business Name");
        var emailField = new EmailField("Business Email");
        var numberField = new TextField("Business Number");
        var descriptionField = new TextArea("Business Description");
        var addressField = new TextField("Business Address");
        var weekDayTimeField = new TextField("Weekdays", "8:00AM - 8:00PM");
        var weekEndTimeField = new TextField("Weekends", "12:00PM -8:00PM");
        var saveButton = new Button("Save Changes", LineAwesomeIcon.SAVE.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);


        var timeHeaderText = new H5("Business Hours");
        var timeDescription = new Paragraph("Define When Your Shop Is Open For Service");
        var timeHeaderContainer = new Div(timeHeaderText, timeDescription);
        timeHeaderContainer.setWidthFull();
        var timeContainer = new VerticalLayout(timeHeaderContainer, weekDayTimeField, weekEndTimeField);
        timeContainer.setWidthFull();
        var buttonContainer = new Div(saveButton);

        //set class names
        timeContainer.addClassName("company-time-container");
        nameField.addClassNames("input-style", "name-field");
        timeContainer.addClassName("company-time-container");
        timeHeaderContainer.addClassNames("company-time-header-container");
        emailField.addClassNames("input-style", "email-field");
        descriptionField.addClassNames("input-style", "description-field");
        numberField.addClassNames("input-style", "number-field");
        weekEndTimeField.addClassNames("input-style", "weekend-time-field");
        weekDayTimeField.addClassNames("input-style", "weekend-time-field");
        buttonContainer.addClassNames("button-container", "company-button-container");
        buttonContainer.setWidthFull();
        buttonContainer.getStyle().setPadding("10px")
                .setMargin("5px")
                .setAlignItems(Style.AlignItems.CENTER);

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.addClassName("settings-form-layout");
        formLayout.getStyle().setLineHeight("20px");

        formLayout.add(nameField, numberField, emailField, addressField,
                descriptionField, timeContainer, buttonContainer);

        layout.add(formLayout);
        layout.setWidthFull();
        return layout;
    }

    private VerticalLayout securityTabComponent() {
        H3 headerText = new H3("Security Settings");
        VerticalLayout layout = new VerticalLayout(headerText);
        layout.addClassNames("tab-content-container", "security-tab-content");
        layout.setSizeFull();
        return layout;
    }

    private VerticalLayout serviceTypesTabComponent() {
        H3 headerText = new H3("Service Types");
        var paragraph = new Span("Define the types of services your shop offers and their associated costs");

        var nameField = new TextField("Service Name", "EV Service");
        var descriptionField = new TextField("Service Description", "eg All electrical works");
        var costField = new NumberField("Cost(Ghc)", "0.00");
        var addButton = new Button(LineAwesomeIcon.PLUS_CIRCLE_SOLID.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClassNames("default-button-style", "add-service-button");

        costField.setRequired(true);
        nameField.setRequired(true);

        HorizontalLayout formLayout = new HorizontalLayout(nameField, descriptionField, costField, addButton);
        formLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        formLayout.setWidthFull();

        //set class names...
        formLayout.addClassName("service-types-layout");
        nameField.addClassNames("input-style", "name-field");
        descriptionField.addClassNames("input-style", "description-field");
        costField.addClassNames("input-style", "cost-field");

        VerticalLayout layout = new VerticalLayout(headerText, paragraph, formLayout, serviceTypeGridConfiguration());
        layout.addClassNames("tab-content-container", "system-tab-content");
        layout.setWidthFull();

        //EVENT LISTENERS IMPLEMENTATION...
        addButton.setEnabled(false);
        nameField.setValueChangeMode(ValueChangeMode.EAGER);
        nameField.addValueChangeListener(event -> addButton.setEnabled(!event.getSource().isEmpty()));

        costField.setValueChangeMode(ValueChangeMode.EAGER);
        costField.addValueChangeListener(event -> {
            addButton.setEnabled(!costField.isInvalid());
        });

        addButton.addClickListener(event -> {
            addToServiceGrid(nameField.getValue(), descriptionField.getValue(), costField.getValue());
            addButton.setEnabled(false);
            nameField.clear();
            descriptionField.clear();
            costField.clear();
        });
        return layout;
    }

    private Grid<ServiceTypesRecord> serviceTypeGridConfiguration() {
        serviceTypesGrid.addClassNames("default-grid-style");
        serviceTypesGrid.addColumn(ServiceTypesRecord::name).setHeader("Service Name");
        serviceTypesGrid.addColumn(ServiceTypesRecord::description).setHeader("Description");
        serviceTypesGrid.addColumn(ServiceTypesRecord::cost).setHeader("Cost(Ghc)").setSortable(true);
        serviceTypesGrid.addComponentColumn(this::deleteServiceButton);
        serviceTypesGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        serviceTypesGrid.setSizeFull();
        serviceTypesGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COLUMN_BORDERS);

        serviceTypesGrid.setEmptyStateComponent(ComponentLoader.emptyGridComponent("No service type is found"));

        serviceTypesGrid.getEmptyStateComponent().getStyle().setTextAlign(Style.TextAlign.CENTER);
        serviceTypesGrid.setItems(loadGridData());

        return serviceTypesGrid;
    }

    //this button when invoked shall delete the source of service type from the list of services
    private Button deleteServiceButton(ServiceTypesRecord data) {
        Button deleteButton = new Button(LineAwesomeIcon.TRASH_SOLID.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> {
            serviceTypesGrid.getListDataView().removeItem(data);
        });
        return deleteButton;
    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     ******************************************************************************************************************

     * @return*/
    private List<ServiceTypesRecord> loadGridData() {
        return SETTINGS_MODE.getServiceTypeByShopId(SHOP_ID.get());
    }

    private void addToServiceGrid(String name, String description, double cost) {
        int response = SETTINGS_MODE.createNewServiceType(SHOP_ID.get(), name, description, cost, USER_ID.get());
        String message = name + " has been added your list of services";
        if(response > 0) {
            NotificationRecords notify = new NotificationRecords("SERVICE TYPE", message, USER_ID.get(), SHOP_ID.get());
            SETTINGS_MODE.logNotification(notify);
            serviceTypesGrid.setItems(loadGridData());
        }else new CustomDialog().showErrorNotification(MessageLoaders.errorMessage("Failed to add service type."));
    }
}//end of class...
