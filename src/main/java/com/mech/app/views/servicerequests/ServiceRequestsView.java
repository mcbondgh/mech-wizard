package com.mech.app.views.servicerequests;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.FormColumns;
import com.mech.app.components.HeaderComponent;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.specialmethods.ComponentLoader;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@PageTitle("Service Requests")
@Route("/view/service-requests")
@Menu(order = 5, icon = LineAwesomeIconUrl.TOOLBOX_SOLID)
public class ServiceRequestsView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private static H4 layoutHeaderText;
    private static final boolean isVisible = true;
    private final Scroller scroller = new Scroller();

    public ServiceRequestsView() {
        getContent().setSizeUndefined();
        getContent().setWidthFull();
        getContent().add(
                pageBody()
        );
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    public void onAttach(AttachEvent event) {
        scroller.setVisible(false);
    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/

    private List<CustomersDataProvider.CustomersRecord> sampleData() {
        var carOne = List.of("BMW", "Toyota", "Ford", "Kantanka");
        var carTwo = List.of("Range", "Nissan");
        var carThree = List.of("Nissan", "Opel");
        var carFour = List.of("Benz", "Kantanka", "Audi");
        return List.of(
                new CustomersDataProvider.CustomersRecord("Kofi Mensah", "0949490440", "9493949349", "someton@osod.com", carOne, true),
                new CustomersDataProvider.CustomersRecord("Sarah sdsd", "0949490440", "9493949349", "someton@osod.com", carThree, false),
                new CustomersDataProvider.CustomersRecord("James dssds", "0949490440", "9493949349", "sdsd@osod.com", carTwo, true),
                new CustomersDataProvider.CustomersRecord("Rita Ddsdsd", "0949490440", "9493949349", "someton@osod.com", carFour, false)
        );
    }

    private record serviceRequestSampleData(String serviceNo, String name, String status, String vehicle, String type,
                                            String date, String description) {
    }

    private List<serviceRequestSampleData> serviceRequestSampleData() {
        return List.of(
                new serviceRequestSampleData("SVN-00203", "Kofi Menash", "Approved", "SUV-4344", "Oil Change", LocalDate.now().toString(),
                        "Just need a general oil change for my car to maintain its shape and keep it running smoothly. Check that for me"),
                new serviceRequestSampleData("SVN-00204", "Sarah Johnson", "Cancelled", "SUV-1234", "Brake Repair", LocalDate.now().toString(),
                        "The brakes are making a squeaking noise. Please inspect and repair them."),
                new serviceRequestSampleData("SVN-00205", "James Smith", "Pending", "SUV-5678", "Tire Rotation", LocalDate.now().toString(),
                        "I need a tire rotation service. The front tires are wearing out faster than the rear ones.")
        );
    }

    /*******************************************************************************************************************
     PAGE BODY VIEW
     *******************************************************************************************************************/
    private Component pageBody() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeUndefined();
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        layout.addClassNames("service-request-body-box");
        layout.add(createServiceForm(), serviceRequestList());
        return layout;
    }


    private Scroller createServiceForm() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.addClassNames("service-request-form-layout");

        scroller.setContent(layout);
        scroller.addThemeVariants(ScrollerVariant.LUMO_OVERFLOW_INDICATORS);
        scroller.addClassName("request-form-scroll-layout");

        layoutHeaderText = new H4("Create New Service");
        Section header = new Section(LineAwesomeIcon.TOOLS_SOLID.create(), layoutHeaderText);
        header.addClassNames("add-service-request-header");

        Button saveButton = new Button("Submit Request", LineAwesomeIcon.SAVE.create());
        saveButton.addClassNames("default-btn-style");
        saveButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        saveButton.setWidth("fit-content");

        var closeButton = new Button("Close Form");
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        closeButton.addClassNames("default-btn-style", "cancel-button");
        closeButton.setWidth("fit-content");

        TextField plateNumberField = new TextField("Plate Number");
        NumberField serviceCostField = new NumberField("Cost (Ghc)");
        ComboBox<String> carType = new ComboBox<>("Select Car");
        ComboBox<String> serviceTypeSelector = new ComboBox<>("Service Type");
        TextArea problemDescriptionField = new TextArea("Problem Description");
        Checkbox priorityCheckbox = new Checkbox("Very Urgent Service", false);

        var carAndPlateBox = new FlexLayout(carType, plateNumberField);
        var serviceTypeAndCostBox = new FlexLayout(serviceTypeSelector, serviceCostField);

        //set class names
        plateNumberField.addClassNames("input-style");
        serviceCostField.addClassNames("input-style");
        plateNumberField.setReadOnly(true);
        serviceCostField.setReadOnly(true);
        carType.setEnabled(false);
        carType.addClassNames("combo-box-style");
        serviceTypeSelector.addClassNames("combo-box-style");
        priorityCheckbox.addClassNames("default-check-box-style");
        carAndPlateBox.addClassNames("service-selector-box");
        serviceTypeAndCostBox.addClassNames("service-selector-box");

        ComponentLoader.setServiceTypes(serviceTypeSelector);

        var buttonsContainer = FormColumns.buttonsBox(saveButton, closeButton);

        layout.add(
                header,
                customersListBoxComponent(carType),
                carAndPlateBox, serviceTypeAndCostBox, problemDescriptionField, priorityCheckbox,
                buttonsContainer);

        //ACTION EVENT LISTENERS IMPLEMENTATION...
        closeButton.addClickListener(e -> {
            scroller.setVisible(false);
        });

        return scroller;
    }

    private Component serviceRequestList() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.addClassNames("service-request-list-layout");

        layoutHeaderText = new H4("Service Request List");
        Section header = new Section(layoutHeaderText, pageHeaderSection());
        header.addClassNames("view-header");

        TextField filterField = new TextField();
        var filterSection = new HeaderComponent().searchFieldComponent(filterField);

        filterField.addValueChangeListener(event -> {

        });

        ListBox<serviceRequestSampleData> serviceList = new ListBox<>();
        serviceList.addClassNames("service-request-list");
        serviceList.setSizeUndefined();
        serviceList.setItems(serviceRequestSampleData());
        serviceList.setRenderer(serviceRequestComponentRenderer());

        layout.add(header, filterSection, serviceList);
        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT RENDERERS
     *******************************************************************************************************************/

    private Component pageHeaderSection() {
        Button addButton = new Button("Create Service", VaadinIcon.PLUS.create());
        addButton.setWidth("fit-content");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Section layout = new Section(addButton);
        layout.setWidthFull();
        layout.addClassNames("service-request-button-box");
        layout.getStyle().setPadding("var(--lumo-space-m)");

        addButton.addClickListener(e -> {
            scroller.setVisible(true);
        });
        return layout;
    }

    /* This method returns a vertical component that contains the implementation and behaviour of
    a listBox that displays all active customers for the shop which allows to find a customer by either name or number.
     */
    private VerticalLayout customersListBoxComponent(ComboBox<String> component) {
        ListBox<CustomersDataProvider.CustomersRecord> customersList = new ListBox<>();
        customersList.addClassNames("list-box-component");
        customersList.setSizeUndefined();
        customersList.setWidth("100%");

        customersList.setItems(sampleData());
        customersList.setRenderer(customersListItems());

        var section2 = new VerticalLayout();
        section2.addClassNames("section2");

        var filterField = new TextField();
        filterField.setPlaceholder("Search by mobile number or name");
        var filterSection = new HeaderComponent().searchFieldComponent(filterField);
        filterSection.addClassNames("service-request-customer-filter-box");
        section2.add(filterSection, customersList);

        //set filtering for loaded customers
        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        filterField.addValueChangeListener(input -> {
            customersList.getListDataView().setFilter(filter -> {
                if (input.getValue().isEmpty()) return true;
                boolean matchesName = filter.name().toLowerCase().contains(input.getValue().toLowerCase());
                boolean matchesNumbers = filter.mobileNumber().contains(input.getValue()) || filter.otherNumber().contains(input.getValue());
                return matchesName || matchesNumbers;
            });
        });

        // Load cars selector with the number of cars owned/registered by customer...
        customersList.addValueChangeListener(event -> {
            component.setEnabled(true);
            component.setItems(event.getValue().cars());
        });

        return section2;
    }

    private ComponentRenderer<Div, CustomersDataProvider.CustomersRecord> customersListItems() {
        return new ComponentRenderer<>(dataProvider -> {
            Div container = new Div();
            Paragraph name = new Paragraph(dataProvider.name());
            Span span = new Span(dataProvider.email() + ", " + dataProvider.otherNumber());

            container.add(name, span);
            container.setWidthFull();
            container.getStyle().setBoxSizing(Style.BoxSizing.BORDER_BOX);
            container.addClassNames("customers-list-item-renderer-box");
            return container;
        });
    }

    //##################################################################################################
    private ComponentRenderer<Component, serviceRequestSampleData> serviceRequestComponentRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
            VerticalLayout layout = new VerticalLayout();

            var cancelButton = new Button("Cancel", LineAwesomeIcon.TIMES_SOLID.create());
            var updateButton = new Button("Update", LineAwesomeIcon.RECYCLE_SOLID.create());
            var approveButton = new Button("Approve", LineAwesomeIcon.CHECK_CIRCLE.create());
            var deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
            HorizontalLayout menuBar = new HorizontalLayout(approveButton, updateButton, cancelButton, deleteButton);
            menuBar.addClassNames("service-operation-buttons");

            cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            updateButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            approveButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

            Span statusIndicator = new Span(dataProvider.status);
            if (Objects.equals(statusIndicator.getText(), "Approved")) {
                statusIndicator.getElement().getThemeList().add("badge success pill small");
                deleteButton.setVisible(false);
                approveButton.setVisible(false);
            } else if (Objects.equals(statusIndicator.getText(), "Pending")) {
                statusIndicator.getElement().getThemeList().add("badge warning pill small");
                deleteButton.setVisible(false);
            } else {
                statusIndicator.getElement().getThemeList().add("badge error pill small");
                deleteButton.setVisible(true);
                cancelButton.setVisible(false);
                approveButton.setVisible(false);
                updateButton.setVisible(false);
            }

            var icon = VaadinIcon.TOOLBOX.create();
            HorizontalLayout serviceNoAndStatusBox = new HorizontalLayout(new H3(dataProvider.serviceNo), statusIndicator);
            Section nameAndServiceDetail = new Section(serviceNoAndStatusBox, new Span(String.format("%s (%s)", dataProvider.name, dataProvider.vehicle)));
            Section serviceTypeAndDateBox = new Section(new H5(dataProvider.type), new Span(dataProvider.date));

            Section iconAndService = new Section(icon, nameAndServiceDetail);

            iconAndService.addClassNames("request-icon-and-service-box");
            serviceNoAndStatusBox.addClassNames("service-no-and-status-box");
            serviceTypeAndDateBox.addClassNames("service-type-and-date-box");
            icon.addClassNames("request-icon");
            nameAndServiceDetail.addClassNames("service-name-and-details-box");

            FlexLayout blockOne = new FlexLayout(iconAndService, serviceTypeAndDateBox);
            Div blockTwo = new Div(dataProvider.description);
            FlexLayout blockThree = new FlexLayout(menuBar);

            blockOne.addClassNames("content-blocks", "block-on");
            blockTwo.addClassNames("content-blocks", "block-two");
            blockThree.addClassNames("content-blocks", "block-three");

            layout.add(blockOne, blockTwo, blockThree);
            layout.setWidthFull();
            layout.addClassNames("service-request-component-layout");
            return layout;
        });
    }


}//end of class...
