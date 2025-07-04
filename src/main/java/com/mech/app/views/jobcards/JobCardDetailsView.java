package com.mech.app.views.jobcards;

import com.mech.app.components.HeaderComponent;
import com.mech.app.components.MenuBarButtons;
import com.mech.app.dataproviders.jobcards.JobCardDataProvider;
import com.mech.app.models.ServiceRequestModel;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Route(value = "job-details", layout = MainLayout.class)
@PageTitle("Job Details")
@PermitAll
public class JobCardDetailsView extends VerticalLayout implements BeforeEnterObserver {

    private static final AtomicInteger JOB_ID = new AtomicInteger();
    private static final AtomicInteger SERVICE_ID = new AtomicInteger();
    private static ServiceRequestModel DATA_MODEL_OBJECT = new ServiceRequestModel();


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var params = event.getLocation().getQueryParameters().getParameters();
        ;
        event.getLocation().getQueryParameters().getSingleParameter("service_id").ifPresentOrElse(id -> {
            SERVICE_ID.set(Integer.parseInt(id));
        }, () -> JOB_ID.set(0));

        event.getLocation().getQueryParameters().getSingleParameter("job_id").ifPresentOrElse(id -> {
            JOB_ID.set(Integer.parseInt(id));
        }, () -> JOB_ID.set(0));

    }

    @Override
    public void onAttach(AttachEvent event) {
        add(pageHeader(), bodyContent());
    }
    public JobCardDetailsView() {
        setWidthFull();
        setClassName("page-body");
        setHeight("max-content");
        getStyle().set("flex-grow", "1");
    }


    /*******************************************************************************************************************
     PAGE BODY
     *******************************************************************************************************************/
    private Component pageHeader() {
        var title = String.format("Job Details - %s", JOB_ID.get());
        var subTitle = "A detailed job description with items purchased for the job";

        Button backButton = new Button("Back", LineAwesomeIcon.ARROW_LEFT_SOLID.create(),
                e -> UI.getCurrent().navigate(JobCardsView.class));
        backButton.addClassNames("back-button");

        return new HeaderComponent().pageHeaderWithComponent(title, subTitle, backButton);
    }

    private Component bodyContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.addClassNames("job-card-details-layout", "col-span-2");

        var data = sampleData().stream().filter(item -> item.serviceId() == SERVICE_ID.get()).toList();
        var jobData = DATA_MODEL_OBJECT.fetchJobDescription(JOB_ID.get());

        H5 title = new H5("Client Complaint");
        var description = new Paragraph(data.getFirst().serviceDesc());

        var customerDiv = new Div(title, description);
        customerDiv.setWidthFull();
        customerDiv.addClassName("client-desc-div");

        Div jobDescContainer = new Div();
        jobDescContainer.setWidthFull();
        jobDescContainer.addClassName("job-desc-div");

        if (jobData.isEmpty()) {
            jobDescContainer.add(emptyItemsComponent());
        } else {
            extractJobDetails(jobData, jobDescContainer);
        }

        Div parentLayout = new Div(layout, purchasedItemsContent());

        parentLayout.getStyle().setDisplay(Style.Display.GRID);
        parentLayout.addClassNames("grid-cols-3", "job-details-parent-layout");
        layout.add(customerDiv, jobDescContainer);
        return parentLayout;
    }

    private Component purchasedItemsContent() {
        H5 itemsHeader = new H5("Purchased Items");
        var itemsContainer = new Div();
        itemsContainer.setWidthFull();
        itemsHeader.addClassName("client-desc-div");

        itemsContainer.addClassNames("item-parent-container");

        var data = DATA_MODEL_OBJECT.fetchJobPurchasedItemsData(JOB_ID.get());

        if (data.isEmpty()) {
            itemsHeader.add(emptyItemsComponent());
        }else {
            for (var item : data) {
                H6 name = new H6(String.format("%s. %s",item.itemId(), item.name()));
                name.addClassNames("purchase-item-name","col-span-2");
                name.setWidthFull();

                Span qty = new Span("Quantity");
                H6 qtyValue = new H6(String.valueOf(item.qty()));
                Div qtyDiv = new Div(qty, qtyValue);
                qtyDiv.setClassName("job-details-items-div");

                H6 priceValue = new H6(String.valueOf(item.price()));
                Span price = new Span("Price");
                Div amountDiv = new Div(price, priceValue);
                amountDiv.setClassName("job-details-items-div");

                Div flexLayout = new Div(name, qtyDiv, amountDiv);
                flexLayout.getStyle().setDisplay(Style.Display.GRID);
                flexLayout.addClassNames("grid-cols-2", "job-details-items-grid-layout");
                itemsContainer.add(flexLayout);
            }
        }


        VerticalLayout layout = new VerticalLayout(itemsHeader, itemsContainer);
        layout.setWidthFull();
        layout.addClassNames("col-span-1", "items-parent-layout");

        return layout;
    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/
    private List<JobCardDataProvider.JobCardRecords> sampleData() {
        return DATA_MODEL_OBJECT.fetchAllActiveJobCards();
    }
    static void extractJobDetails(List<JobCardDataProvider.JobDescptionRecord> jobData, Div jobDescContainer) {
        for (var item : jobData) {
            Button button = new Button("Update", e -> {
                Notification.show("jOB ID: " + item.recordId());
            });
            button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON);
            button.addClassNames("default-button-style");
            var date = item.noteDate().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));

            TextArea textArea = new TextArea("Note Date: " + date, item.jobDesc(), "");
            textArea.setReadOnly(true);
            jobDescContainer.add(textArea, button);
        }
    }

    /*******************************************************************************************************************
     COMPONENT RENDERERS
     *******************************************************************************************************************/
    private Component emptyItemsComponent() {
        Div div = new Div("No data to display for job no: " + JOB_ID.get());
        div.setWidthFull();
        div.getStyle().setBoxSizing(Style.BoxSizing.BORDER_BOX);
        div.getStyle().setPadding("10px")
                .setMargin("auto")
                .setBorder("1px solid #ddd")
                .setBorderRadius("5px");

        return div;
    }

}//
