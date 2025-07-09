package com.mech.app.views.jobcards;

import com.mech.app.components.HeaderComponent;
import com.mech.app.components.transactions.TransactionDialogs;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.jobcards.JobCardDataProvider;
import com.mech.app.dataproviders.servicesrequest.ServicesDataProvider;
import com.mech.app.enums.MasterRoles;
import com.mech.app.models.ServiceRequestModel;
import com.mech.app.views.MainLayout;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Completed Jobs")
@Route(value = "/view/completed-job", layout = MainLayout.class)
@RolesAllowed({"MECHANIC", "ADMIN", "RECEPTIONIST"})
//@Menu(title = "Completed Jobs", order = 4, icon = LineAwesomeIconUrl.CHECK_CIRCLE_SOLID)
public class CompletedJobView extends Composite<VerticalLayout> implements BeforeEnterObserver, AfterNavigationObserver {

    private final Grid<ServicesDataProvider.CompletedServicesRecord> grid = new Grid<>();
    private static final ServiceRequestModel SERVICE_REQUEST_MODEL = new ServiceRequestModel();
    private static AtomicReference<String> ACCESS_TYPE;

    public CompletedJobView() {
        getContent().setBoxSizing(BoxSizing.BORDER_BOX);
        getContent().setWidthFull();
        getContent().setHeightFull();

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            var allowedRole = List.of(MasterRoles.values()).toString().toLowerCase();
            ACCESS_TYPE = new AtomicReference<>(SessionManager.getAttribute("role").toString());
            if (!allowedRole.contains(ACCESS_TYPE.get().toLowerCase())) {
                event.forwardTo("/login");
            }
        }catch (NullPointerException ex) {
            event.rerouteTo(LoginView.class);
        }
    }

    @Override
    public void onAttach(AttachEvent event) {
        getContent().add(pageHeader(), pageBody());
//        loader.setVisible(false);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        // This method is called after the navigation is complete
        // You can perform any additional actions here if needed
        // For example, you can update the UI or fetch data based on the navigation
        // event.getLocation().getPath() to get the current path
//        UI.getCurrent().access(() -> {
//            Notification.show("Navigation completed to: " + afterNavigationEvent.getLocation().getPath());
//            loader.setVisible(false);
//        });
    }

    /*******************************************************************************************************************
     REFERENCE METHODS IMPLEMENTATION
     *******************************************************************************************************************/
    private List<ServicesDataProvider.CompletedServicesRecord> sampleData() {
        // Sample data for the grid
        return SERVICE_REQUEST_MODEL.fetchCompletedJobs();

    }

    private Component cardComponent(Component header, Component content) {
        var layout = new VerticalLayout();
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        layout.setWidthFull();
        layout.addClassNames("details-card-layout");
        header.addClassNames("detail-card-header");
        content.addClassNames("detail-card-content");
        layout.add(header, content);
        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT RENDERERS
     *******************************************************************************************************************/
    private Component serviceRecordsRenderer() {
        var heroText = new H5(VaadinIcon.RECORDS.create(), new Span(" Service Records"));
        heroText.addClassNames("completed-service-component-header");
        VerticalLayout layout = new VerticalLayout(heroText, gridConfiguration());
        layout.setWidthFull();
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        layout.addClassNames("completed-job-record-layout");
        return layout;
    }

    private Component serviceDetailsRenderer() {
        var heroText = new H5(LineAwesomeIcon.RECEIPT_SOLID.create(), new Span(" Service Details"));
        heroText.addClassNames("completed-service-component-header");
        VerticalLayout layout = new VerticalLayout(heroText);
        layout.setWidthFull();
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        layout.addClassNames("completed-job-record-layout");

        Button feedbackButton = new Button("Leave Feedback", LineAwesomeIcon.STAR.create());
        feedbackButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        feedbackButton.addClassNames("details-feedback-button");

        Div emptyBox = new Div(LineAwesomeIcon.CLOCK.create(), new Paragraph("Please select a job to display content"));
        emptyBox.addClassNames("details-empty-div");
        emptyBox.getStyle().set("box-sizing", "border-box");

        Section dataSection = new Section();
        dataSection.setWidthFull();
        dataSection.addClassNames("details-data-section");
        dataSection.getStyle().set("box-sizing", "border-box");

        //client details
        var nameLabel = new Span("Client Name");
        var nameValue = new H5();
        Section customerSection = new Section(nameLabel, nameValue);
        customerSection.addClassName("completed-jobs-details-section");

        //vehicle info
        var vehicleLabel = new Span("Vehicle Information");
        var vehicleValues = new H5();
        var vehicleSection = new Section(vehicleLabel, vehicleValues);
        vehicleSection.addClassName("completed-jobs-details-section");

        //service details
        var serviceLabel = new Span("Service Type");
        var serviceTypeValue = new H5();
        var serviceCostLabel = new Span("Service Cost");
        var serviceCost = new H5();
        var serviceSection = new Div(serviceLabel, serviceTypeValue, new Hr(), serviceCostLabel, serviceCost);
        serviceSection.addClassNames("completed-jobs-details-section");

        var finishedDateLabel = new Span("Date Completed");
        var finishedDateValue = new H5();
        finishedDateValue.addComponentAsFirst(LineAwesomeIcon.CALENDAR_CHECK.create());
        var dateContainer = new Div(finishedDateLabel, finishedDateValue);
        dateContainer.setWidthFull();
        dateContainer.addClassNames("completed-jobs-details-section");

        // items details
        var itemsLabel = new Span("Items used");
        var itemsValue = new H5();
        var itemsAmountLabel = new Span("Total Item Amount");
        var itemAmountValue = new H5();
        Div itemsContainer = new Div(itemsLabel, itemsValue, new Hr(), itemsAmountLabel, itemAmountValue);
        itemsContainer.addClassNames("completed-jobs-details-section");

        H3 totalBillValue = new H3();
        totalBillValue.addClassName("total-bill-value");
        totalBillValue.addComponentAsFirst(new H3("Ghc"));
        HorizontalLayout billContainer = new HorizontalLayout(new H6("Service + Items Bill:"), totalBillValue);
        billContainer.setWidthFull();
        billContainer.addClassNames("total-bill-container");

        dataSection.add(dateContainer, customerSection, vehicleSection, serviceSection, itemsContainer, billContainer);

        layout.add(emptyBox);
        AtomicInteger jobId = new AtomicInteger();
        AtomicReference<String> jobNo = new AtomicReference<>();
        grid.addSelectionListener(selected -> {
            layout.remove(dataSection, emptyBox);
            UI.getCurrent().access(() -> {
                selected.getFirstSelectedItem().ifPresentOrElse(
                        item -> {
                            layout.add(dataSection);
                            jobId.set(item.jobId());
                            jobNo.set(item.jobNo());

                            //set customer value
                            nameValue.setText(item.clientName());
                            vehicleValues.setText(item.vehicle() + ", " + item.VIN());
                            serviceTypeValue.setText(item.serviceType());
                            serviceCost.setText("Ghc"+ item.serviceCost());
                            itemsValue.setText(String.valueOf(item.itemsCount()));
                            itemAmountValue.setText("Ghc" + item.itemsTotalCost());
                            finishedDateValue.setText(item.dateCompletedString());

                            double computeBill = item.itemsTotalCost() + item.serviceCost();
                            totalBillValue.setText("Ghc" + computeBill);

                        }, () -> layout.replace(dataSection, emptyBox)
                );
            });
        });
        //show feedback dialog
        feedbackButton.addClickListener(e -> new TransactionDialogs().feedbackDialog(jobNo.get(), jobId.get()));
        return layout;
    }

    private Component gridConfiguration() {
        grid.setWidthFull();
        grid.setHeightFull();
        grid.addClassNames("alternative-grid-style");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(ServicesDataProvider.CompletedServicesRecord::jobNo).setHeader("Job No").setFlexGrow(0).setWidth("100px");
        grid.addColumn(ServicesDataProvider.CompletedServicesRecord::serviceType).setHeader("Service Type");
        grid.addColumn(new LocalDateRenderer<>(date -> date.serviceDate().toLocalDateTime().toLocalDate())).setHeader("Date");
        grid.addColumn(rateRenderer()).setHeader("Rate");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setItems(sampleData());
        return grid;
    }

    private Renderer<ServicesDataProvider.CompletedServicesRecord> rateRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
//            var responseBody = SERVICE_REQUEST_MODEL.fetchAllFeedbacksByJobId(dataProvider.jobId());
            Div layout = new Div(dataProvider.getStars());
            layout.addClassNames("completed-service-details-renderer-box");
            if (dataProvider.getStars().equals("No rating")) {
                layout.getElement().getThemeList().add("badge warning small pill");
            }
            return layout;
        });
    }


    /*******************************************************************************************************************
     PAGE BODY SECTION
     *******************************************************************************************************************/
    private Component pageHeader() {
        var layout = new HeaderComponent().textHeader("Completed Jobs",
                "View all completed jobs and their history with rating");
        layout.setWidthFull();
        layout.setBoxSizing(com.vaadin.flow.component.orderedlayout.BoxSizing.BORDER_BOX);
        layout.addClassNames("header-layout");
        return layout;
    }

    private Component pageBody() {
        HorizontalLayout layout = new HorizontalLayout(serviceRecordsRenderer(), serviceDetailsRenderer());

        layout.setBoxSizing(com.vaadin.flow.component.orderedlayout.BoxSizing.BORDER_BOX);
        layout.addClassNames("completed-job-view-layout");
        return layout;
    }

}//end of class...
