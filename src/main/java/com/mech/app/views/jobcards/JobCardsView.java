package com.mech.app.views.jobcards;

import com.mech.app.components.HeaderComponent;
import com.mech.app.dataproviders.jobcards.JobCardDataProvider;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
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
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@PageTitle("Job Cards")
@Route(value = "view/job-cards", layout = MainLayout.class)
//@Menu(order = 3, icon = LineAwesomeIconUrl.ADDRESS_CARD)
public class JobCardsView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private final Button jobCardButton = new Button("Create Job Card");
    private final Grid<JobCardDataProvider> jobCardGrid = new Grid<>();
    private final Grid<JobCardDataProvider.partsDataProvider> partsGrid = new Grid<>();

    public JobCardsView() {
        getContent().setWidth("100%");
        getContent().setHeight("max-content");
        getContent().getStyle().set("flex-grow", "1");
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
    private List<JobCardDataProvider> sampleData() {
        List<JobCardDataProvider> records = new ArrayList<>();
        var itemOne = List.of("Oil Filter", "Machine Part", "Breaks");
        var itemTwo = List.of("Tyres", "Wheels", "Screws", "Air Bag");
        var price1 = List.of(100, 233, 50, 23);
        var price2 = List.of(900, 333, 50, 14);

        var part1 = new HashMap<String, Object>();
        part1.put("item1", itemOne);
        part1.put("price1", price1);
        part1.put("item2", itemTwo);
        part1.put("price2", price2);
        // Record 1
        records.add(new JobCardDataProvider(
                1,
                "JC-2024-001",
                "Regular Maintenance",
                "Annual service and oil change",
                "Vehicle needs new oil filter",
                "In Progress",
                25.0,
                "",
                "Michael Brown",
                "Toyota Camry",
                "GR-235-24",
                part1,
                Timestamp.valueOf("2024-03-20 14:30:00")
        ));

        // Record 2
        records.add(new JobCardDataProvider(
                2,
                "JC-2024-002",
                "Brake Repair",
                "Squeaking brakes and vibration",
                "Front brake pads worn out",
                "Waiting for parts",
                50.0,
                "David Wilson",
                "Sarah Johnson",
                "Honda Civic",
                "GS-789-24",
                part1,
                Timestamp.valueOf("2024-03-22 11:00:00")
        ));

        // Record 3
        records.add(new JobCardDataProvider(
                3,
                "JC-2024-003",
                "Engine Diagnostics",
                "Check engine light on",
                "O2 sensor malfunction detected",
                "Completed",
                100.0,
                "Robert Quansah",
                "Emma Emmisah",
                "BMW X5",
                "GW-412-24",
                part1,
                Timestamp.valueOf("2024-03-18 09:00:00")
        ));

        // Record 4
        records.add(new JobCardDataProvider(
                4,
                "JC-2024-004",
                "Transmission Service",
                "Gear shifting issues",
                "Transmission fluid needs replacement",
                "In Progress",
                75.0,
                "Electric James",
                "William Adongo",
                "Mercedes C300",
                "GN-567-24",
                part1,
                Timestamp.valueOf("2024-03-21 13:15:00")
        ));

        // Record 5
        records.add(new JobCardDataProvider(
                5,
                "JC-2024-005",
                "AC Repair",
                "AC not cooling properly",
                "Pending inspection",
                "New",
                0.0,
                "Adu Kofi Mensah",
                "Oliver Vormawor",
                "Audi A4",
                "GT-891-24",
                part1,
                Timestamp.valueOf("2024-03-23 10:00:00")
        ));

        return records;
    }

    /*******************************************************************************************************************
     VIEW SECTION
     *******************************************************************************************************************/
    private Component pageBody() {

        var headerText = "Job Card List";
        var sub = "A list of all created job cards from your service request with progress status";

        var headerBox = new HeaderComponent().pageHeaderWithComponent(headerText, sub, jobCardButton);
        headerBox.addClassNames("job-card-header-box");

        VerticalLayout layout = new VerticalLayout(headerBox, gridConfiguration());
        layout.setWidthFull();
        layout.setHeight("max-content");
        layout.addClassNames("job-card-page-layout");

        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT RENDERERS
     *******************************************************************************************************************/
    private Component gridConfiguration() {
        jobCardGrid.addClassNames("alternative-grid-style");
        jobCardGrid.setSizeFull();

        jobCardGrid.addColumn(JobCardDataProvider::getJobNumber).setHeader("Job Number").setSortable(true);
        jobCardGrid.addComponentColumn(this::customerDetailsComponent).setHeader("Customer Information");
        jobCardGrid.addColumn(JobCardDataProvider::getServiceType).setHeader("Service Type");
        jobCardGrid.addColumn(new LocalDateRenderer<>(item -> item.getServiceDate().toLocalDateTime().toLocalDate())).setHeader("Date Booked");
        jobCardGrid.addComponentColumn(this::jobStatusComponent).setHeader("Job Status");
        jobCardGrid.addComponentColumn(this::jobProgressComponent).setHeader("Progress");
        jobCardGrid.setItemDetailsRenderer(jobDetailsComponent());
        jobCardGrid.setColumnRendering(ColumnRendering.EAGER);
        jobCardGrid.setItems(sampleData());

        var filterField = new TextField("", "Filter by job no, car number or service no");
        var filterContainer = new HeaderComponent().searchFieldComponent(filterField);

        filterField.addValueChangeListener(input -> {

        });

        var gridAndSearchFieldContainer = new VerticalLayout(filterContainer, jobCardGrid);
        gridAndSearchFieldContainer.addClassNames("grid-and-filter-box");
        gridAndSearchFieldContainer.setWidthFull();
        gridAndSearchFieldContainer.setHeight("fit-content");
        return gridAndSearchFieldContainer;
    }

    private Grid<JobCardDataProvider.partsDataProvider> partsGridConfiguration() {
        partsGrid.addClassNames("alternative-grid-style");
        partsGrid.setWidthFull();

        partsGrid.addColumn(JobCardDataProvider.partsDataProvider::itemName).setHeader("Item Name");
        partsGrid.addColumn(JobCardDataProvider.partsDataProvider::quantity).setHeader("Quantity").setSortable(true);
        partsGrid.addColumn(JobCardDataProvider.partsDataProvider::amount).setSortable(true).setHeader("Amount(Ghc)").setSortable(true);
        partsGrid.addComponentColumn(this::removeItemButtonRenderer).setHeader("Action");
        partsGrid.getColumns().forEach(i -> i.setAutoWidth(true));
        return partsGrid;
    }

    private Component removeItemButtonRenderer(JobCardDataProvider.partsDataProvider dataProvider) {
        Div div = new Div("❌");
        div.addClassNames("item-remove-btn");
        div.addSingleClickListener(e -> partsGrid.getListDataView().removeItem(dataProvider));
        return div;
    }

    //-----------------------------------------------------------------------------------------------------------------//
    private Component customerDetailsComponent(JobCardDataProvider dataProvider) {
        var h4 = new H4(dataProvider.getCustomerName());
        var subText = new Paragraph(String.format("%s - (%s)", dataProvider.getCar(), dataProvider.getPlateNumber()));
        var layout = new Section(h4, subText);
        layout.addClassNames("job-card-grid-customer-info");
        return layout;
    }

    private Component jobStatusComponent(JobCardDataProvider dataProvider) {
        var item = new Div(LineAwesomeIcon.CLOCK.create(), new Span(dataProvider.getJobStatus()));
        item.setWidthFull();
        item.getStyle().setBoxSizing(Style.BoxSizing.BORDER_BOX)
                .setDisplay(Style.Display.INLINE_FLEX)
                .setJustifyContent(Style.JustifyContent.SPACE_BETWEEN)
                .set("gap", "3px")
                .setWidth("min-content")
                .setPadding("5px");

        switch (dataProvider.getJobStatus()) {
            case "In Progress" -> item.getElement().getThemeList().add("badge small");
            case "Waiting for parts" -> item.getElement().getThemeList().add("badge warning pill small");
            case "Completed" -> item.getElement().getThemeList().add("badge success pill small");
            case "New" -> item.getElement().getThemeList().add("badge contrast small");
            default -> item.getElement().getThemeList().add("badge error pill small");
        }
        return item;
    }

    private Component jobProgressComponent(JobCardDataProvider dataProvider) {
        var item = new Div(String.format("%s", dataProvider.getProgressValue()) + "% Done");
        item.addClassNames("progress-value-box");
        return item;
    }

    private Renderer<JobCardDataProvider> jobDetailsComponent() {
        return new ComponentRenderer<>(dataProvider -> {
            var assignedTechnician = dataProvider.getAssignedTechnician().isEmpty() || dataProvider.getAssignedTechnician().isBlank();
            var techs = List.of("Tech One", "Tech Two", "Tech Three");
            var technicianSelector = new ComboBox<>("Select Technician", techs);
            technicianSelector.addClassNames("combo-box-style");
            technicianSelector.setErrorMessage("Current job has no assigned technician, kindly assign a technician to proceed.");
            technicianSelector.setInvalid(assignedTechnician);
            Button assignButton = new Button(LineAwesomeIcon.PUSHED.create());
            assignButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            assignButton.setWidth("min-content");

            FlexLayout selectorAndButton = new FlexLayout(technicianSelector, assignButton);
            selectorAndButton.getStyle().set("gap", "5px");
            selectorAndButton.addClassNames("job-card-selector-and-button-box");

            RadioButtonGroup<String> jobStatusButtons = new RadioButtonGroup<>();
            jobStatusButtons.addClassName("radio-buttons-group-style");
            jobStatusButtons.setLabel("Job Status");
            jobStatusButtons.setItems(ComponentLoader.getJobStatusList());
            jobStatusButtons.setValue(dataProvider.getJobStatus());

            Section section1 = new Section(jobStatusButtons);
            section1.addClassNames("job-card-details-section1");

            //SECTION 2 CONTENT
            RadioButtonGroup<String> progressButtons = new RadioButtonGroup<>("", "0.0%", "25.0%", "50.0%", "75.0%", "100%");
            progressButtons.addClassNames("check-buttons-group-style");
            progressButtons.setSelectionPreservationMode(SelectionPreservationMode.PRESERVE_EXISTING);

            ProgressBar progressBar = new ProgressBar();
            progressBar.setWidthFull();
            progressBar.setHeight("10px");
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            progressBar.addClassName("job-progress-bar");
            progressBar.setValue((dataProvider.getProgressValue() * 0.01));

            progressButtons.setValue(dataProvider.getProgressValue() + "%");

            progressButtons.addValueChangeListener(event -> {
                try {
                    var value = Double.parseDouble(event.getSource().getValue().replace("%", ""));
                    Notification.show(String.format("Job progress update to %s", event.getValue()));
                    progressBar.setValue((value) * 0.01);
                }catch(NullPointerException ignore) {}

            });

            var progressBarAndLabel = new Div(new H5("Job Progress Indicator"), progressBar);
            progressBarAndLabel.addClassNames("progress-bar-and-label");

            TextArea noteArea = new TextArea("Technician Notes");
            noteArea.addClassNames("item-text-area");

            var section2 = new Section(progressBarAndLabel, progressButtons, noteArea);
            section2.addClassNames("job-card-details-section1");

            //SECTION THREE CONTENT...
            Button updateStatus = new Button("Update Job Card");
            updateStatus.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            updateStatus.addClassNames("default-btn-style");
            updateStatus.setWidth("fit-content");

            Button addPartsButton = new Button("Add Parts");
            addPartsButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            addPartsButton.addClassNames("default-btn-style");
            addPartsButton.setWidth("fit-content");

            var section3 = new HorizontalLayout(updateStatus, addPartsButton);
            section3.getStyle().set("gap", "10px");
            section3.addClassNames("job-card-button-box");

            var innerLayout = new VerticalLayout(section1, section2, section3);
            innerLayout.addClassNames("job-card-details-inner-layout");

            VerticalLayout layout = new VerticalLayout(selectorAndButton, innerLayout);
            layout.addClassNames("job-car-details-layout");
            innerLayout.setVisible(!assignedTechnician);

            //ACTION EVENT LISTENERS
            addPartsButton.addSingleClickListener(e -> {
                addPartsComponent(dataProvider.getJobNumber()).open();
            });
            return layout;
        });
    }

    private Dialog addPartsComponent(String jobNumber) {
        H4 h4 = new H4(String.format("Add Part For (%s)", jobNumber));
        Span span = new Span("Add parts required for the job. These will be included in the job card.");
        var header = new Section(h4, span);
        header.addClassNames("add-part-header-container");

        var dialog = new Dialog();
        dialog.addClassNames("add-part-dialog");
        dialog.getHeader().add(header);

        TextField partName = new TextField("Part Or item Name", "eg Bread Pad Set");
        IntegerField qtyField = new IntegerField("Quantity");
        BigDecimalField amountField = new BigDecimalField("Amount");
        Button saveButton = new Button("Save Date", LineAwesomeIcon.SAVE.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        Button addItemButton = new Button("Add", LineAwesomeIcon.PLUS_SOLID.create());
        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        var fieldsBox = new HorizontalLayout(partName, qtyField, amountField, addItemButton);
        fieldsBox.addClassNames("add-part-fields-box");

        amountField.setPrefixComponent(new H5("Ghc"));
        partName.addClassNames("input-style");
        qtyField.addClassNames("input-style", "number-field-item");
        amountField.addClassNames("input-style", "number-field-item");
        qtyField.setStepButtonsVisible(true);

        VerticalLayout tableBox = new VerticalLayout(partsGridConfiguration());
        tableBox.addClassNames("add-part-table-box");

        dialog.add(fieldsBox, tableBox, saveButton);

        //ACTION EVENT LISTENERS
        addItemButton.addClickListener(
                e -> {
                    var item = new JobCardDataProvider.partsDataProvider(jobNumber, partName.getValue(), qtyField.getValue(), amountField.getValue().doubleValue());
                    partsGrid.getListDataView().addItem(item);
                });

        return dialog;
    }

}//end of class...
