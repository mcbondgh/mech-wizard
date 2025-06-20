package com.mech.app.views.employees;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.HeaderComponent;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.configfiles.secutiry.DataEncryptor;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.employees.EmployeesDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.users.UsersDataProvider;
import com.mech.app.enums.GenderEnums;
import com.mech.app.models.EmployeeModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@PageTitle("Employees")
@Route(value = "employees", layout = MainLayout.class)
@Menu(order = 7, icon = LineAwesomeIconUrl.USER_PLUS_SOLID)
public class EmployeesView extends Composite<VerticalLayout> implements BeforeEnterObserver, AfterNavigationObserver {

    private final Grid<EmployeesDataProvider.EmployeesRecord> employeesGrid = new Grid<>();
    private final TextField searchField = new TextField("", "Search by name, position or mobile number");
    private final TextField nameField = new TextField("Employee Name");
    private final IntegerField mobileNumber = new IntegerField("Mobile Number");
    private final TextField addressField = new TextField("Digital Address");
    private final TextField cardNumberField = new TextField("Card Number");
    private final ComboBox<String> cardSelector = new ComboBox<>("Card Type");
    private final ComboBox<GenderEnums> genderSelector = new ComboBox<>("Gender");
    private final EmailField emailField = new EmailField("Email Address");
    private final ComboBox<String> skillSelector = new ComboBox<>("Select Skill");
    private final TextArea descriptionBox = new TextArea("Description(Optional)");
    private final ComboBox<String> roleSelector = new ComboBox<>("Role");
    private final DatePicker dateJoinedPicker = new DatePicker("Date Joined");
    private final Button addEmployeeButton = new Button("Add Employee", LineAwesomeIcon.SAVE.create());
    private final EmployeesDataProvider EMP_DATA_PROVIDER;
    private final EmployeeModel DATA_MODEL;
    private final UsersDataProvider USER_DATA_PROVIDER;
    private CustomDialog DIALOG_BOX;
    private static final AtomicInteger USER_ID = new AtomicInteger();
    private static final AtomicInteger SHOP_ID = new AtomicInteger();

    public EmployeesView() {
        EMP_DATA_PROVIDER = new EmployeesDataProvider();
        DATA_MODEL = new EmployeeModel();
        USER_DATA_PROVIDER = new UsersDataProvider();
        getContent().setHeightFull();
        getContent().setWidthFull();
        getContent().add(
                pageHeader(),
                pageBody()
        );
    }

    @Override
    public void onAttach(AttachEvent event){
        ComponentLoader.loadMechanicSkill(skillSelector);
        ComponentLoader.setRoles(roleSelector);
        ComponentLoader.setCardTypes(cardSelector);
        genderSelector.setItems(GenderEnums.MALE, GenderEnums.FEMALE);

        genderSelector.setValue(GenderEnums.MALE);
        cardSelector.setValue("Ghana Card");

        nameField.addClassNames("input-style", "emp-name-field");
        emailField.addClassNames("input-style", "emp-email-field");
        addressField.addClassNames("input-style", "aemp-ddress-field");
        cardSelector.addClassNames("combo-box", "emp-card-type-selector");
        skillSelector.addClassNames("combo-box", "emp-position-selector");
        mobileNumber.addClassNames("input-style", "emp-number-field");
        genderSelector.addClassNames("combo-box", "emp-gender-selector");
        cardNumberField.addClassNames("input-style", "emp-card-number-field");
        descriptionBox.addClassNames("input-style", "emp-description-field");
        dateJoinedPicker.addClassNames("date-picker","emp-joined-date-picker");
        addEmployeeButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        addEmployeeButton.addClassNames("default-button-style");

        mobileNumber.setMax(1000000000);
        nameField.setRequired(true);
        emailField.setRequired(true);
        addressField.setRequired(true);
        cardSelector.setRequired(true);
        cardNumberField.setRequired(true);
        skillSelector.setRequired(true);
        mobileNumber.setRequired(true);
        genderSelector.setRequired(true);
        dateJoinedPicker.setRequired(true);

    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        USER_ID.set(SessionManager.DEFAULT_USER_ID);
        SHOP_ID.set(SessionManager.DEFAULT_SHOP_ID);
    }
    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }

    /*******************************************************************************************************************
     HEADER SECTION
     *******************************************************************************************************************/
    private Component pageHeader() {
        var header=  new HeaderComponent().textHeader("Employees List", "Manage your team members and their roles");

        MenuBar menuBar = new MenuBar();
        menuBar.getStyle().setBackgroundColor("transparent");
        menuBar.addThemeVariants(MenuBarVariant.LUMO_PRIMARY, MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_TERTIARY);

        var addButton = menuBar.addItem(" New Employee", "Show and add new employee form.");
        addButton.addComponentAsFirst(LineAwesomeIcon.PLUS_SOLID.create());
        addButton.addClassNames("add-employee-button");
        addButton.addClickListener(menuItemClickEvent -> {
            showEmployeeFormDialog().open();
        });

        FlexLayout layout = new FlexLayout( header , menuBar);
        layout.addClassNames("employees-header-container");
        layout.setWidthFull();
        layout.setAlignContent(FlexLayout.ContentAlignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return layout;
    }

    /*******************************************************************************************************************
     BODY SECTION
     *******************************************************************************************************************/
    private VerticalLayout pageBody() {
        Button export = new Button(LineAwesomeIcon.FILE_CSV_SOLID.create());
        export.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        export.addClassNames("export-button");
        export.setTooltipText("Export employees list as .csv file");

        searchField.setPrefixComponent(LineAwesomeIcon.SEARCH_SOLID.create());
        searchField.setClearButtonVisible(true);

        //add filter to the table grid
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(input -> {
           employeesGrid.getListDataView().addFilter(filter -> {
               if (input.getValue().isEmpty()) return true;
               boolean matchesName = input.getValue().toLowerCase().contains(filter.name().toLowerCase());
               boolean matchesMail = input.getValue().toLowerCase().contains(filter.email().toLowerCase());
               return matchesName || matchesMail;
           }).refreshAll();
        });

        HorizontalLayout formLayout = new HorizontalLayout(searchField, export);
        formLayout.addClassName("filter-and-export-button-layout");
        formLayout.setWidthFull();

        VerticalLayout layout = new VerticalLayout(formLayout, configureEmployeesGrid());
        layout.addClassNames("employees-pagebody-layout");
        layout.setWidthFull();
        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT RENDERS
     *******************************************************************************************************************/

    private Dialog showEmployeeFormDialog() {
        var headerText = new H4("Add New Employee");
        var subTitle = new Span("Enter the details for the new employee. Click add when you're done.");
        var container1 = new Div(headerText, subTitle);
        container1.setWidthFull();

        var closeButton = new Span(VaadinIcon.CLOSE_SMALL.create());
        closeButton.addClassNames("dialog-close-button");

        FlexLayout headerComponent = new FlexLayout(container1, closeButton);
        headerComponent.addClassNames("dialog-header-component", "add-employee-dialog-component");
        headerComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerComponent.setAlignContent(FlexLayout.ContentAlignment.CENTER);
        headerComponent.setWidthFull();

        Div buttonContainer = new Div(addEmployeeButton);
        buttonContainer.addClassNames("buttons-container");

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.addClassName("add-employee-form-layout-component");
        formLayout.add(nameField, mobileNumber, emailField, addressField,
                genderSelector, skillSelector, dateJoinedPicker,
                cardSelector, cardNumberField, roleSelector, descriptionBox);
        formLayout.add(buttonContainer);

        Dialog dialog = new Dialog(formLayout);
        dialog.addClassNames("dialog-box", "add-employee-dialog-component");
        dialog.getHeader().getElement().getThemeList().add("badge contrast pill small");

        dialog.getHeader().add(headerComponent);


        //CLICK EVENTS IMPLEMENTATIONS
        closeButton.addClickListener(e -> UI.getCurrent().refreshCurrentRoute(false));
        dialog.setCloseOnOutsideClick(false);

        formLayout.getElement().addEventListener("mouseover", e -> {
            if (formHasEmptyFields()) {
                addEmployeeButton.setEnabled(false);
                addEmployeeButton.setClassName("disable-button-style");
                addEmployeeButton.setTooltipText("Please fill all required fields before adding an employee.");
            } else {
                addEmployeeButton.removeClassName("disable-button-style");
                addEmployeeButton.setEnabled(true);
                addEmployeeButton.setTooltipText("Click to add the new employee.");
            }
        });

        addEmployeeButton.addSingleClickListener(event -> processFormData());
        return dialog;
    }

    private Component configureEmployeesGrid() {
        employeesGrid.setSizeFull();
        employeesGrid.addClassNames("default-grid-style");
        employeesGrid.addColumn(EmployeesDataProvider.EmployeesRecord::id).setHeader("Id").setSortable(true);
        employeesGrid.addColumn(EmployeesDataProvider.EmployeesRecord::name).setHeader("Full Name");
        employeesGrid.addColumn(EmployeesDataProvider.EmployeesRecord::number).setHeader("Mobile Number");
        employeesGrid.addColumn(EmployeesDataProvider.EmployeesRecord::email).setHeader("Email");
        employeesGrid.addColumn(roleComponent()).setHeader("Role").setTextAlign(ColumnTextAlign.CENTER);
        employeesGrid.addComponentColumn(data -> statusComponent(data.active())).setHeader("Status").setTextAlign(ColumnTextAlign.CENTER);
        employeesGrid.addColumn(employeesActionButton());
        employeesGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
        employeesGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        employeesGrid.setEmptyStateComponent(ComponentLoader.emptyGridComponent("Employees list is empty."));
        employeesGrid.setItems(employeesRecords());
        return employeesGrid;
    }

    private Renderer<EmployeesDataProvider.EmployeesRecord> roleComponent() {
        return new ComponentRenderer<>(data -> {
            Span span = new Span(data.usersData().role());
            if (Objects.equals(data.usersData().role(), "Admin")) {
                span.getElement().getThemeList().add("badge success pill small primary");
            } else if (Objects.equals(data.usersData().role(), "Receptionist")) {
                span.getElement().getThemeList().add("badge warning pill small primary");
            } else {
                span.getElement().getThemeList().add("badge tertiary pill small primary");
            }
            return span;
        });
    }

    private Component statusComponent(boolean status) {
        Span span = new Span();
        if (status) {
            span.setText("Active");
            span.getElement().getThemeList().add("badge success pill small");
        }else {
            span.setText("Inactive");
            span.getElement().getThemeList().add("badge error pill small");
        }
        return span;
    }

    private Renderer<EmployeesDataProvider.EmployeesRecord> employeesActionButton(){
        return new ComponentRenderer<>(data -> {
            MenuBar menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_TERTIARY);
            menuBar.getStyle().setBackgroundColor("transparent").setMargin("auto");

            var updateButton = menuBar.addItem(" Update", e -> {
                Notification.show("Update button clicked for " + data.usersData().username());
            });
            updateButton.addComponentAsFirst(LineAwesomeIcon.PENCIL_ALT_SOLID.create());
            updateButton.addClassNames("item-update-button");

            var userButton = menuBar.addItem(" Edit User" , e -> {
                Notification.show("Edit button clicked for " + data.usersData().username());
            });
            userButton.addComponentAsFirst(LineAwesomeIcon.USER_EDIT_SOLID.create());
            userButton.addClassName("item-update-button");

            var deleteButton = menuBar.addItem(" Delete", e -> {
                Notification.show("Delete button clicked for " + data.usersData().username());
            });
            deleteButton.addComponentAsFirst(LineAwesomeIcon.TRASH_SOLID.create());
            deleteButton.addClassNames("item-delete-button");

            return menuBar;
        });

    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/
    private List<EmployeesDataProvider.EmployeesRecord> employeesRecords() {
        return DATA_MODEL.fetchAllMechanics();
    }
    private void clearFormFields() {
        mobileNumber.clear();
        nameField.clear();
        emailField.clear();
        addressField.clear();
        cardSelector.clear();
        cardNumberField.clear();
        skillSelector.clear();
        mobileNumber.clear();
        genderSelector.clear();
        dateJoinedPicker.clear();
        descriptionBox.clear();
    }

    private boolean formHasEmptyFields() {
        return nameField.isEmpty() || emailField.isEmpty() || addressField.isEmpty() || cardNumberField.isEmpty() ||
                cardSelector.isEmpty() || skillSelector.isEmpty() || mobileNumber.isEmpty() ||
                genderSelector.isEmpty() || dateJoinedPicker.isEmpty() || roleSelector.isEmpty();
    }

    private void processFormData() {
        if (!formHasEmptyFields()) {
            // set the form field values to the data provider
            EMP_DATA_PROVIDER.setFullName(nameField.getValue());
            EMP_DATA_PROVIDER.setEmployeeSkill(skillSelector.getValue());
            EMP_DATA_PROVIDER.setMobileNumber(mobileNumber.getValue().toString());
            EMP_DATA_PROVIDER.setEmail(emailField.getValue());
            EMP_DATA_PROVIDER.setCardNumber(cardNumberField.getValue());
            EMP_DATA_PROVIDER.setCardType(cardSelector.getValue());
            EMP_DATA_PROVIDER.setActive(true);
            EMP_DATA_PROVIDER.setDescription(descriptionBox.getValue());
            EMP_DATA_PROVIDER.setDateJoined(Date.valueOf(dateJoinedPicker.getValue()));
            EMP_DATA_PROVIDER.setDigitalAddress(addressField.getValue());
            EMP_DATA_PROVIDER.setGender(genderSelector.getValue().toString());
            EMP_DATA_PROVIDER.setShopId(SHOP_ID.get());

            //set USER DETAILS
            USER_DATA_PROVIDER.setUsername(emailField.getValue());
            USER_DATA_PROVIDER.setPassword(DataEncryptor.defaultPassword());
            USER_DATA_PROVIDER.setRole(roleSelector.getValue());
            USER_DATA_PROVIDER.setShop_id(SessionManager.DEFAULT_SHOP_ID);
            
            //set notification message
            String msg = "New employee " + nameField.getValue() + " has been added successfully today " +
                    "at " + LocalTime.now().toString().substring(0, 8) + " as a " + roleSelector.getValue() + ".";
            var notificationRecords = new NotificationRecords("EMPLOYEE REGISTRATION", msg, 1, SHOP_ID.get());

            UI currentUI = UI.getCurrent();

            String content = MessageLoaders.confirmationMessage( "add " + nameField.getValue());
            DIALOG_BOX = new CustomDialog();
            DIALOG_BOX.showSaveDialog("ADD EMPLOYEE", content).addConfirmListener(e -> {
                currentUI.access(() ->
                {
                    var responseValue = new AtomicInteger(DATA_MODEL.registerEmployee(EMP_DATA_PROVIDER));
                    responseValue.getAndAdd(DATA_MODEL.registerUser(USER_DATA_PROVIDER));

                    //check if data was saved successfully
                    if (responseValue.get() == 2) {
                        DIALOG_BOX.showSuccessNotification(MessageLoaders.successMessage());
                        DATA_MODEL.logNotification(notificationRecords);
                        clearFormFields();
                    }else {
                        DIALOG_BOX.showErrorNotification(MessageLoaders.errorMessage("refer to Sys admin for assistance"));
                    }
                });
            });
        }
    }

}//end of class...
