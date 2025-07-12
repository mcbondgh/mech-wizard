package com.mech.app.views.servicerequests;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.HeaderComponent;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.servicesrequest.ServicesDataProvider;
import com.mech.app.enums.SubRoles;
import com.mech.app.models.CustomerModel;
import com.mech.app.models.ServiceRequestModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.dashboard.CustomerDashboardView;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.Menu;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("My Services")
@Route(value = "view/customer-services")
@Menu(icon = LineAwesomeIconUrl.TOOLBOX_SOLID)
@RolesAllowed("CUSTOMER")
public class CustomerServiceView extends VerticalLayout implements BeforeEnterObserver {

    private final Grid<ServicesDataProvider.BookedServicesRecord> servicesGrid = new Grid<>();
    private final ComboBox<String> vehicleSelector = new ComboBox<>("Select Car");
    private final TextField plateNumberField = new TextField("Plate Number");
    private final TextArea problemDescriptionField = new TextArea("Describe your Problem", "Please describe any specific issues or concerns here");
    private final NumberField costField = new NumberField("Service Price");
    private final ComboBox<String> serviceSelector = new ComboBox<>("Select Your Service");
    private final Button addServiceButton = new Button("New Service", LineAwesomeIcon.PLUS_SOLID.create());
    private final Checkbox priorityCheckbox = new Checkbox("Pickup");
    private final ComboBox<String> urgencySelector = new ComboBox<>("Urgency Level");
    private final DatePicker pickupDateSelector = new DatePicker("Preferred Date", LocalDate.now());

    private static ServiceRequestModel DAO_MODEL;
    private static CustomerModel CUSTOMER_MODEL;

    private static int SHOP_ID, USER_ID;
    private static int CUSTOMER_ID;
    private static AtomicReference<String> ACCESS_TYPE;

    public CustomerServiceView() {
        setHeightFull();
        setWidthFull();
        DAO_MODEL = new ServiceRequestModel();
        CUSTOMER_MODEL = new CustomerModel();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            SHOP_ID = Integer.parseInt(SessionManager.getAttribute("shopId").toString());
            USER_ID = Integer.parseInt(SessionManager.getAttribute("userId").toString());
            CUSTOMER_ID = Integer.parseInt(SessionManager.getAttribute("clientId").toString());
            var allowedRole = List.of(SubRoles.values()).toString().toLowerCase();
            ACCESS_TYPE = new AtomicReference<>(SessionManager.getAttribute("role").toString());
            if (!allowedRole.contains(ACCESS_TYPE.get().toLowerCase())) {
                event.rerouteTo(LoginView.class);
            }
        } catch (NullPointerException ex) {
            event.rerouteTo(LoginView.class);
        }
    }

    @Override
    public void onAttach(AttachEvent event) {
        serviceSelector.addClassNames("combo-box");
        serviceSelector.setRequired(true);
        vehicleSelector.setRequired(true);
        problemDescriptionField.setRequired(true);
        vehicleSelector.addClassNames("combo-box");
        priorityCheckbox.addClassNames("check-box-style");
        costField.setPrefixComponent(new H4("Ghc "));
        costField.setReadOnly(true);
        costField.addClassNames("input-style");
        ComponentLoader.setServiceTypes(serviceSelector, SHOP_ID);
        addServiceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ComponentLoader.setUrgencyLevel(urgencySelector);

        Icon icon = VaadinIcon.BOOK.create();
        H2 viewTitle = new H2("Book a Service");
        FlexLayout divOne = new FlexLayout(icon, new Paragraph(), viewTitle);

        var navButton = new Button("Dashboard", VaadinIcon.DASHBOARD.create(), e -> {
            UI.getCurrent().navigate(CustomerDashboardView.class);
        });
        navButton.addClassNames("default-button-style");

        var headerLayout = new HeaderComponent().dashboardHeader(divOne, navButton);
        add(headerLayout, pageBody());
    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/
    private List<ServicesDataProvider.BookedServicesRecord> serviceRequestsData() {
        return DAO_MODEL.fetchAllServiceRequestsNotDeleted(SHOP_ID)
                .stream()
                .filter(ex ->
                        !(ex.serviceStatus().equalsIgnoreCase("completed") || ex.serviceStatus().equalsIgnoreCase("paid"))
                                && ex.customerId() == CUSTOMER_ID).toList();
    }

    List<CarDataProvider> loadVehicles() {
        return DAO_MODEL.fetchCustomerCarsById(CUSTOMER_ID);
    }

    /*******************************************************************************************************************
     BODY SECTION
     *******************************************************************************************************************/
    private VerticalLayout pageBody() {
        FlexLayout buttonsContainer = new FlexLayout(new H4("Request a new service or track service progress"), addServiceButton);
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
        servicesGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
        servicesGrid.addColumn(ServicesDataProvider.BookedServicesRecord::serviceNo).setHeader("Service No");
        servicesGrid.addColumn(ServicesDataProvider.BookedServicesRecord::vehicle).setHeader("Car Details");
        servicesGrid.addColumn(ServicesDataProvider.BookedServicesRecord::serviceType).setHeader("Service Type");
        servicesGrid.addColumn(serviceStatusRenderer()).setHeader("Status").setTextAlign(ColumnTextAlign.CENTER);
        servicesGrid.addColumn(viewDetailsButton()).setHeader("Action").setTextAlign(ColumnTextAlign.CENTER);
        servicesGrid.setItems(serviceRequestsData());
//        servicesGrid.setDetailsVisibleOnClick(true);
        servicesGrid.setItemDetailsRenderer(serviceDetailsComponent());
        servicesGrid.addClassNames("default-grid-style");
        return servicesGrid;
    }

    private Renderer<ServicesDataProvider.BookedServicesRecord> serviceStatusRenderer() {
        return new ComponentRenderer<>(data -> {
            Span span = new Span(data.statusValue());
            switch (data.serviceStatus()) {
                case "new" -> span.getElement().getThemeList().add("badge primary small pill");
                case "assigned" -> span.getElement().getThemeList().add("badge primary warning small pill");
                case "cancelled" -> span.getElement().getThemeList().add("badge primary error small pill");
            }
            return span;
        });
    }

    private Renderer<ServicesDataProvider.BookedServicesRecord> viewDetailsButton() {
        return new ComponentRenderer<>(dataProvider -> {
            var menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_CONTRAST);
            menuBar.getStyle().setBackgroundColor("transparent");

            var cancelBtn = menuBar.addItem("Cancel", "Terminate service", e -> {
                Notification.show("Service cancelled successfully");
            });
            var progressBtn = menuBar.addItem("Track Progress", "How the progress of service", e -> {
                showProgressDialog(dataProvider.serviceId());
            });

            var jobAssigned = dataProvider.statusValue().equalsIgnoreCase("new");
            cancelBtn.setVisible(jobAssigned);

            return menuBar;
        });
    }

    private Renderer<ServicesDataProvider.BookedServicesRecord> serviceDetailsComponent() {
        return new ComponentRenderer<>(dataProvider -> {
            VerticalLayout layout = new VerticalLayout();
            var header = new H3("Service Details");
            layout.addClassNames("customer-service-request-details-box");

            TextArea complaintsBox = new TextArea("Your initial statement", dataProvider.desc(), "...");
            complaintsBox.getStyle().setFontSize("medium");
            complaintsBox.setRequired(true);
            complaintsBox.setReadOnly(true);

            var serviceNo = new H4("Service No: " + dataProvider.serviceNo());
            var mechanicLabel = new H4("Assigned Mechanic: " + (dataProvider.mechanic() == null ? "Unassigned" : dataProvider.mechanic()));

            var spanLabel = new Span("Reason for cancellation");
            var cancelledText = new Paragraph(dataProvider.terminationNote() == null ? "." : dataProvider.terminationNote());
            var cancelledNote = new Div(spanLabel, cancelledText);
            cancelledNote.addClassNames("customer-service-request-description-box");

            cancelledNote.setVisible(!(dataProvider.terminationNote() == null));
            layout.add(header, serviceNo, mechanicLabel, complaintsBox, cancelledNote);
            return layout;
        });
    }

    private void bookServiceDialog() {
        //primary component items...
        Button bookServiceButton = new Button("Submit Booking");
        bookServiceButton.addClassNames("default-button-style", "customer-book-service-button");
        bookServiceButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        H3 primaryTitle = new H3("Service Request Information");
        //load vehicles selector
        var carBrands = loadVehicles().stream().map(CarDataProvider::getBrand).toList();
        vehicleSelector.setItems(carBrands);

        H3 appointmentLabel = new H3("Appointment Section");
        FormLayout appointmentLayout = new FormLayout();
        FormLayout.ResponsiveStep oneCol = new FormLayout.ResponsiveStep("0", 1);
        FormLayout.ResponsiveStep threeCol = new FormLayout.ResponsiveStep("480px", 3);
        appointmentLayout.setResponsiveSteps(oneCol, threeCol);
        appointmentLayout.add(appointmentLabel, 3);
        appointmentLayout.add(pickupDateSelector, urgencySelector, priorityCheckbox);

        VerticalLayout primaryLayout = new VerticalLayout(primaryTitle,
                serviceSelector, costField, vehicleSelector,
                problemDescriptionField, appointmentLayout, bookServiceButton);
        primaryLayout.addClassNames("book-service-layout");
        primaryLayout.setSpacing(false);

        //Add value change listeners...

        AtomicInteger serviceId = new AtomicInteger(0);
        AtomicInteger vehicleId = new AtomicInteger();
        var serviceTypes = DAO_MODEL.getServiceTypeByShopId(SHOP_ID);

        serviceSelector.addValueChangeListener(item -> {
            serviceTypes.stream().filter(ex -> ex.name().equalsIgnoreCase(item.getValue()))
                    .findFirst().ifPresent(ex -> {
                        serviceId.set(ex.recordId());
                        costField.setValue(ex.cost());
                        serviceSelector.setHelperText("Service Id: " + ex.recordId());
                    });
        });

        vehicleSelector.addValueChangeListener(event -> {
            loadVehicles().stream()
                    .filter(filter -> event.getValue().equalsIgnoreCase(filter.getBrand()))
                    .findFirst().ifPresent(item -> {
                        vehicleId.set(item.getRecordId());
                        vehicleSelector.setHelperText(String.format("Id: %s, Plate Number: %s", item.getRecordId(), item.getPlateNumber()));
                    });
        });

        //secondary component items...
        Image icon = new Image(LineAwesomeIconUrl.CHECK_CIRCLE_SOLID, "CONFIRMED");
        icon.addClassName("book-service-confirm-icon");

        H3 title = new H3("Booking Confirmed!");
        title.addClassName("book-service-confirm-title");
        Paragraph subTitle = new Paragraph("Nice! you have successfully booked your service");

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
        dialog.setCloseOnOutsideClick(false);
        dialog.open();

        bookServiceButton.addSingleClickListener(event -> {
            CustomDialog customDialog = new CustomDialog();
            var saveDialog = customDialog.showSaveDialog("Book Service", MessageLoaders.confirmationMessage("book this service"));
            saveDialog.addConfirmListener(click -> {
                String name = SessionManager.getAttribute("fullName").toString();
                String logMessage = String.format("Customer %s has booked a new service for service type '%s' and preferred date as %s", name, serviceSelector.getValue(), pickupDateSelector.getValue());

                var requestProvider = new ServicesDataProvider.ServiceRequestRecord(
                        CUSTOMER_ID, vehicleId.get(), serviceId.get(), costField.getValue(),
                        problemDescriptionField.getValue(), Date.valueOf(pickupDateSelector.getValue()), urgencySelector.getValue(),
                        priorityCheckbox.getValue(), "new", USER_ID
                );

                int bookStatus = CUSTOMER_MODEL.bookServiceRequest(requestProvider);
                if (bookStatus > 0) {
                    NotificationRecords logRecord = new NotificationRecords("CUSTOMER SERVICE BOOKING", logMessage, USER_ID, SHOP_ID);
                    DAO_MODEL.logNotification(logRecord);
                    dialog.remove(primaryLayout);
                    dialog.add(secondaryLayout);
                }
            });

        });

        bookAnotherButton.addSingleClickListener(event -> {
            UI.getCurrent().refreshCurrentRoute(false);
        });

    }

    private void showProgressDialog(int serviceID) {
        CustomDialog dialog = new CustomDialog();
        var parentLayout = new VerticalLayout();
        parentLayout.setWidthFull();
        parentLayout.addClassNames("job-tracker-layout");

        var jobCardData = DAO_MODEL.fetchAllActiveJobCards().stream().filter(ex -> ex.serviceId() == serviceID).toList();
        var jobId = new AtomicInteger();

        try {
            jobId.set(jobCardData.getFirst().jobId());
        }catch (NoSuchElementException e){
            Notification emptyJobTracker = new Notification("Empty Job Tracker", 3000, Notification.Position.TOP_CENTER);
            emptyJobTracker.addThemeVariants(NotificationVariant.LUMO_ERROR);
            emptyJobTracker.open();
        }

        //fetch job description details
        var jobDescriptionData = DAO_MODEL.fetchJobDescription(jobId.get());
        var purchasedItemsData = DAO_MODEL.fetchJobPurchasedItemsData(jobId.get());

        H1 emptyJobLabel = new H1("Nothing to track so far..");
        parentLayout.add(emptyJobLabel);

        var title = "JOB TRACKER";
        var subText = "Access and track the progress of work for your service";

        if (jobDescriptionData != null) {
            parentLayout.remove(emptyJobLabel);

            double progress = Double.parseDouble(jobCardData.getFirst().progressValue());
            var progressLabel = new H4("Repair Progress: " + progress + "%");
            ProgressBar progressBar = new ProgressBar(0, 100, progress);
            var jobStatus = new H4("Current Status: " + jobCardData.getFirst().jobStatusValue());

            Div progressBox = new Div(jobStatus, new Hr(), progressLabel, progressBar);
            progressBox.addClassName("progress-indicator-box");
            progressBox.setHeight("8px");
            progressBox.setWidthFull();

            Div descriptionBox = new Div();
            descriptionBox.setWidthFull();
            descriptionBox.addClassNames("job-description-box");

            for (var item : jobDescriptionData) {
                Span dateLabel = new Span(item.noteDate().toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                Paragraph jobDescription = new Paragraph(item.jobDesc());
                descriptionBox.add(dateLabel, jobDescription);
            }
            Span timeLine = new Span("Service Timeline");
            timeLine.addClassName("timeline-indicator");
            parentLayout.add(progressBox, timeLine, descriptionBox);
        }


        dialog.defaultDialogBox(parentLayout, title, subText);
    }

}//end of class...
