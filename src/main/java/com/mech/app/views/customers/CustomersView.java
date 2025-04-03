package com.mech.app.views.customers;

import com.mech.app.components.HeaderComponent;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.specialmethods.ComponentLoader;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PageTitle("Customers")
@Route("/view/customers")
@Menu(title = "Customers", order = 2, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
public class CustomersView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final Button addCustomerButton = new Button("Add Customer", LineAwesomeIcon.PLUS_SOLID.create());
    private final VerticalLayout pageLoadIndicator = new VerticalLayout();
    private final Grid<CustomersDataProvider.CustomersRecord> customersRecordGrid = new Grid<>();
    private final TextField filterField = new TextField("", "Search by name, mobile number or plate number");


    public CustomersView() {
        getContent().setSizeUndefined();
        getContent().addClassNames("page-content");

        pageLoadIndicator.setClassName("page-loader-indicator");
        pageLoadIndicator.add(new H4("Content Loading...."));

        var Header = new HeaderComponent().pageHeaderWithComponent("Registered Customers",
                "easily manage and register all your customers from here", addCustomerButton);
        getContent().add(Header);
        getContent().add(pageLoadIndicator, Header, pageBody());

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    public void onAttach(AttachEvent event) {
        setComponentProperties();
        getContent().remove(pageLoadIndicator);
    }


    /*******************************************************************************************************************
     PAGE BODY SECTION
     *******************************************************************************************************************/
    private VerticalLayout pageBody() {
        VerticalLayout layout = new VerticalLayout(filterField, configureCustomerGrid());
        layout.addClassNames("customers-filter-and-grid-layout");
        layout.setSizeUndefined();
        layout.setBoxSizing(BoxSizing.BORDER_BOX);

        return layout;
    }


    /*******************************************************************************************************************
     REFERENCE METHODS SECTION
     *******************************************************************************************************************/
    private void setComponentProperties() {
        addCustomerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        addCustomerButton.addClassNames("add-customer-button", "default-button-style");
        pageLoadIndicator.getStyle().setZIndex(100);

        //add navigation event to the add-customer-button
        addCustomerButton.addSingleClickListener(event -> UI.getCurrent().navigate(AddCustomerView.class));
    }

    private List<CustomersDataProvider.CustomersRecord> sampleData() {
        var carOne = List.of("BMW", "Toyota", "Ford", "Kantanka");
        var carTwo = List.of("Range", "Nissan");
        var carThree = List.of("Nissan", "Opel");
        return List.of(
                new CustomersDataProvider.CustomersRecord("Kofi Mensah", "0949490440", "9493949349", "someton@osod.com", carOne, true),
                new CustomersDataProvider.CustomersRecord("Sarah sdsd", "0949490440", "9493949349", "someton@osod.com", carThree, false),
                new CustomersDataProvider.CustomersRecord("James dssds", "0949490440", "9493949349", "sdsd@osod.com", carTwo, true),
                new CustomersDataProvider.CustomersRecord("Rita Ddsdsd", "0949490440", "9493949349", "someton@osod.com", carOne, false)
        );
    }

    /*******************************************************************************************************************
     COMPONENTS RENDERERS
     *******************************************************************************************************************/
    private Component configureCustomerGrid() {
        customersRecordGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COMPACT);
        customersRecordGrid.addClassNames("alternative-grid-style");

//      customersRecordGrid.setSelectionMode(Grid.SelectionMode.NONE);
        customersRecordGrid.setRowsDraggable(true);
        customersRecordGrid.setSizeUndefined();

        //set grid columns
        customersRecordGrid.addColumn(customerDetailsRenderer()).setHeader("CUSTOMER DETAILS");
        customersRecordGrid.addColumn(customerStatusRenderer()).setHeader("STATUS");
        customersRecordGrid.addColumn(actionButtonsRenderer()).setHeader("ACTION");
        customersRecordGrid.getColumns().forEach(col -> {
            col.setTextAlign(ColumnTextAlign.CENTER);
            col.setAutoWidth(true);
        });

        customersRecordGrid.setItems(sampleData());
        return customersRecordGrid;
    }

    private Renderer<CustomersDataProvider.CustomersRecord> customerDetailsRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.addClassNames("customers-details-renderer-box");

            Avatar avatar = new Avatar("CUS", LineAwesomeIconUrl.USER_CIRCLE);
            avatar.addClassName("customer-avator");
            var nameText = new H4(dataProvider.name());

            var vehiclesLayout = new FlexLayout();
            vehiclesLayout.addClassNames("vehicles-layout");

            dataProvider.cars().forEach(item -> {
                Span span = new Span(item  );
                span.addClassNames("car-names");
                vehiclesLayout.add(LineAwesomeIcon.CAR_SOLID.create(), span);
            });

            var nameAndCarsBox = new Section(nameText, vehiclesLayout);
            nameAndCarsBox.addClassNames("customers-details-grid-component", "customer-name-and-cars-box");
            nameAndCarsBox.setWidthFull();

            var itemOne = new FlexLayout(LineAwesomeIcon.SMS_SOLID.create(), new Span(dataProvider.email()));
            var itemTwo = new FlexLayout(LineAwesomeIcon.PHONE_SOLID.create(), new Span(dataProvider.mobileNumber()));
            var itemThree = new FlexLayout(LineAwesomeIcon.PHONE_SOLID.create(), new Span(dataProvider.otherNumber()));

            itemOne.getStyle().set("gap", "6px");
            itemTwo.getStyle().set("gap", "6px");
            itemThree.getStyle().set("gap", "6px");

            var contactBox = new Section(itemOne, itemTwo, itemThree);
            contactBox.addClassNames("customers-details-grid-component");

            layout.add(avatar, nameAndCarsBox, contactBox);
            return layout;
        });
    }


    private Renderer<CustomersDataProvider.CustomersRecord> customerStatusRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
            Span span = new Span("Placeholder");
            if (dataProvider.status()) {
                span.setText("Active");
                span.getElement().getThemeList().add("badge success primary pill small");
            } else {
                span.setText("Suspended");
                span.getElement().getThemeList().add("badge error primary pill small");
            }
            return span;
        });
    }

    private Renderer<CustomersDataProvider.CustomersRecord> actionButtonsRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
            MenuBar menuBar = new MenuBar();
            menuBar.addClassNames("default-menu-bar-style");
            menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_TERTIARY);
            menuBar.getStyle()
                    .setBackgroundColor("transparent")
                    .setTextDecoration("none")
                    .setWidth("auto");

            var IconsList = List.of(LineAwesomeIcon.PENCIL_ALT_SOLID.create(), LineAwesomeIcon.TRASH_SOLID.create(),
                    LineAwesomeIcon.CAR_SOLID.create(), LineAwesomeIcon.ID_CARD.create());
            var labels = List.of("Update", "Delete", "Add Car", "New Job Card");

            FlexLayout menuItemsLayout;
            for (int i = 0; i < IconsList.size(); i++) {
                menuItemsLayout = new FlexLayout(IconsList.get(i), new Span(labels.get(i)));
                menuItemsLayout.addClassNames("customers-action-button-container");
                menuBar.addItem(menuItemsLayout);
            }

            menuBar.getItems().forEach(item -> {
                item.addClickListener(event -> {

                });
            });
            return menuBar;
        });
    }


}//end of class...
