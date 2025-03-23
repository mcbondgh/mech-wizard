package com.mech.app.views.employees;

import com.mech.app.components.HeaderComponent;
import com.mech.app.dataproviders.employees.EmployeesDataProvider;
import com.mech.app.enums.GenderEnums;
import com.mech.app.specialmethods.ComponentLoader;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@PageTitle("Employees")
@Route("employees")
@Menu(order = 6, icon = LineAwesomeIconUrl.USER_PLUS_SOLID)
public class EmployeesView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private static final Grid<EmployeesDataProvider.EmployeesRecord> employeesGrid = new Grid<>();
    private final TextField searchField = new TextField("", "Search by name, position or mobile number");
    private final TextField nameField = new TextField("Employee Name");
    private final TextField mobileNumber = new TextField("Mobile Number");
    private final TextField addressField = new TextField("Digital Address");
    private final TextField cardNumberField = new TextField("Card Number");
    private final ComboBox<String> cardSelector = new ComboBox<>("Card Type");
    private final ComboBox<GenderEnums> genderSelector = new ComboBox<>("Gender");
    private final EmailField emailField = new EmailField("Email Address");
    private final ComboBox<String> positionSelector = new ComboBox<>("Position");
    private final TextArea descriptionBox = new TextArea("Description(Optional)");
    private final ComboBox<String> statusSelector = new ComboBox<>("Status");
    private final DatePicker dateJoinedPicker = new DatePicker("Date Joined");
    private final Button addEmployeeButton = new Button("Add Employee", LineAwesomeIcon.SAVE.create());

    public EmployeesView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
        getContent().add(
                pageHeader(),
                pageBody()
        );
    }

    @Override
    public void onAttach(AttachEvent event){
        ComponentLoader.loadMechanicPositions(positionSelector);
        ComponentLoader.setStatusTypes(statusSelector);
        ComponentLoader.setCardTypes(cardSelector);
        genderSelector.setItems(GenderEnums.MALE, GenderEnums.FEMALE);

        statusSelector.setValue("Active");
        genderSelector.setValue(GenderEnums.MALE);
        cardSelector.setValue("Ghana Card");

        nameField.addClassNames("input-style", "emp-name-field");
        emailField.addClassNames("input-style", "emp-email-field");
        addressField.addClassNames("input-style", "aemp-ddress-field");
        cardSelector.addClassNames("combo-box", "emp-card-type-selector");
        positionSelector.addClassNames("combo-box", "emp-position-selector");
        mobileNumber.addClassNames("input-style", "emp-number-field");
        genderSelector.addClassNames("combo-box", "emp-gender-selector");
        cardNumberField.addClassNames("input-style", "emp-card-number-field");
        descriptionBox.addClassNames("input-style", "emp-description-field");
        dateJoinedPicker.addClassNames("date-picker","emp-joined-date-picker");
        addEmployeeButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    /*******************************************************************************************************************
     HEADER SECTION
     *******************************************************************************************************************/
    private Component pageHeader() {
        var header=  new HeaderComponent().textHeader("Employees List", "Manage your team members and their roles");

        MenuBar menuBar = new MenuBar();
        menuBar.getStyle().setBackgroundColor("transparent");
        menuBar.addThemeVariants(MenuBarVariant.LUMO_PRIMARY, MenuBarVariant.LUMO_SMALL);

        var addButton = menuBar.addItem("Add Employee", "Show and add new employee form.");
        addButton.addComponentAsFirst(LineAwesomeIcon.PLUS_CIRCLE_SOLID.create());
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
                genderSelector, positionSelector, dateJoinedPicker,
                cardSelector, cardNumberField,statusSelector, descriptionBox);

        Dialog dialog = new Dialog(formLayout);
        dialog.addClassNames("dialog-box", "add-employee-dialog-component");
        dialog.getHeader().getElement().getThemeList().add("badge contrast pill small");

        dialog.getHeader().add(headerComponent);
        dialog.getFooter().add(addEmployeeButton);


        //CLICK EVENTS IMPLEMENTATIONS
        closeButton.addClickListener(e -> dialog.close());
        dialog.addDetachListener(e -> {

        });

        formLayout.getElement().addEventListener("mouseover", e -> {

        });

        addEmployeeButton.addSingleClickListener(event -> {
            processFormData();
        });



        return dialog;
    }

    private Component configureEmployeesGrid() {
        employeesGrid.setSizeFull();
        employeesGrid.addClassNames("default-grid-style");
        employeesGrid.addColumn(EmployeesDataProvider.EmployeesRecord::id).setHeader("Id").setSortable(true);
        employeesGrid.addColumn(EmployeesDataProvider.EmployeesRecord::name).setHeader("Full Name");
        employeesGrid.addColumn(EmployeesDataProvider.EmployeesRecord::number).setHeader("Mobile Number");
        employeesGrid.addColumn(EmployeesDataProvider.EmployeesRecord::email).setHeader("Email");
        employeesGrid.addColumn(employeeStatusComponent()).setHeader("Status");
        employeesGrid.addColumn(employeesActionButton());

        employeesGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        return employeesGrid;
    }

    private Renderer<EmployeesDataProvider.EmployeesRecord> employeeStatusComponent() {
        return new ComponentRenderer<>(data -> {
            Span span = new Span(data.status());
            if (Objects.equals(data.status(), "Active")) {
                span.getElement().getThemeList().add("badge success pill small primary");
            } else if (Objects.equals(data.status(), "Suspended")) {
                span.getElement().getThemeList().add("badge warning pill small primary");
            } else {
                span.getElement().getThemeList().add("badge error pill small primary");
            }
            return span;
        });
    }

    private Renderer<EmployeesDataProvider.EmployeesRecord> employeesActionButton(){
        return new ComponentRenderer<>(data -> {
            MenuBar menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_TERTIARY);
            menuBar.getStyle().setBackgroundColor("transparent").setMargin("auto");
            var updateButton = menuBar.addItem("Update");
            updateButton.addComponentAsFirst(LineAwesomeIcon.PENCIL_ALT_SOLID.create());
            var deleteButton = menuBar.addItem("Delete");
            deleteButton.addComponentAsFirst(LineAwesomeIcon.TRASH_SOLID.create());

            updateButton.addClassNames("item-update-button");
            updateButton.getStyle().setMargin("auto");

            deleteButton.addClassNames("item-delete-button");
            deleteButton.getStyle().setMargin("auto");
            return menuBar;
        });

    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/

    private void processFormData() {
        AtomicInteger counter = new AtomicInteger(0);
        var dataList = new EmployeesDataProvider.EmployeesRecord(
                counter.incrementAndGet(),
                nameField.getValue(),
                mobileNumber.getValue(),
                emailField.getValue(),
                statusSelector.getValue()
        );
        employeesGrid.getListDataView().addItem(dataList).refreshAll();
    }

}//end of class...
