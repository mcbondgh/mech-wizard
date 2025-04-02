package com.mech.app.views.customers;

import com.mech.app.components.HeaderComponent;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.enums.GenderEnums;
import com.mech.app.specialmethods.ComponentLoader;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Add Customer")
@Route(value = "/view/add-customer")
public class AddCustomerView extends VerticalLayout implements BeforeEnterObserver {

    private TextField nameField = new TextField("Full Name", "Kofi Mensah");
    private final TextField mobileNumberField = new TextField("Mobile Number", "020000000");
    private final TextField digitalAddressField = new TextField("Digital Address", "GH-0000-0000");
    private final EmailField emailField = new EmailField("Email Address", "kofimensah@exampl.com");
    private final TextField otherNumberField = new TextField("Other Number", "02700000000");
    private final ComboBox<GenderEnums> genderSelector = new ComboBox<>("Gender", GenderEnums.FEMALE, GenderEnums.MALE);
    private final ComboBox<String> brandSelector = new ComboBox<>("Car Brand");
    private final TextField modelField = new TextField("Car Model");
    private final IntegerField yearField = new IntegerField("Car Year");
    private final TextArea informationField = new TextArea("Description(Optional)", "Add additional information(optional)");
    private final TextField plateNumberField = new TextField("Plate Number", "GR-03434-25");
    private final Grid<CarDataProvider> carsGrid = new Grid<>();
    private final Button backButton = new Button("Close Form");
    private final Button saveButton = new Button("Save", LineAwesomeIcon.SAVE.create());


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    public void onAttach(AttachEvent listener) {
        ComponentLoader.setCarBrands(brandSelector);
        genderSelector.addClassNames("combo-box-style");
        informationField.setAutocapitalize(Autocapitalize.CHARACTERS);
        informationField.setClearButtonVisible(true);
        informationField.addClassNames("item-text-area");

        brandSelector.addClassNames("combo-box-style");
        yearField.addClassNames("input-style");

        var pageHeader = new HeaderComponent().textHeader("Customer Registration", "Register a new customer with their car details here...");
        add(pageHeader, pageBodySection());
    }


    /*******************************************************************************************************************
     REFERENCE METHODS IMPLEMENTATION
     *******************************************************************************************************************/

    /*******************************************************************************************************************
     PAGE CONTENT SECTION
     *******************************************************************************************************************/
    private VerticalLayout pageBodySection() {
        VerticalLayout layout = new VerticalLayout(customerInfoSection(), carInfoSection());
        layout.addClassNames("page-body-layout", "add-customer-page-body");

        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT RENDERERS SECTION
     *******************************************************************************************************************/
    private Component customerInfoSection() {
        Header headerText = new Header(new Span("Personal Information"));
        headerText.addClassName("inner-header-text");

        Section layout = new Section(headerText);
        layout.addClassNames("customer-info-area");
        layout.setWidthFull();

        VerticalLayout verticalLayout = new VerticalLayout(nameField,
                genderSelector, mobileNumberField, otherNumberField,
                emailField, digitalAddressField, informationField);
        verticalLayout.addClassNames("customer-input-area");

        layout.add(verticalLayout);
        return layout;
    }

    private Component carInfoSection() {
        Header headerText = new Header(new Span("Car Information"));
        headerText.addClassName("inner-header-text");

        Section layout = new Section(headerText);
        layout.addClassNames("customer-info-area");
        layout.setWidthFull();

        Button addButton = new Button(LineAwesomeIcon.PLUS_SOLID.create());
        addButton.setEnabled(false);
        addButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        addButton.addClassNames("add-car-info-button");
        addButton.getStyle().setMargin(LumoUtility.Margin.AUTO);

        HorizontalLayout formLayout = new HorizontalLayout(brandSelector, modelField, yearField, plateNumberField, addButton);
        formLayout.addClassNames("car-info-box");

        var buttonsContainer = new FlexLayout(saveButton, backButton);
        backButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        buttonsContainer.addClassNames("buttons-box");
        buttonsContainer.setWidthFull();
        buttonsContainer.setJustifyContentMode(JustifyContentMode.END);
        buttonsContainer.setAlignContent(FlexLayout.ContentAlignment.CENTER);

        layout.add(formLayout, configureCarGrid(), buttonsContainer);

        formLayout.getElement().addEventListener("mouseover", e -> {
            try {
                addButton.setEnabled(
                        !(brandSelector.getValue().isEmpty() || modelField.isEmpty() || yearField.isEmpty() || plateNumberField.isEmpty())
                );
            } catch (Exception ignore) {

            }
        });

        //ACTION EVENT IMPLEMENTATION
        addButton.addSingleClickListener(event -> {
            AtomicInteger counter = new AtomicInteger(0);
            carsGrid.getListDataView().addItem(
                    new CarDataProvider(counter.incrementAndGet(), brandSelector.getValue(),
                            modelField.getValue(), plateNumberField.getValue(), yearField.getValue())
            );
            event.getSource().setEnabled(false);
        });

        backButton.addSingleClickListener(e-> UI.getCurrent().navigate(CustomersView.class));

        return layout;
    }

    private Component configureCarGrid() {
        carsGrid.addClassNames("alternative-grid-style");
        carsGrid.setWidthFull();
        carsGrid.getStyle().setHeight("250px");

        carsGrid.addColumn(CarDataProvider::getCustomerId).setHeader("Customer Id");
        carsGrid.addColumn(CarDataProvider::getBrand).setHeader("Car Brand");
        carsGrid.addColumn(CarDataProvider::getModel).setHeader("Car Model");
        carsGrid.addColumn(CarDataProvider::getCarYear).setHeader("Year");
        carsGrid.addColumn(CarDataProvider::getPlateNumber).setHeader("Plate Number");
        carsGrid.addColumn(removeCarButtonRenderer());

        return carsGrid;
    }

    private Renderer<CarDataProvider> removeCarButtonRenderer() {
        return new ComponentRenderer<>(data -> {
            var removeButton = new Button("❌");
            removeButton.addClassNames("item-delete-button");
            removeButton.addSingleClickListener(event -> {
                carsGrid.getListDataView().removeItem(data);
            });
            return removeButton;
        });
    }


}//END OF CLASS...
