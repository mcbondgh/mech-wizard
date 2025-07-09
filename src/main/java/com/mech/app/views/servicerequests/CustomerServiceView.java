package com.mech.app.views.servicerequests;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.HeaderComponent;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.enums.SubRoles;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("My Services")
@Route(layout = MainLayout.class, value = "view/customer-services")
@Menu(icon = LineAwesomeIconUrl.TOOLBOX_SOLID)
@RolesAllowed("CUSTOMER")
public class CustomerServiceView extends VerticalLayout implements BeforeEnterObserver {

    private record serviceRequests(String serviceNo, String status, String desc, String date, String carInfo, String serviceTyp) {}

    private final Grid<serviceRequests> servicesGrid = new Grid<>();
    private final ComboBox<String> vehicleSelector = new ComboBox<>("Select Car");
    private final TextField plateNumberField = new TextField("Plate Number");
    private final TextArea problemDescriptionField = new TextArea("", "Please describe any specific issues or concerns here(Optional)");
    private final NumberField costField = new NumberField("Service Price");
    private final ComboBox<String> serviceSelector = new ComboBox<>("Select Your Service");
    private final Button addServiceButton = new Button("New Service", LineAwesomeIcon.PLUS_SOLID.create());
    private final Checkbox priorityCheckbox = new Checkbox("Urgent Attention");

    private static int SHOP_ID, USER_ID;
    private static AtomicReference<String> ACCESS_TYPE;
    public CustomerServiceView() {
        setHeightFull();
        setWidthFull();
        SHOP_ID = Integer.parseInt(SessionManager.getAttribute("shopId").toString());
        USER_ID = Integer.parseInt(SessionManager.getAttribute("userId").toString());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            var allowedRole = List.of(SubRoles.values()).toString().toLowerCase();
            ACCESS_TYPE = new AtomicReference<>(SessionManager.getAttribute("role").toString());
            System.out.println("Notification view: "+ allowedRole.toLowerCase().contains(ACCESS_TYPE.get().toLowerCase()));
            if (!allowedRole.contains(ACCESS_TYPE.get().toLowerCase())) {
                event.forwardTo("/login");
            }
        }catch (NullPointerException ex) {
            event.rerouteTo(LoginView.class);
        }
    }
    @Override
    public void onAttach(AttachEvent event) {
        serviceSelector.addClassNames("combo-box");
        vehicleSelector.addClassNames("combo-box");
        priorityCheckbox.addClassNames("default-check-box-style");
        costField.setPrefixComponent(new H4("Ghc "));
        costField.setReadOnly(true);
        costField.addClassNames("input-style");
        ComponentLoader.setServiceTypes(serviceSelector, SHOP_ID);
        ComponentLoader.setCarBrands(vehicleSelector);
        addServiceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(pageBody());
    }

    /*******************************************************************************************************************
    REFERENCE METHODS
     *******************************************************************************************************************/
    private List<serviceRequests> sampleData() {
        return List.of(
                new serviceRequests("001", "Active", "Oil change", "2023-01-01", "Toyota Camry", "Maintenance"),
                new serviceRequests("002", "Active", "Tire replacement", "2023-02-15", "Honda Accord", "Repair"),
                new serviceRequests("003", "Completed", "Brake inspection", "2023-03-10", "Ford Focus", "Inspection"),
                new serviceRequests("004", "Pending", "Battery replacement", "2023-04-05", "Chevrolet Malibu", "Repair"),
                new serviceRequests("005", "Active", "Engine tuning", "2023-05-20", "Nissan Altima", "Maintenance"),
                new serviceRequests("006", "Cancelled", "Transmission check", "2023-06-25", "BMW 3 Series", "Inspection")
        );
    }

    /*******************************************************************************************************************
     BODY SECTION
     *******************************************************************************************************************/
    private VerticalLayout pageBody() {
        FlexLayout buttonsContainer = new FlexLayout(addServiceButton);
        buttonsContainer.addClassNames("buttons-box", "customer-add-service-button-box");

        VerticalLayout layout = new VerticalLayout(buttonsContainer, gridConfiguration());
        layout.setSizeFull();
        layout.addClassNames("page-body-layout");

        addServiceButton.addSingleClickListener(e -> {
            bookServiceDialog();
        });

        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT RENDERERS
     *******************************************************************************************************************/
    private Component gridConfiguration() {
        servicesGrid.setWidthFull();
        servicesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        servicesGrid.addColumn(serviceRequests::serviceNo).setHeader("Service No#");
        servicesGrid.addColumn(serviceRequests::carInfo).setHeader("Car Details");
        servicesGrid.addColumn(serviceRequests::serviceTyp).setHeader("Service Type");
        servicesGrid.addColumn(serviceStatusRenderer()).setHeader("Status").setTextAlign(ColumnTextAlign.CENTER);
        servicesGrid.addColumn(viewDetailsButton()).setHeader("Action");
        servicesGrid.setItems(sampleData());
        servicesGrid.setDetailsVisibleOnClick(false);
        servicesGrid.setItemDetailsRenderer(serviceDetailsComponent());
        servicesGrid.addClassNames("alternative-grid-style");
        return servicesGrid;
    }
    private Renderer<serviceRequests> serviceStatusRenderer() {
        return new ComponentRenderer<>(data -> {
            Span span = new Span(data.status());
            if (data.status.equalsIgnoreCase("active")) {
                span.getElement().getThemeList().add("badge warning pill small");
            } else if (data.status.equalsIgnoreCase("completed")) {
                span.getElement().getThemeList().add("badge success pill small primary");
            } else if (data.status.equalsIgnoreCase("pending")) {
                span.getElement().getThemeList().add("badge tertiary small pill");
            } else span.getElement().getThemeList().add("badge error pill small");
            return span;
        });
    }
    private Renderer<serviceRequests> viewDetailsButton() {
        return new ComponentRenderer<>(dataProvider -> {
            Button button = new Button("View");
            button.addClassNames("customer-service-grid-button");
            button.setWidth("min-content");
            button.addSingleClickListener(e -> {
                var isVisible = servicesGrid.isDetailsVisible(dataProvider);
                if (isVisible) {
                    button.setText("Close X");
                    button.getStyle().setColor("#ff0000");
                } else {
                    button.setText("View");
                    button.getStyle().setColor("var(--lumo-primary-color)");
                }
                servicesGrid.setDetailsVisible(dataProvider, !isVisible);
            });
            return button;
        });
    }
    private Renderer<serviceRequests> serviceDetailsComponent() {
        return new ComponentRenderer<>(dataProvider -> {
            VerticalLayout layout = new VerticalLayout();
            var header = new H3("Service Details");
            layout.addClassNames("customer-service-request-details-box");

            var desc = new Div(dataProvider.desc);
            desc.addClassNames("customer-service-request-description-box");
            layout.add(header, desc);
            return layout;
        });
    }

    private void bookServiceDialog() {
        //primary component items...
        Button bookServiceButton = new Button("Submit Booking");
        bookServiceButton.addClassNames("default-btn-style", "customer-book-service-button");
        bookServiceButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        H3 primaryTitle = new H3("Service Request Information");

        VerticalLayout primaryLayout = new VerticalLayout(primaryTitle,
                serviceSelector, costField, vehicleSelector,
                problemDescriptionField, priorityCheckbox, bookServiceButton);
        primaryLayout.addClassNames("book-service-layout");
        primaryLayout.setSpacing(false);

        //secondary component items...
        Image icon = new Image(LineAwesomeIconUrl.CHECK_CIRCLE_SOLID, "CONFIRMED");
        icon.addClassName("book-service-confirm-icon");

        H3 title = new H3("Booking Confirmed!");
        title.addClassName("book-service-confirm-title");
        Paragraph subTitle = new Paragraph("Your booking reference is BK-4260");

        Section secondaryLayoutHeader = new Section(icon, title, subTitle);
        secondaryLayoutHeader.addClassNames("secondary-booking-header-section");

        Button bookAnotherButton = new Button("Book Another Service");
        bookAnotherButton.addClassNames("default-btn-style", "customer-book-another-service-button");

        VerticalLayout secondaryLayout = new VerticalLayout(secondaryLayoutHeader, bookAnotherButton);
        secondaryLayout.addClassNames("book-service-layout");

        Dialog dialog = new Dialog(primaryLayout);
        dialog.addClassNames("customer-service-booking-dialog");
        dialog.getHeader().add(new H2("Book A New Service"));
        dialog.setModal(true);
        dialog.open();

        bookServiceButton.addSingleClickListener(event -> {
            dialog.remove(primaryLayout);
            dialog.add(secondaryLayout);

        });

        bookAnotherButton.addSingleClickListener(event -> {
            dialog.remove(secondaryLayout);
            dialog.add(primaryLayout);
        });

    }




}//end of class...
