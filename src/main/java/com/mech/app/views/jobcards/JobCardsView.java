package com.mech.app.views.jobcards;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.HeaderComponent;
import com.mech.app.components.MenuBarButtons;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.jobcards.JobCardDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.models.ServiceRequestModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.shared.SelectionPreservationMode;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import org.jetbrains.annotations.NotNull;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@PageTitle("Job Cards")
@Route(value = "view/job-cards", layout = MainLayout.class)
//@Menu(order = 3, icon = LineAwesomeIconUrl.ADDRESS_CARD)
public class JobCardsView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private final Button jobCardButton = new Button("Create Job Card");
    private final Grid<JobCardDataProvider.JobCardRecords> jobCardGrid = new Grid<>();
    private final Grid<JobCardDataProvider.VehiclePartsRecord> partsGrid = new Grid<>();
    private static ServiceRequestModel DATA_MODEL_OBJECT = new ServiceRequestModel();
    private AtomicInteger USER_ID, SHOP_ID;
    private static CustomDialog dialog;

    public JobCardsView() {
        getContent().setWidth("100%");
        getContent().setHeight("max-content");
        getContent().getStyle().set("flex-grow", "1");
        USER_ID = new AtomicInteger(SessionManager.DEFAULT_USER_ID);
        SHOP_ID = new AtomicInteger(SessionManager.DEFAULT_SHOP_ID);

        getContent().add(pageBody());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    public void onAttach(AttachEvent event) {
        jobCardButton.setWidth("min-content");
        jobCardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/
    private List<JobCardDataProvider.JobCardRecords> sampleData() {
        return DATA_MODEL_OBJECT.fetchAllActiveJobCards();
    }

    //this method when invoked shall update selected job card details with the provided params
    private void updateJobCardProcess(int jobId, int serviceId, String progressValue, String status, String note) {
        dialog = new CustomDialog();
        var confirmDialog = dialog.showUpdateDialog("UPDATE JOB CARD", MessageLoaders.confirmationMessage("update current job card status"));

        UI ui = UI.getCurrent();
        ui.access(() -> {
            confirmDialog.addConfirmListener(event -> {
                String logMsg = String.format("Job No: %s has been updated by assigned mechanic. Job progress is %s done and status is %s. Check job cards for details",
                        jobId, progressValue, status);
                NotificationRecords logRecord = new NotificationRecords("JOB CARD UPDATE", logMsg, USER_ID.get(), SHOP_ID.get());

                int statusCode = DATA_MODEL_OBJECT.updateJobCardStatus(jobId, serviceId, progressValue, status, note);
                if (statusCode > 0) {
                    DATA_MODEL_OBJECT.logNotification(logRecord);
                    ui.refreshCurrentRoute(true);
                } else dialog.showErrorNotification("Failed to update job card details, retry the process.");
            });
        });


    }

    /*******************************************************************************************************************
     VIEW SECTION
     *******************************************************************************************************************/
    private Component pageBody() {

        var headerText = "Job Card List";
        var sub = "A list of all created job cards from your service request with progress status";

        var headerBox = new HeaderComponent().textHeader(headerText, sub);
        headerBox.addClassNames("job-card-header-box");

        VerticalLayout layout = new VerticalLayout(headerBox, jobCardsTable());
        layout.setWidthFull();
        layout.setHeight("max-content");
        layout.addClassNames("job-card-page-layout");

        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT RENDERERS
     *******************************************************************************************************************/
    private Component jobCardsTable() {
        jobCardGrid.addClassNames("alternative-grid-style");
        jobCardGrid.setSizeFull();

        jobCardGrid.addColumn(JobCardDataProvider.JobCardRecords::jobNo).setHeader("Job No").setSortable(true);
        jobCardGrid.addColumn(JobCardDataProvider.JobCardRecords::serviceType).setHeader("Service Type").setSortable(true);
        jobCardGrid.addComponentColumn(this::customerDetailsComponent).setHeader("Customer Information");
        jobCardGrid.addColumn(new LocalDateRenderer<>(item -> item.loggedDate().toLocalDateTime().toLocalDate())).setHeader("Date Booked");
        jobCardGrid.addComponentColumn(this::jobStatusComponent).setHeader("Job Status").setTextAlign(ColumnTextAlign.CENTER);
        jobCardGrid.addComponentColumn(this::jobProgressComponent).setHeader("Progress(%)").setTextAlign(ColumnTextAlign.CENTER);
        jobCardGrid.addColumn(jobsActionButtons()).setHeader("Actions").setTextAlign(ColumnTextAlign.CENTER);
        jobCardGrid.setColumnRendering(ColumnRendering.EAGER);
        jobCardGrid.setItems(sampleData());
        jobCardGrid.getColumns().forEach(col -> col.setAutoWidth(true));

//        jobCardGrid.setItemDetailsRenderer(jobDetailsComponent());

        var filterField = new TextField("", "Filter by job no, car number or service no");
        var filterContainer = new HeaderComponent().searchFieldComponent(filterField);

        filterField.addValueChangeListener(input -> {
            String userInput = input.getValue().toLowerCase();
            jobCardGrid.getListDataView().setFilter(filter -> {
                var matchesServiceId = filter.serviceNo().toLowerCase().contains(userInput);
                var matchesServiceType = filter.serviceType().toLowerCase().contains(userInput);
                var matchesCustomer = filter.customerName().toLowerCase().contains(userInput);

                return matchesServiceId || matchesServiceType || matchesCustomer;
            });
        });

        var gridAndSearchFieldContainer = new VerticalLayout(filterContainer, jobCardGrid);
        gridAndSearchFieldContainer.addClassNames("grid-and-filter-box");
        gridAndSearchFieldContainer.setWidthFull();
        gridAndSearchFieldContainer.setHeight("fit-content");
        return gridAndSearchFieldContainer;
    }

    private Grid<JobCardDataProvider.VehiclePartsRecord> carPartsTable() {
//        partsGrid.removeAllColumns();
        partsGrid.addClassNames("alternative-grid-style");
        partsGrid.setWidthFull();
        partsGrid.setHeight("350px");

        partsGrid.addColumn(JobCardDataProvider.VehiclePartsRecord::itemName).setHeader("Item Name");
        partsGrid.addColumn(JobCardDataProvider.VehiclePartsRecord::quantity).setHeader("Quantity").setSortable(true);
        partsGrid.addColumn(JobCardDataProvider.VehiclePartsRecord::amount).setSortable(true).setHeader("Amount(Ghc)").setSortable(true);
        partsGrid.addComponentColumn(this::removeItemButtonRenderer).setHeader("Action");
        partsGrid.getColumns().forEach(i -> i.setAutoWidth(true));
        return partsGrid;
    }

    private Component removeItemButtonRenderer(JobCardDataProvider.VehiclePartsRecord dataProvider) {
        Div div = new Div("❌");
        div.addClassNames("item-remove-btn");
        div.addSingleClickListener(e -> partsGrid.getListDataView().removeItem(dataProvider));
        return div;
    }

    //COMPONENT RENDERS
    // -----------------------------------------------------------------------------------------------------------------//
    private Component customerDetailsComponent(JobCardDataProvider.JobCardRecords dataProvider) {
        var h4 = new H4(dataProvider.customerName());
        var subText = new Paragraph(String.format("%s - (%s)", dataProvider.vehicleName(), dataProvider.VIN()));
        var layout = new Section(h4, subText);
        layout.addClassNames("job-card-grid-customer-info");
        return layout;
    }

    private Component jobStatusComponent(JobCardDataProvider.JobCardRecords dataProvider) {
        var item = new Div(LineAwesomeIcon.CLOCK.create(), new Span(dataProvider.jobStatusValue()));
        item.setWidthFull();
        item.getStyle().setBoxSizing(Style.BoxSizing.BORDER_BOX)
                .setDisplay(Style.Display.INLINE_FLEX)
                .setJustifyContent(Style.JustifyContent.SPACE_BETWEEN)
                .set("gap", "3px")
                .setWidth("min-content")
                .setPadding("5px");

        switch (dataProvider.jobStatusValue()) {
            case "In progress" -> item.getElement().getThemeList().add("badge small success pill");
            case "Awaiting parts" -> item.getElement().getThemeList().add("badge warning pill small");
            case "Completed" -> item.getElement().getThemeList().add("badge success pill small");
            case "New" -> item.getElement().getThemeList().add("badge contrast small pill");
            default -> item.getElement().getThemeList().add("badge error pill small");
        }
        return item;
    }

    private Component jobProgressComponent(JobCardDataProvider.JobCardRecords dataProvider) {
        double progress = Double.parseDouble(dataProvider.progressValue());
        var item = new Div(String.format("%s%s", progress, "%"));
        item.addClassNames("progress-value-box");
        if (progress < 25) {
            item.getStyle().setColor("var(--lumo-error-text-color)");
        } else if (progress == 25.0) {
            item.getStyle().setColor("var(--lumo-success-text-color)");
        } else if (progress == 75.0) {
            item.getStyle().setColor("var(--lumo-warning-color)");
        } else {
            item.getStyle().setColor("var( --lumo-success-color)");
        }
        return item;
    }

    private void updateJobCardDialog(@NotNull JobCardDataProvider.JobCardRecords dataProvider) {
        double progress = Double.parseDouble(dataProvider.progressValue());

        RadioButtonGroup<String> jobStatusButtons = new RadioButtonGroup<>();
        jobStatusButtons.addClassName("radio-buttons-group-style");
        jobStatusButtons.setLabel("Job Status");
        jobStatusButtons.setItems(ComponentLoader.getJobStatusList());
        jobStatusButtons.setValue(dataProvider.jobStatusValue());
        jobStatusButtons.setValue("In Progress");

        Section section1 = new Section(jobStatusButtons);
        section1.addClassNames("job-card-details-section1");

        //SECTION 2 CONTENT
        RadioButtonGroup<String> progressButtons = new RadioButtonGroup<>("", "0.0", "25.0", "50.0", "75.0", "100");
        progressButtons.addClassNames("check-buttons-group-style");
        progressButtons.setSelectionPreservationMode(SelectionPreservationMode.PRESERVE_EXISTING);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        progressBar.setHeight("10px");
        progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
        progressBar.addClassName("job-progress-bar");
        progressBar.setValue((progress * 0.01));

        progressButtons.setValue(String.valueOf(progress));

        progressButtons.addValueChangeListener(event -> {
            try {
                var value = Double.parseDouble(event.getSource().getValue().replace("%", ""));
                Notification.show(String.format("Job progress updated to %s", event.getValue() + "%"));
                progressBar.setValue((value) * 0.01);
            } catch (NullPointerException ignore) {
            }

        });

        var progressBarAndLabel = new Div(new H5("Job Progress Indicator"), progressBar);
        progressBarAndLabel.addClassNames("progress-bar-and-label");

        TextArea noteArea = new TextArea("Technician Notes", "Kindly provide job update description");
        noteArea.addClassNames("item-text-area");
        noteArea.setRequired(true);
        noteArea.getStyle().setMargin("8px 0");
        noteArea.setInvalid(noteArea.isEmpty());
        noteArea.setErrorMessage("Job update description is required");

        var section2 = new Section(progressBarAndLabel, progressButtons, noteArea);
        section2.addClassNames("job-card-details-section1");

        //SECTION THREE CONTENT...
        Button updateStatus = new Button("Update Job Card");
        updateStatus.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateStatus.addClassNames("default-btn-style");
        updateStatus.setWidth("fit-content");
        updateStatus.setWidthFull();

        //click event listener on update button
        updateStatus.addSingleClickListener(click -> {
            if (!noteArea.isInvalid()) {
                var progressValue = progressButtons.getValue() == null ? dataProvider.progressValue() : progressButtons.getValue();
                var jobStatusValue = Objects.equals(jobStatusButtons.getValue(), "On hold") ? "on_hold" :
                        Objects.equals(jobStatusButtons.getValue(), "Awaiting parts") ? "awaiting" :
                                Objects.equals(jobStatusButtons.getValue(), "In Progress") ? "progress" : "completed";

                //call method to perform data processing for database...
                updateJobCardProcess(dataProvider.jobId(), dataProvider.serviceId(), progressValue, jobStatusValue, noteArea.getValue());
            }
        });

        var section3 = new HorizontalLayout(updateStatus);
        section3.getStyle().set("gap", "10px");
        section3.addClassNames("job-card-button-box");
        section3.setWidthFull();

        var innerLayout = new VerticalLayout(section1, section2, section3);
        innerLayout.addClassNames("job-card-details-inner-layout");

        VerticalLayout layout = new VerticalLayout(innerLayout);
        layout.addClassNames("job-car-details-layout");

        var dialog = new CustomDialog();
        dialog.defaultDialogBox(layout, "UPDATE JOB CARD - " + dataProvider.jobNo(), "Provide progress details to this job card.");
    }

//    private Renderer<JobCardDataProvider.JobCardRecords> jobDetailsComponent() {
//        return new ComponentRenderer<>(data -> {
//            var jobData = DATA_MODEL_OBJECT.fetchJobDescription(data.jobId());
//
//            VerticalLayout layout = new VerticalLayout();
//            layout.setWidthFull();
//            layout.addClassNames("job-card-details-layout");
//
//            H5 title = new H5("Client Complaint");
//            var description = new Paragraph(data.serviceDesc());
//            Div customerDiv = new Div(title, description);
//            customerDiv.setWidthFull();
//            customerDiv.addClassName("client-desc-div");
//
//            Div jobDescContainer = new Div();
//            jobDescContainer.setWidthFull();
//            jobDescContainer.addClassName("job-desc-div");
//
//            JobCardDetailsView.extractJobDetails(jobData, jobDescContainer);
//
//            layout.add(customerDiv, jobDescContainer);
//            return layout;
//        });
//    }

    private Renderer<JobCardDataProvider.JobCardRecords> jobsActionButtons() {
        return new ComponentRenderer<>(data -> {
            MenuBar menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_END_ALIGNED);
            menuBar.getStyle()
                    .setBackgroundColor("transparent")
                    .setPadding("0")
                    .setAlignItems(Style.AlignItems.CENTER)
                    .setJustifyContent(Style.JustifyContent.START);
            menuBar.addClassNames("menu-bar-style");

            //create and implement click listener for print Button
            menuBar.addItem(new MenuBarButtons("Print", LineAwesomeIcon.PRINT_SOLID).createMenuButton(), e -> {

            });

            //create and implement click listener for UPDATE Button
            menuBar.addItem(new MenuBarButtons("Update", LineAwesomeIcon.EDIT_SOLID).createMenuButton(), e -> {
                updateJobCardDialog(data);
            });

            //create and implement click listener for VIEW-PROGRESS Button
            menuBar.addItem(new MenuBarButtons("View Progress", LineAwesomeIcon.EYE_SOLID).createMenuButton(), e -> {

                var paramMap = new HashMap<String, List<String>>();
                paramMap.put("service_id", List.of(String.valueOf(data.serviceId())));
                paramMap.put("job_id", List.of(String.valueOf(data.jobId())));
                QueryParameters param = new QueryParameters(paramMap);
                getUI().flatMap(ui -> ui.navigate(JobCardDetailsView.class, param));
            });

            //create and implement click listener for ADD-PART Button
            menuBar.addItem(new MenuBarButtons("Add Item", LineAwesomeIcon.PLUS_CIRCLE_SOLID).createMenuButton(), e -> {
                addPartsComponent(data.jobId());
            });

            return menuBar;
        });
    }

    private void addPartsComponent(int jobNumber) {
        String title = String.format("Add Part For (%s)", jobNumber);
        var subTitle = "Add parts required for the job. These will be included in the job card.";

        TextField partName = new TextField("Part Or item Name", "eg Bread Pad Set");
        IntegerField qtyField = new IntegerField("Quantity");
        BigDecimalField amountField = new BigDecimalField("Amount");
        Button saveButton = new Button("Save Date", LineAwesomeIcon.SAVE.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        Button addItemButton = new Button("Add", LineAwesomeIcon.PLUS_SOLID.create());
        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        var fieldsBox = new FormLayout();
        fieldsBox.add(partName, 2);
        fieldsBox.add(qtyField, amountField);
        fieldsBox.add(addItemButton, 2);
//        FormLayout.ResponsiveStep colOne = new FormLayout.ResponsiveStep("0", 1);
//        FormLayout.ResponsiveStep colFour    = new FormLayout.ResponsiveStep("768px", 4);
//        fieldsBox.setResponsiveSteps(colOne, colFour);

        fieldsBox.addClassNames("add-part-fields-box", "col-span-4");

        amountField.setPrefixComponent(new H5("Ghc"));
        partName.addClassNames("input-style");
        qtyField.addClassNames("input-style", "number-field-item");
        amountField.addClassNames("input-style", "number-field-item");
        qtyField.setStepButtonsVisible(true);

        VerticalLayout tableBox = new VerticalLayout(fieldsBox, carPartsTable(), new Hr(), saveButton);
        tableBox.addClassNames("add-part-table-box");

        var dialog = new CustomDialog();
        dialog.defaultDialogBox(tableBox, title, subTitle);

        //ACTION EVENT LISTENERS
        addItemButton.addClickListener(
                e -> {
                    boolean fieldEmpty = partName.isEmpty() || qtyField.isEmpty() || amountField.isEmpty();
                    if (!fieldEmpty) {
                        var item = new JobCardDataProvider.VehiclePartsRecord(jobNumber, partName.getValue(), qtyField.getValue(), amountField.getValue().doubleValue());
                        partsGrid.getListDataView().addItem(item);
                        partName.clear();
                        qtyField.clear();
                        amountField.clear();
                    }
                });

        saveButton.addClickListener(e -> {
            UI ui = UI.getCurrent();
            if (partsGrid.getListDataView().getItems().findAny().isPresent()) {
                partsGrid.getListDataView().getItems().forEach(item -> {

                    var totalItems = partsGrid.getListDataView().getItemCount();
                    var logMsg = String.format("New items added to service Job no. %s. Total items added were %s today %s", item.jobNo(), totalItems, LocalDate.now());
                    NotificationRecords logRecord = new NotificationRecords("NEW ITEM ADDED", logMsg, USER_ID.get(), SHOP_ID.get());

                    var confirmDialog = dialog.showUpdateDialog("Update ", MessageLoaders.confirmationMessage("add items to job card"));
                    confirmDialog.addConfirmListener(confirm -> {
                        ui.access(() -> {
                            int statusCode = DATA_MODEL_OBJECT.addPurchasedPart(item.jobNo(), item.itemName(), item.quantity(), item.amount());
                            if (statusCode > 0) {
                                DATA_MODEL_OBJECT.logNotification(logRecord);
                                ui.refreshCurrentRoute(false);
                            } else
                                dialog.showErrorNotification(MessageLoaders.errorMessage("Failed to add table items to job card!"));
                        });
                    });
                });
            } else {
                Notification.show("Items table is empty, enter at least one item and save", 4000, Notification.Position.TOP_CENTER);
            }
        });

    }

}//end of class...
