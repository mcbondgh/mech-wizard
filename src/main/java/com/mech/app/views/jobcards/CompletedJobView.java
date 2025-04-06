package com.mech.app.views.jobcards;

import com.mech.app.components.HeaderComponent;
import com.mech.app.components.transactions.TransactionDialogs;
import com.mech.app.dataproviders.servicesrequest.ServiceRequestDataProvider;
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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;
import java.util.Objects;

@PageTitle("Completed Jobs")
@Route(value = "/view/completed-job")
@Menu(title = "Completed Jobs", order = 4, icon = LineAwesomeIconUrl.CHECK_CIRCLE_SOLID)
public class CompletedJobView extends Composite<VerticalLayout> implements BeforeEnterObserver, AfterNavigationObserver {

    private record serviceRecords() {
    }

    private Div loader = new Div(new Text("Page Loading...."));
    private final Grid<ServiceRequestDataProvider.completedServicesRecord> grid = new Grid<>();

    public CompletedJobView() {
        getContent().setBoxSizing(BoxSizing.BORDER_BOX);
        getContent().setWidthFull();
        getContent().setHeightFull();
        getContent().add(loader, pageHeader(), pageBody());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        loader.setVisible(true);
    }

    @Override
    public void onAttach(AttachEvent event) {
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
    private List<ServiceRequestDataProvider.completedServicesRecord> sampleData() {
        // Sample data for the grid
        var parts = List.of("Oil Filter", "Air Filter", "Brake Pads", "Spark Plugs", "Battery");
        var parts2 = List.of("Oil Filter", "Brake Pads", "Battery");
        var notes = List.of("Replaced the oil filter and topped up engine oil.",
                "Cleaned and replaced the air filter.",
                "Inspected and replaced worn-out brake pads.",
                "Checked and replaced faulty spark plugs.",
                "Tested and replaced the car battery.");

        var record1 = new ServiceRequestDataProvider.completedServicesRecord(
                "J123", "Oil Change", "2023-10-01", "Star 5", "John Doe",
                "ABC123", "Engine oil change and filter replacement.", parts, notes, "Ghc150.00");
        var record2 = new ServiceRequestDataProvider.completedServicesRecord(
                "J124", "Brake Inspection", "2023-10-02", "No rating", "Jane Smith",
                "XYZ456", "Brake inspection and pad replacement.", parts2, notes, "Ghc200.00");
        var record3 = new ServiceRequestDataProvider.completedServicesRecord(
                "J125", "Tire Rotation", "2023-10-03", "Star 4", "Alice Johnson",
                "LMN789", "Tire rotation and alignment.", parts, notes, "Ghc100.00");
        var record4 = new ServiceRequestDataProvider.completedServicesRecord(
                "J126", "Battery Replacement", "2023-10-04", "No rating", "Bob Brown",
                "DEF012", "Battery replacement and electrical system check.", parts, notes, "Ghc250.00");
        var record5 = new ServiceRequestDataProvider.completedServicesRecord(
                "J127", "Fluid Check", "2023-10-05", "Star 2", "Charlie Green",
                "GHI345", "Checked and topped up all fluids.", parts2, notes, "Ghc80.00");
        return List.of(record1, record2, record3, record4, record5);
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

        Div emptyBox = new Div(LineAwesomeIcon.CLOCK.create(), new Paragraph("Please select a service to display content"));
        emptyBox.addClassNames("details-empty-div");
        emptyBox.getStyle().set("box-sizing", "border-box");

        layout.add(emptyBox);

        Section dataSection = new Section();
        dataSection.addClassNames("details-data-section");
        dataSection.getStyle().set("box-sizing", "border-box");

        grid.addSelectionListener(selected -> {
            dataSection.removeAll();
            layout.remove(dataSection);
            selected.getFirstSelectedItem().ifPresentOrElse(
                    item -> {
                        UnorderedList partsList = new UnorderedList();
                        Section partsSection = new Section(new H5("Purchased Parts"), partsList);
                        partsSection.addClassNames("list-and-header-section");

                        UnorderedList technicianList = new UnorderedList();
                        Section technicianSection = new Section(new H5("Technician Notes"), technicianList);
                        technicianSection.addClassNames("list-and-header-section");

                        partsList.removeAll();
                        technicianList.removeAll();

                        var jobAndCarContainer = new Section(LineAwesomeIcon.CAR_SOLID.create(), cardComponent(new Paragraph(item.jobNo()), new Span(item.plateNumber())));
                        jobAndCarContainer.addClassNames("details-container-filter");
                        var serviceDate = cardComponent(new Span("Service Date"), new H4(item.date()));
                        serviceDate.addClassNames("details-container-filter");
                        var moneyColumn = cardComponent(new Paragraph("Total Bill"), new Paragraph(item.amount()));
                        moneyColumn.addClassNames("details-container-filter-amount");
                        var serviceTypeColumn = cardComponent(new Span("Service Type"), new H4(item.serviceType()));
                        serviceTypeColumn.addClassNames("details-container-filter");
                        var technicianColumn = cardComponent(new Span("Technician"), new H4(item.assignedTechnician()));
                        technicianColumn.addClassNames("details-container-filter");
                        var desColumn = cardComponent(new Span("Problem Description"), new H6(item.problemDescription()));
                        desColumn.addClassNames("details-container-filter");
                        item.partsUsed().forEach(e -> partsList.add(e + "\n"));
                        item.technicianNotes().forEach(e -> technicianList.add(e + "\n"));

                        dataSection.add(jobAndCarContainer, serviceDate, moneyColumn, serviceTypeColumn,
                                technicianColumn, desColumn, partsSection, technicianSection, feedbackButton);

                        layout.replace(emptyBox, dataSection);

                        var hasRating = Objects.equals(item.rateStatus(), "No rating");
                        feedbackButton.setVisible(hasRating);

                        //show feedback dialog
                        feedbackButton.addClickListener(e -> new TransactionDialogs().feedbackDialog(item.jobNo()));
                    }, () -> layout.replace(dataSection, emptyBox)

            );
        });
        return layout;
    }

    private Component gridConfiguration() {
        grid.setWidthFull();
        grid.setHeightFull();
        grid.addClassNames("alternative-grid-style");
        grid.setItems(sampleData());
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(ServiceRequestDataProvider.completedServicesRecord::jobNo).setHeader("Job No").setFlexGrow(0).setWidth("100px");
        grid.addColumn(ServiceRequestDataProvider.completedServicesRecord::serviceType).setHeader("Service Type");
        grid.addColumn(ServiceRequestDataProvider.completedServicesRecord::date).setHeader("Date");
        grid.addColumn(rateRenderer()).setHeader("Rate");
        grid.setItems(sampleData());
        return grid;
    }

    private Renderer<ServiceRequestDataProvider.completedServicesRecord> rateRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
            Div layout = new Div(dataProvider.rateStatus());
            layout.addClassNames("completed-service-details-renderer-box");
            if (dataProvider.rateStatus().equals("No rating")) {
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
