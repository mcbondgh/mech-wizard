package com.mech.app.views.reports;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.HeaderComponent;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.servicesrequest.ServicesDataProvider;
import com.mech.app.enums.SubRoles;
import com.mech.app.models.ServiceRequestModel;
import com.mech.app.views.MainLayout;
import com.mech.app.views.dashboard.CustomerDashboardView;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.vaadin.flow.component.Tag.H4;

@Route(value = "my-report")
@PageTitle("My Reports")
@RolesAllowed("CUSTOMER")
public class CustomerReportsView extends VerticalLayout implements BeforeEnterObserver {
    private int USER_ID;
    private int SHOP_ID, CUSTOMER_ID;
    private DAO SERVICE_REQUEST_MODEL;
    private static AtomicReference<String> ACCESS_TYPE;

    public CustomerReportsView() {
        setPadding(true);
        setSpacing(true);
        setSizeFull();
        addClassName("page-body");
        SERVICE_REQUEST_MODEL = new ServiceRequestModel();
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
        Icon icon = VaadinIcon.INVOICE.create();
        H2 viewTitle = new H2("All Completed Services");
        FlexLayout divOne = new FlexLayout(icon, new Span(), viewTitle);

        var navButton = new Button("Dashboard", VaadinIcon.DASHBOARD.create(), e -> {
            UI.getCurrent().navigate(CustomerDashboardView.class);
        });
        navButton.addClassNames("default-button-style");

        var headerComponent = new HeaderComponent().dashboardHeader(divOne, navButton);
        add(headerComponent, bodyContent());

    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/
    private List<ServicesDataProvider.PaidServicesRecord> sampleData() {
        // Sample data for the grid
        return SERVICE_REQUEST_MODEL.fetchPaidServicesByCustomerId(CUSTOMER_ID);

    }


    /*******************************************************************************************************************
     VIEW COMPONENTS
     *******************************************************************************************************************/
    private Component bodyContent() {
        var headerText = new H3("Comprehensive Service History");
        headerText.getStyle().setColor("var(--header-text-color)")
                .setWidth("100%");

        VerticalLayout headerLayout = new VerticalLayout(headerText, gridSetup());
        headerLayout.setSizeFull();
        headerLayout.addClassName("customer-report-view-parent-layout");
        return headerLayout;
    }

    private Component gridSetup() {
        final Grid<ServicesDataProvider.PaidServicesRecord> grid = new Grid<>();
        grid.setSizeFull();
        grid.getColumns().forEach(i -> {
            i.setAutoWidth(true);
            i.setSortable(true);
            i.setEditorComponent(new Button("edit"));
        });
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.addColumn(ServicesDataProvider.PaidServicesRecord::jobNo).setHeader("Job No");
        grid.addColumn(serviceDescComponent()).setHeader("Service");
        grid.addColumn(vehicleComponent()).setHeader("Vehicle");
        grid.addColumn(new LocalDateRenderer<>(i -> i.serviceDate().toLocalDateTime().toLocalDate())).setHeader("Date");
        grid.addColumn(new LocalDateRenderer<>(i -> i.dateCompleted().toLocalDateTime().toLocalDate())).setHeader("Completed");
        grid.addComponentColumn(e -> starsComponent(e.rate())).setHeader("Stars");
        grid.addComponentColumn(e -> billComponent(e.totalBill())).setHeader("Total Bill(Ghc)");
        grid.setItemDetailsRenderer(gridDetails());
        grid.setColumnReorderingAllowed(true);
        grid.setItems(sampleData());
        return grid;
    }

    private Component starsComponent(String stars) {
        H4 component = new H4(stars);
        component.getElement().getThemeList().add("badge warning pill small");
        return component;
    }

    private Component billComponent(double amount) {
        H4 component = new H4(String.valueOf(amount));
        component.getElement().getThemeList().add("badge success primary pill small");
        return component;
    }

    private Renderer<ServicesDataProvider.PaidServicesRecord> serviceDescComponent() {
        return new ComponentRenderer<>(data -> {
            var service = new H5(data.serviceType());
            var span = new Span(data.description());
            Div div = new Div(service, span);
            div.getStyle().setWidth("100%");
            return div;
        });
    }

    private Renderer<ServicesDataProvider.PaidServicesRecord> vehicleComponent() {
        return new ComponentRenderer<>(data -> {
            var service = new H5(data.vehicle());
            var span = new Span(data.VIN());
            Div div = new Div(service, span);
            div.getStyle().setWidth("fit-content");
            return div;
        });
    }

    private Renderer<ServicesDataProvider.PaidServicesRecord> gridDetails() {
        return new ComponentRenderer<>(data -> {
            VerticalLayout layout = new VerticalLayout();

            //check if customer has provided a rating or not
            var feedComponent = jobFeedBackComponent(data.jobId(), data.rate(), data.feedback());
            layout.add(feedComponent);

            return layout;
        });
    }

    private Component jobFeedBackComponent(int jobId, String stars, String feedback) {
        var h3 = new H3(stars);
        h3.addClassName("rating-label");
        var indicator = new Paragraph();
        indicator.setWidthFull();
        indicator.getStyle().setTextAlign(Style.TextAlign.CENTER);
        Div div = new Div();
        div.setWidthFull();
        div.addClassNames("feedback-component");
        Select<Integer> starsSelect = new Select<>("Rate Job", e -> {
            var rateValue = String.valueOf(e.getValue());
                switch (rateValue) {
                    case "1" -> {
                        h3.setText("⭐");
                        indicator.setText("Poor");
                    }
                    case "2" ->{
                        h3.setText("⭐⭐");
                        indicator.setText("Average");
                    }
                    case "3" -> {
                        h3.setText("⭐⭐⭐");
                        indicator.setText("Good");
                    }
                    case "4" -> {
                        h3.setText("⭐⭐⭐⭐");
                        indicator.setText("Very Good");
                    }
                    case "5" -> {
                        h3.setText("⭐⭐⭐⭐⭐");
                        indicator.setText("Excellent");
                    }
                }
        }, 1, 2, 3, 4, 5);

        TextArea feedbackArea = new TextArea("Leave a feedback", "Leave a feedback to this work.");
        feedbackArea.setRequired(true);

        Button rateButton = new Button("Submit Feedback", LineAwesomeIcon.TELEGRAM.create(), e -> {
            var dialog = new CustomDialog().showSaveDialog("SERVICE RATING", "Please confirm to rate this service else cancel");
            dialog.addConfirmListener(ee -> {
                int rateValue = starsSelect.getValue();
                String name = SessionManager.getAttribute("fullName").toString();
               String logMsg = String.format("Customer %s submitted a %d-star rating for job #%d with satisfaction level: %s", name, rateValue, jobId, indicator.getText());
               int response = new ServiceRequestModel().rateService(CUSTOMER_ID, jobId, rateValue, feedbackArea.getValue());
               if (response > 0) {
                   var logger = new NotificationRecords("SERVICE RATING", logMsg, USER_ID, SHOP_ID);
                   SERVICE_REQUEST_MODEL.logNotification(logger);
                   UI.getCurrent().refreshCurrentRoute(false);
               }
            });
        });
        rateButton.addClassNames("default-button-style");
        rateButton.setWidthFull();
        if (!stars.equals("no rating")) {
            feedbackArea.setValue(feedback);
            rateButton.setVisible(false);
            div.setEnabled(false);
        }
        div.add(h3, indicator, new Hr(), starsSelect, feedbackArea, rateButton);
        return div;
    }

}//END OF CLASS
