package com.mech.app.views.customers;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.HeaderComponent;
import com.mech.app.components.customer.CustomerComponents;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.enums.GenderEnums;
import com.mech.app.models.CustomerModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@PageTitle("Customers")
@Route(value = "/view/customers", layout = MainLayout.class)
@Menu(title = "Customers", order = 2, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
public class CustomersView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final CustomerModel DATA_MODEL = new CustomerModel();
    private static CustomDialog DIALOG_BOX;

    private final Button addCustomerButton = new Button("Add Customer", LineAwesomeIcon.PLUS_SOLID.create());
    private final VerticalLayout pageLoadIndicator = new VerticalLayout();
    private final Grid<CustomersDataProvider> customersRecordGrid = new Grid<>();
    private final TextField filterField = new TextField("", "Search by name, mobile number or plate number");


    public CustomersView() {
        getContent().setSizeUndefined();
        getContent().addClassNames("page-body");
        var Header = new HeaderComponent().pageHeaderWithComponent("Registered Customers",
                "easily manage and register all your customers from here", addCustomerButton);
        getContent().add(Header);
        getContent().add(Header, pageBody());

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

    private List<CustomersDataProvider> sampleData() {
        return DATA_MODEL.fetchAllCustomers();
    }

    /*******************************************************************************************************************
     COMPONENTS RENDERERS
     *******************************************************************************************************************/
    private Component configureCustomerGrid() {
        customersRecordGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COLUMN_BORDERS);
        customersRecordGrid.addClassNames("alternative-grid-style");
        customersRecordGrid.setEmptyStateComponent(ComponentLoader.emptyGridComponent("Customers record is empty"));

//      customersRecordGrid.setSelectionMode(Grid.SelectionMode.NONE);
        customersRecordGrid.setRowsDraggable(true);
        customersRecordGrid.setSizeUndefined();

        //set grid columns
        customersRecordGrid.addColumn(customerDetailsRenderer()).setHeader("CUSTOMER DETAILS").setSortable(true);
        customersRecordGrid.addColumn(customerStatusRenderer()).setHeader("STATUS").setTextAlign(ColumnTextAlign.CENTER);
        customersRecordGrid.addComponentColumn(this::contactComponent).setHeader("CONTACT");
        customersRecordGrid.addColumn(actionButtonsRenderer()).setHeader("ACTION").setTextAlign(ColumnTextAlign.CENTER);
        customersRecordGrid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.getSortOrder(SortDirection.DESCENDING);
        });
        customersRecordGrid.setColumnReorderingAllowed(true);

        customersRecordGrid.setItems(sampleData());
        customersRecordGrid.setItemDetailsRenderer(customerUpdateForm());
        customersRecordGrid.setDetailsVisibleOnClick(false);
        return customersRecordGrid;
    }

    private Renderer<CustomersDataProvider> customerDetailsRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.addClassNames("customers-details-renderer-box");

            Avatar avatar = new Avatar("CUS", LineAwesomeIconUrl.USER);
            avatar.addClassName("customer-avator");

            AtomicLong carsCount = new AtomicLong(0);
            carsCount.setRelease(
                    DATA_MODEL.fetchCustomerCarInformation().stream()
                            .filter(each -> each.getCustomerId() == dataProvider.getCustomerId()
                            ).count());

            Span carsCountText = new Span(carsCount + " Registered Car" + (carsCount.get() > 1 ? "s" : ""));
            carsCountText.addClassNames("vehicles-layout");

            var nameAndCarsBox = new Section(new H4(dataProvider.getName()), carsCountText);
            nameAndCarsBox.addClassNames("customers-details-grid-component", "customer-name-and-cars-box");
            nameAndCarsBox.setWidthFull();

            layout.add(avatar, nameAndCarsBox);
            return layout;
        });
    }

    private Component contactComponent(CustomersDataProvider dataProvider) {
        var itemOne = new FlexLayout(LineAwesomeIcon.SMS_SOLID.create(), new Span(dataProvider.getEmail()));
        var itemTwo = new FlexLayout(new Span(dataProvider.getMobileNumber() + (dataProvider.getOtherNumber().isEmpty() ? "" : ",")), new Span(dataProvider.getOtherNumber()));
        var itemThree = new FlexLayout(LineAwesomeIcon.PHONE_SOLID.create(), itemTwo);

        itemOne.getStyle().set("gap", "6px");
        itemTwo.getStyle().set("gap", "3px");
        itemThree.getStyle().set("gap", "6px");

        var contactBox = new Section(itemOne, itemThree);
        contactBox.addClassNames("customers-details-grid-component");

        return contactBox;
    }

    private Renderer<CustomersDataProvider> customerStatusRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
            Span span = new Span();
            if (dataProvider.isStatus()) {
                span.setText("Active");
                span.getElement().getThemeList().add("badge success primary pill small");
            } else {
                span.setText("Suspended");
                span.getElement().getThemeList().add("badge error primary pill small");
            }
            return span;
        });
    }

    private Renderer<CustomersDataProvider> actionButtonsRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
            MenuBar menuBar = new MenuBar();
            menuBar.addClassNames("default-menu-bar-style");
            menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_END_ALIGNED);
            menuBar.getStyle()
                    .setBackgroundColor("transparent")
                    .setTextDecoration("none")
                    .setWidth("auto")
                    .setPadding("0")
                    .setWhiteSpace(Style.WhiteSpace.INITIAL);

            var IconsList = List.of(LineAwesomeIcon.PENCIL_ALT_SOLID.create(),
                    LineAwesomeIcon.CAR_SOLID.create(), LineAwesomeIcon.ID_CARD.create(), LineAwesomeIcon.TRASH_SOLID.create());
            var labels = List.of("Update", "Delete", "Add Car", "New Job Card");

            var updateButton = menuBar.addItem(new FlexLayout(LineAwesomeIcon.PENCIL_ALT_SOLID.create(), new Span("Update")));
            var addCarButton = menuBar.addItem(new FlexLayout(LineAwesomeIcon.CAR_SOLID.create(), new Span("Add Car")));
            var accountButton = menuBar.addItem(new FlexLayout(LineAwesomeIcon.WALLET_SOLID.create(), new Span("Wallet")));
            var deleteButton = menuBar.addItem(new FlexLayout(LineAwesomeIcon.TRASH_SOLID.create(), new Span("Delete")));
            deleteButton.addClassName("item-delete-button");

            var buttonList = List.of(updateButton, addCarButton, accountButton, deleteButton);

            for (MenuItem flexLayout : buttonList) {
                flexLayout.addClassNames("customers-action-button-container");
            }

            //IMPLEMENTATION OF BUTTON ACTION EVENTS...
            CustomerComponents CUSTOMER_COMPONENTS = new CustomerComponents();
            deleteButton.addClickListener(e -> deleteCustomerAction(dataProvider));
            addCarButton.addSingleClickListener(e -> CUSTOMER_COMPONENTS.addCarAction(dataProvider.getCustomerId()));

            Map<String, String> paramValues = new HashMap<>();
            paramValues.put("customer_id", String.valueOf(dataProvider.getCustomerId()));
            paramValues.put("name", dataProvider.getName());
            paramValues.put("mobile_number", dataProvider.getMobileNumber());

            QueryParameters params = QueryParameters.simple(paramValues);
            accountButton.addSingleClickListener(e -> getUI().ifPresent(ui -> ui.navigate(CustomerAccountView.class.getAnnotation(Route.class).value(), params)));

            updateButton.addSingleClickListener(e -> {
                customersRecordGrid.setDetailsVisible(dataProvider, !customersRecordGrid.isDetailsVisible(dataProvider));
            });
            return menuBar;
        });
    }

    private Renderer<CustomersDataProvider> customerUpdateForm() {
        return new ComponentRenderer<>(data -> {
            H4 title = new H4("Personal Information");
            title.addClassName("customer-update-header-title");

            FormLayout formLayout = getFormLayout(data);

            var paragraph = new Paragraph("By clicking the 'Update' button, this customer's password would be updated" +
                    "and a notification alert sent to the customer about this action. Customer's new password will be: ");
            paragraph.addClassName("customer-password-body-content");
            paragraph.setWidthFull();

            var resetButton = new Button("Reset Password", VaadinIcon.REFRESH.create(), e -> {
            });
            resetButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

            var sectionTwo = new HorizontalLayout(paragraph, resetButton);
            sectionTwo.addClassName("customer-password-section");
            sectionTwo.setWidthFull();
            sectionTwo.setSpacing(true);

            VerticalLayout parentLayout = new VerticalLayout(title, formLayout, sectionTwo);
            parentLayout.addClassName("customer-update-form-parent-layout");
            return parentLayout;
        });
    }

    private static FormLayout getFormLayout(CustomersDataProvider data) {
        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("customer-update-form-layout");

        // Create form components here
        var cus_nameField = new TextField("Customer Name", data.getName(), "customer name...");
        var cus_numberField = new TextField("Mobile Number", data.getMobileNumber(), "0230000....");
        var cus_otherNumber = new TextField("Other Number", data.getOtherNumber(), "023000....");
        var cus_genderSelector = new ComboBox<>("Gender", GenderEnums.MALE.name(), GenderEnums.FEMALE.name());
        cus_genderSelector.setValue(data.getGender());

        var cus_emailField = new EmailField("Email", data.getEmail());
        cus_emailField.setValue(data.getEmail());
        var cus_addressField = new TextField("Digital Address", data.getDigitalAddress(), "KE-0001...");
        var cus_notesField = new TextArea("Description(Optional)", data.getComments(), "Optional Description");

        var updateButton = new Button("Update Data", LineAwesomeIcon.PENCIL_ALT_SOLID.create(), e -> {
            // Handle update action here
            Notification.show("Customer data updated successfully!", 3000, Notification.Position.BOTTOM_START);
        });
        updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        updateButton.addClassNames("default-button-style", "customer-update-button");

        var buttonContainer = new Div(updateButton);
        buttonContainer.setWidthFull();
        buttonContainer.addClassName("customer-update-button-container");
        buttonContainer.getStyle().setPadding("10px")
                .setBoxSizing(Style.BoxSizing.BORDER_BOX)
                .setBorder("1px solid var(--lumo-contrast-10pct)");

        //set form data content...
        formLayout.add(cus_nameField, cus_numberField, cus_otherNumber, cus_genderSelector, cus_emailField, cus_addressField);
        formLayout.add(cus_notesField, 2);
        formLayout.add(buttonContainer, 2);

        return formLayout;
    }

    /*******************************************************************************************************************
     MENU BAR ACTION EVENT BUTTONS IMPLEMENTATION
     *******************************************************************************************************************/
    private void deleteCustomerAction(CustomersDataProvider dataProvider) {
        DIALOG_BOX = new CustomDialog();
        var dialogAction = DIALOG_BOX.showDeleteDialog("DELETE CUSTOMER", MessageLoaders.confirmationMessage(" permanently remove this customer"));
        dialogAction.addConfirmListener(action -> {
            String logMessage = "Customer with id " + dataProvider.getCustomerId() + " and name " + dataProvider + " has been deleted from your list of customer";
            DIALOG_BOX.showSuccessNotification("Customer successfully deleted...");
        });
    }


}//end of class...
