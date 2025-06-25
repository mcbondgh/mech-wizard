package com.mech.app.views.customers;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.HeaderComponent;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.configfiles.secutiry.DataEncryptor;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.users.UsersDataProvider;
import com.mech.app.enums.GenderEnums;
import com.mech.app.models.CustomerModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
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
import com.vaadin.flow.component.notification.Notification;
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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Add Customer")
@Route(value = "/view/add-customer", layout = MainLayout.class)
public class AddCustomerView extends VerticalLayout implements BeforeEnterObserver {

    private TextField nameField = new TextField("Full Name", "Kofi Mensah");
    private final TextField mobileNumberField = new TextField("Mobile Number", "020000000");
    private final TextField digitalAddressField = new TextField("Digital Address", "GH-0000-0000");
    private final EmailField emailField = new EmailField("Email Address", "kofimensah@exampl.com");
    private final TextField otherNumberField = new TextField("Other Number", "02700000000");
    private final ComboBox<GenderEnums> genderSelector = new ComboBox<>("Gender", GenderEnums.FEMALE, GenderEnums.MALE);
    private final ComboBox<String> brandSelector = new ComboBox<>("Car Brand");
    private final TextField modelField = new TextField("Car Model");
    private final TextField yearField = new TextField("Car Year");
    private final TextArea descriptionField = new TextArea("Description(Optional)", "Add additional information(optional)");
    private final TextField plateNumberField = new TextField("Plate Number", "GR-03434-25");
    private final Grid<CarDataProvider> carsGrid = new Grid<>();
    private final Button backButton = new Button("Close Form");
    private final Button saveButton = new Button("Register", LineAwesomeIcon.SAVE.create());

    private final CustomersDataProvider CUSTOMER_DATA_PROVIDER = new CustomersDataProvider();
    private final UsersDataProvider USER_DATA_PROVIDER = new UsersDataProvider();
    private final CarDataProvider CAR_DATA_PROVIDER = new CarDataProvider();
    private final CustomerModel DATA_MODEL = new CustomerModel();
    private static CustomDialog DIALOG_BOX;
    private static final AtomicInteger USER_ID = new AtomicInteger();
    private static final AtomicInteger SHOP_ID = new AtomicInteger();

    public AddCustomerView() {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        USER_ID.set(SessionManager.DEFAULT_USER_ID);
        SHOP_ID.set(SessionManager.DEFAULT_USER_ID);
    }

    @Override
    public void onAttach(AttachEvent listener) {
        genderSelector.addClassNames("combo-box-style");
        descriptionField.setAutocapitalize(Autocapitalize.CHARACTERS);
        descriptionField.setClearButtonVisible(true);
        descriptionField.addClassNames("item-text-area");

        brandSelector.addClassNames("combo-box-style");

        var pageHeader = new HeaderComponent().textHeader("Customer Registration", "Register a new customer with their car details here");
        add(pageHeader, pageBodySection());
        componentStyles();
    }


    /*******************************************************************************************************************
     REFERENCE METHODS IMPLEMENTATION
     *******************************************************************************************************************/
    private void componentStyles() {
        nameField.addClassNames("input-style");
        mobileNumberField.addClassNames("input-style");
        digitalAddressField.addClassNames("input-style");
        emailField.addClassNames("input-style");
        otherNumberField.addClassNames("input-style");
        plateNumberField.addClassNames("input-style");
        modelField.addClassNames("input-style");
        genderSelector.addClassNames("input-style");
        yearField.addClassNames("input-style");
        brandSelector.setClassName("input-style");

        mobileNumberField.setMaxLength(10); // Set max value for mobile number
        mobileNumberField.setAllowedCharPattern("[0-9]");
        otherNumberField.setMaxLength(10);
        otherNumberField.setAllowedCharPattern("[0-9]*");

        nameField.setRequired(true);
        mobileNumberField.setRequired(true);
        emailField.setRequired(true);
        genderSelector.setRequired(true);
        brandSelector.setRequired(true);
        modelField.setRequired(true);
        yearField.setRequired(true);

        nameField.setInvalid(nameField.isEmpty());
        mobileNumberField.setInvalid(mobileNumberField.isEmpty());
        emailField.setInvalid(emailField.isEmpty());
        genderSelector.setInvalid(genderSelector.isEmpty());
        brandSelector.setInvalid(brandSelector.isEmpty());
        modelField.setInvalid(modelField.isEmpty());
        yearField.setInvalid(yearField.isEmpty());
        yearField.setMaxLength(4);
        yearField.setAllowedCharPattern("[0-9]");

        emailField.setHelperText("Customer email shall be used as login username");
    }

    private void processFormData() {
        if (!checkForEmptyFields()) {
            var actionMsg = "add this customer to your list";
            DIALOG_BOX = new CustomDialog();
            DIALOG_BOX.showSaveDialog("REGISTER CUSTOMER", MessageLoaders.confirmationMessage(actionMsg))
                    .addConfirmListener(e ->
                            UI.getCurrent().access(() -> {
                                //SET VALUES FOR CUSTOMER DATA_DATA_PROVIDER
                                CUSTOMER_DATA_PROVIDER.setName(nameField.getValue());
                                CUSTOMER_DATA_PROVIDER.setEmail(emailField.getValue());
                                CUSTOMER_DATA_PROVIDER.setDigitalAddress(digitalAddressField.getValue());
                                CUSTOMER_DATA_PROVIDER.setMobileNumber(mobileNumberField.getValue());
                                CUSTOMER_DATA_PROVIDER.setOtherNumber(otherNumberField.getValue());
                                CUSTOMER_DATA_PROVIDER.setComments(descriptionField.getValue());
                                CUSTOMER_DATA_PROVIDER.setGender(genderSelector.getValue().name());
                                CUSTOMER_DATA_PROVIDER.setUserId(USER_ID.get());
                                CUSTOMER_DATA_PROVIDER.setShopId(SHOP_ID.get());
                                CUSTOMER_DATA_PROVIDER.setStatus(true);

                                AtomicInteger response = new AtomicInteger();
                                response.getAndAdd(DATA_MODEL.registerCustomer(CUSTOMER_DATA_PROVIDER));

                                //SET VALUES FOR USER_DATA_PROVIDER
                                USER_DATA_PROVIDER.setUsername(emailField.getValue());
                                USER_DATA_PROVIDER.setRole("Customer");
                                USER_DATA_PROVIDER.setPassword(DataEncryptor.defaultPassword());
                                USER_DATA_PROVIDER.setShop_id(SHOP_ID.get());
                                USER_DATA_PROVIDER.setStatus(true);

                                response.getAndAdd(DATA_MODEL.registerCustomerAsUser(USER_DATA_PROVIDER));

                                if (response.get() > 1) {
                                    AtomicBoolean isExecuted = new AtomicBoolean(false);

                                    var totalCars = carsGrid.getListDataView().getItemCount();

                                    String logMsg = "Customer registration process for " + nameField.getValue() + " was successful with " + totalCars + " registered car(s).";
                                    NotificationRecords notification = new NotificationRecords(
                                            "Customer Registration", logMsg, USER_ID.get(), SHOP_ID.get());

                                    //iterate through the car table and get table data
                                    carsGrid.getListDataView().getItems().forEach(car -> {
                                        CAR_DATA_PROVIDER.setBrand(car.getBrand());
                                        CAR_DATA_PROVIDER.setModel(car.getModel());
                                        CAR_DATA_PROVIDER.setCarYear(car.getCarYear());
                                        CAR_DATA_PROVIDER.setPlateNumber(car.getPlateNumber());
                                        isExecuted.set(DATA_MODEL.registerCustomerCar(CAR_DATA_PROVIDER) > 0);
                                    });

                                    //Check if cars we successfully saved. If true, then log and show notification to the user...
                                    if (isExecuted.get()) {
                                        DATA_MODEL.logNotification(notification);
                                        DIALOG_BOX.showSuccessNotification(MessageLoaders.successMessage());
                                        getUI().ifPresent(ex -> ex.refreshCurrentRoute(false));
                                    } else
                                        DIALOG_BOX.showErrorNotification(MessageLoaders.errorMessage("Unable to save customer customer details"));
                                }
                            }));
        }
    }//end of method...

    private boolean checkForEmptyFields() {
        return nameField.isInvalid() || mobileNumberField.isInvalid() || emailField.isInvalid()
                || genderSelector.isEmpty() || checkIfVehicleTableIsEmpty();
    }

    private boolean checkIfVehicleTableIsEmpty() {
        return carsGrid.getListDataView().getItems().findAny().isEmpty();
    }

    private void addCarDetailsToGrid() {
        boolean exists = carsGrid.getListDataView().getItems().anyMatch(car ->
                car.getBrand().equals(brandSelector.getValue()) &&
                        car.getModel().equals(modelField.getValue()) &&
                        car.getPlateNumber().equals(plateNumberField.getValue()) &&
                        Objects.equals(car.getCarYear(), yearField.getValue())
        );
        if (!exists) {
            carsGrid.getListDataView().addItem(
                    new CarDataProvider(brandSelector.getValue(),
                            modelField.getValue(), plateNumberField.getValue(), yearField.getValue())
            );
            modelField.clear();
            plateNumberField.clear();
            yearField.clear();
            brandSelector.setValue(null);
        }
    }

    /*******************************************************************************************************************
     PAGE CONTENT SECTION
     *******************************************************************************************************************/
    private VerticalLayout pageBodySection() {
        var buttonsContainer = new FlexLayout(saveButton, backButton);
        buttonsContainer.addClassNames("buttons-box");
        buttonsContainer.setWidthFull();
        buttonsContainer.setJustifyContentMode(JustifyContentMode.END);
        buttonsContainer.setAlignContent(FlexLayout.ContentAlignment.CENTER);

        VerticalLayout layout = new VerticalLayout(customerInfoSection(), carInfoSection(), buttonsContainer);
        layout.addClassNames("page-body-layout", "add-customer-page-body");

        //add a mousemove listener to the save button
        layout.getElement().addEventListener("mousemove", e -> {
            try {
                ComponentLoader.enableOrDisableComponent(saveButton, checkForEmptyFields());
            } catch (Exception ignore) {
            }
        });

        backButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        backButton.setClassName("default-error-button");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        saveButton.addClassNames("default-button-style", "save-button");
        backButton.addSingleClickListener(e -> UI.getCurrent().navigate(CustomersView.class));

        //action event when the save button is clicked to process and save customer to the database...
        saveButton.addSingleClickListener(e -> {
            processFormData();
        });

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
                emailField, digitalAddressField, descriptionField);
        verticalLayout.addClassNames("customer-input-area");

        layout.add(verticalLayout);
        return layout;
    }

    public Component carInfoSection() {
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
        ComponentLoader.setCarBrands(brandSelector);

        HorizontalLayout formLayout = new HorizontalLayout(brandSelector, modelField, yearField, plateNumberField, addButton);
        formLayout.addClassNames("car-info-box");

        formLayout.getElement().addEventListener("mouseover", (e) -> {
            try {
                addButton.setEnabled(
                        !(brandSelector.isEmpty() || modelField.isEmpty() || yearField.isEmpty() || plateNumberField.isEmpty())
                );

            } catch (Exception ignored) {
            }
        });

        layout.add(formLayout, configureCarGrid());

        //ACTION EVENT IMPLEMENTATION
        addButton.addSingleClickListener(event -> {
            addCarDetailsToGrid();
            event.getSource().setEnabled(false);
        });

        return layout;
    }

    private Component configureCarGrid() {
        carsGrid.addClassNames("alternative-grid-style");
        carsGrid.setWidthFull();
        carsGrid.getStyle().setHeight("250px");

//        carsGrid.addColumn(CarDataProvider::getCustomerId).setHeader("Customer Id");
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
