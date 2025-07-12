package com.mech.app.views.customers;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.HeaderComponent;
import com.mech.app.components.customer.CustomerComponents;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.enums.MasterRoles;
import com.mech.app.enums.SubRoles;
import com.mech.app.models.CustomerModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
import com.mech.app.views.dashboard.CustomerDashboardView;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Manage Cars")
@Route(value = "/view/manage-cars")
public class AddCarsView extends VerticalLayout implements BeforeEnterObserver {

    private final CustomerModel DATA_MODEL = new CustomerModel();
    AtomicReference<String> ACCESS_TYPE;
    int USER_ID, SHOP_ID, CUSTOMER_ID;
    public AddCarsView() {
        setClassName("page-cover");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            var allowedRole = List.of(SubRoles.values()).toString().toLowerCase();
            USER_ID = Integer.parseInt(SessionManager.getAttribute("userId").toString());
            CUSTOMER_ID = Integer.parseInt(SessionManager.getAttribute("clientId").toString());
            SHOP_ID = Integer.parseInt(SessionManager.getAttribute("shopId").toString());
            ACCESS_TYPE = new AtomicReference<>(SessionManager.getAttribute("role").toString());
            if (!allowedRole.contains(ACCESS_TYPE.get().toLowerCase())) {
                event.forwardTo("/login");
            }
        } catch (NullPointerException ex) {
            event.rerouteTo(LoginView.class);
        }
    }

    @Override
    public void onAttach(AttachEvent event) {
        Icon icon = VaadinIcon.CAR.create();
        H2 viewTitle = new H2(" Your Fleet Of Vehicles");
        FlexLayout divOne = new FlexLayout(icon, new Span(),viewTitle);

        var navButton = new Button("Dashboard", VaadinIcon.DASHBOARD.create(), e -> {
            UI.getCurrent().navigate(CustomerDashboardView.class);
        });
        navButton.addClassNames("default-button-style");

        var headerComponent = new HeaderComponent().dashboardHeader(divOne, navButton);
        add(headerComponent, addCarSection());
    }

    private VerticalLayout addCarSection() {

        ComboBox<String> carComboBox = new ComboBox<>("Pick Vehicle Make");
        TextField modelField = new TextField("Vehicle Model");
        TextField yearField = new TextField("Year");
        TextField plateField = new TextField("Plate Number", "AG0-00-2025");
        var addButton = new Button("Add Vehicle", LineAwesomeIcon.PLUS_SOLID.create(), e-> {
            var checkFields = carComboBox.isEmpty() || modelField.isEmpty() || yearField.isEmpty() || plateField.isEmpty();

            if (!checkFields) {
                processFormData(carComboBox.getValue(), modelField.getValue(), yearField.getValue(), plateField.getValue());
            }
        });
        addButton.setWidthFull();
        addButton.addClassNames("default-button-style");

        carComboBox.setRequired(true);
        modelField.setRequired(true);
        yearField.setRequired(true);
        plateField.setRequired(true);

        yearField.setMaxLength(4);
        yearField.setPattern("\\d{4}");
        yearField.setHelperText("Enter a valid year (e.g., 2023)");

        ComponentLoader.setCarBrands(carComboBox);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("add-car-form-layout");
        var colOne = new FormLayout.ResponsiveStep("0", 1);
        var colThree = new FormLayout.ResponsiveStep("768px", 3);
        formLayout.setResponsiveSteps(colOne, colThree);

        formLayout.add(carComboBox, 3);
        formLayout.add(modelField, yearField, plateField);
        formLayout.add(addButton, 3);

        var headerLabel = new H3("Add Or Update Vehicle");
        VerticalLayout parentLayout = new VerticalLayout(headerLabel, new Hr(),formLayout);
        parentLayout.setWidthFull();
        parentLayout.addClassName("add-car-view-layout");

        var grid = new CustomerComponents().carsGrid(CUSTOMER_ID);
        grid.setItemDetailsRenderer(carActionsRenderer());
        parentLayout.add(grid);

        return parentLayout;
    }


    private Renderer<CarDataProvider>carActionsRenderer() {
        return new ComponentRenderer<>(data -> {
           VerticalLayout layout = new VerticalLayout(new H3("Update " + data.getBrand()));

           return layout;
        });
    }

    private void processFormData(String make, String model, String year, String plateNo) {
        CustomDialog dialog = new CustomDialog();
        //create car data provider
        var vehiclesDataObj = new CarDataProvider();
        var confirmDialog = dialog.showSaveDialog("SAVE NEW CAR", MessageLoaders.confirmationMessage("add this car to your fleet"));
        confirmDialog.addConfirmListener(event -> {
            vehiclesDataObj.setCustomerId(CUSTOMER_ID);
            vehiclesDataObj.setBrand(make);
            vehiclesDataObj.setModel(model);
            vehiclesDataObj.setPlateNumber(plateNo);
            vehiclesDataObj.setCarYear(year);

            int status = DATA_MODEL.registerCustomerCarByCustomerId(vehiclesDataObj);
            if (status > 0) {
                String logMsg = "Customer No: " + CUSTOMER_ID + " added " + make + " with plate number " + plateNo;
                var logger = new NotificationRecords("SERVICE RATING", logMsg, USER_ID, SHOP_ID);
                DATA_MODEL.logNotification(logger);
                UI.getCurrent().refreshCurrentRoute(false);
            } else dialog.showErrorNotification(MessageLoaders.errorMessage("Failed to add vehicle to your fleets. Refresh the page and try again"));
        });
    }

}//END OF CLASS...
