package com.mech.app.views.servicerequests;

import com.mech.app.components.CustomDialog;
import com.mech.app.components.HeaderComponent;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.components.service_requests.ServiceRequestComponents;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.employees.EmployeesDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.servicesrequest.ServicesDataProvider;
import com.mech.app.enums.MasterRoles;
import com.mech.app.models.ServiceRequestModel;
import com.mech.app.views.MainLayout;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.communication.PushMode;
import jakarta.annotation.security.RolesAllowed;
import org.jetbrains.annotations.NotNull;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Service Requests")
@Route(value = "/view/service-requests", layout = MainLayout.class)
//@Menu(order = 5, icon = LineAwesomeIconUrl.TOOLBOX_SOLID)
@RolesAllowed({"MECHANIC", "ADMIN", "RECEPTIONIST"})
public class ServiceRequestsView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private static H4 layoutHeaderText;
    private static final boolean isVisible = true;
    private AtomicInteger USER_ID;
    private AtomicInteger SHOP_ID;
    private static AtomicReference<String> ACCESS_TYPE;

    public ServiceRequestsView() {
        getContent().setSizeUndefined();
        getContent().setWidthFull();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            USER_ID = new AtomicInteger(Integer.parseInt(SessionManager.getAttribute("userId").toString()));
            SHOP_ID = new AtomicInteger(Integer.parseInt(SessionManager.getAttribute("shopId").toString()));
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
        getContent().add(
                pageHeaderSection(),
                pageBody()
        );
    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/


    private List<ServicesDataProvider.BookedServicesRecord> serviceRequestSampleData() {
        return new DAO().fetchAllServiceRequestsNotDeleted(SHOP_ID.get());
    }

    /*******************************************************************************************************************
     PAGE BODY VIEW
     *******************************************************************************************************************/
    private Component pageHeaderSection() {

        layoutHeaderText = new H4("Service Request List");
        var subTitle = new Span("Manage all service requests for the shop here. You can create, update, approve or delete a service request.");
        var headerComponent = new HeaderComponent().textHeader(layoutHeaderText.getText(), subTitle.getText());

        Button addButton = new Button("Create Service", VaadinIcon.PLUS.create());
        addButton.setWidth("fit-content");
        addButton.addClassNames("default-button-style");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        addButton.addClickListener(e -> {
            serviceRequestFormDialog();
        });
        return headerComponent;
    }

    private Component pageBody() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        layout.addClassNames("service-request-body-box");
        layout.add(serviceRequestList());
        return layout;
    }


    /*******************************************************************************************************************
     COMPONENT RENDERERS
     *******************************************************************************************************************/
    private void serviceRequestFormDialog() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.addClassNames("service-request-form-layout");

        CustomDialog dialog = new CustomDialog();
        dialog.defaultDialogBox(layout, "Create New Service", "Create a new service request for a customer.");

        layoutHeaderText = new H4("Customer Information");
        Section infoSection = new Section(layoutHeaderText);
        infoSection.addClassNames("service-form-customer-info");

        //create form components
        ComboBox<CustomersDataProvider> customerSelector = new ComboBox<>("Select a Customer");
        customerSelector.addClassNames("combo-box-style", "customer-selector");

        Button saveButton = new Button("Submit Request", LineAwesomeIcon.SAVE.create());
        saveButton.addClassNames("default-btn-style");
        saveButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        saveButton.setWidth("fit-content");

        var closeButton = new Button("Close Form");
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        closeButton.addClassNames("default-btn-style", "cancel-button");
        closeButton.setWidth("fit-content");

        var buttonsContainer = new HorizontalLayout();
        buttonsContainer.setWidthFull();
        buttonsContainer.addClassName("service-request-buttons-container");
        buttonsContainer.add(saveButton, closeButton);

        var sectionOne = serviceFormInnerSection("Car Information");
        var sectionTwo = serviceFormInnerSection("Service Request Information");

        var innerLayout = new VerticalLayout(sectionOne, sectionTwo, buttonsContainer);
        innerLayout.addClassNames("service-request-inner-form-layout");
        innerLayout.setWidthFull();

        layout.add(customerSelector, innerLayout);

    }

    @NotNull
    private static Component serviceFormInnerSection(String title, Component... component) {
        var section = new Section();
        H5 innerHeader = new H5(title);
        section.addClassName("request-inner-section");
        section.setWidthFull();
        section.add(innerHeader);
        section.add(component);
        return section;
    }

    private Component serviceRequestList() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.addClassNames("service-request-list-layout");

        TextField filterField = new TextField();
        filterField.setPlaceholder("Search service by customer name, service no, type or car plates...");
        filterField.addClassNames("input-style");
        var filterSection = new HeaderComponent().searchFieldComponent(filterField);

        VirtualList<ServicesDataProvider.BookedServicesRecord> serviceList = new VirtualList<>();
        serviceList.addClassNames("service-request-list");
        serviceList.setSizeUndefined();
        serviceList.setItems(serviceRequestSampleData());
        serviceList.setRenderer(serviceRequestComponentRenderer());

        //add a value change listener to filter services based on user search parameter...
        filterField.addValueChangeListener(
                event -> {
                    UI.getCurrent().access(() -> {
                        String keyword = event.getValue().toLowerCase();
                        if (event.getSource().getValue().isEmpty()) {
                            serviceList.setItems(serviceRequestSampleData());
                            return;
                        }
                        var filterResult = serviceRequestSampleData()
                                .stream()
                                .filter(item -> {
                                    var matchesName = item.customerName().toLowerCase().contains(keyword);
                                    var matchesServiceNumber = item.serviceNo().toLowerCase().contains(keyword);
                                    var matchesServiceType = item.serviceType().toLowerCase().contains(keyword);
                                    var matchesNumber = item.VIN().toLowerCase().contains(keyword);
                                    return matchesServiceType || matchesName || matchesNumber || matchesServiceNumber;
                                });
                        serviceList.setItems(filterResult);
                    });
                });

        layout.add(filterSection, serviceList);
        return layout;
    }


    private ComponentRenderer<Div, CustomersDataProvider.CustomersRecord> customersListItems() {
        return new ComponentRenderer<>(dataProvider -> {
            Div container = new Div();
            Paragraph name = new Paragraph(dataProvider.name());
            Span span = new Span(dataProvider.email() + ", " + dataProvider.otherNumber());

            container.add(name, span);
            container.setWidthFull();
            container.getStyle().setBoxSizing(Style.BoxSizing.BORDER_BOX);
            container.addClassNames("customers-list-item-renderer-box");
            return container;
        });
    }

    //##################################################################################################
    private ComponentRenderer<Component, ServicesDataProvider.BookedServicesRecord> serviceRequestComponentRenderer() {
        return new ComponentRenderer<>(dataProvider -> {
            VerticalLayout layout = new VerticalLayout();

            H6 cancelHeader = new H6("Reason For Cancellation");
            Paragraph noteText = new Paragraph();
            Div cancellationNoteSection = new Div(cancelHeader, noteText);
            cancellationNoteSection.setWidthFull();
            cancellationNoteSection.addClassName("cancel-div");
            cancellationNoteSection.setVisible(false);

            var cancelButton = new Button("Cancel", LineAwesomeIcon.TIMES_SOLID.create());
            var updateButton = new Button("Update", LineAwesomeIcon.PENCIL_RULER_SOLID.create());
            var assignButton = new Button("Assign Mechanic", LineAwesomeIcon.CHECK_CIRCLE.create());
            var deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
            HorizontalLayout menuBar = new HorizontalLayout(assignButton, updateButton, cancelButton, deleteButton);
            menuBar.addClassNames("service-operation-buttons");

            cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            updateButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            assignButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

//          assignButton.addClassNames("button-style", "default-button-style");
            updateButton.addClassNames("button-style", "item-update-style");
            cancelButton.addClassNames("button-style");
            deleteButton.addClassNames("button-style");

            //ADD CLICK EVENT LISTENERS TO THE BUTTONS...
            assignButton.addSingleClickListener(e -> ServiceRequestComponents
                    .assignMechanicDialog(dataProvider.serviceId(), USER_ID.get(), SHOP_ID.get()));

            //update Button click event implementation...
            updateButton.addSingleClickListener(e -> ServiceRequestComponents
                    .updateServiceDialog(dataProvider, USER_ID.get(), SHOP_ID.get()));

            //delete Button click event implementation...
            deleteButton.addSingleClickListener(e -> {
                var deleteDialog = new CustomDialog().showDeleteDialog("Delete Request", MessageLoaders.confirmationMessage("to delete this service"));
                deleteDialog.addConfirmListener(event -> {
                    var dataModel = new ServiceRequestModel();
                    int responseStatus = dataModel.deleteServiceRequest(dataProvider.serviceId());
                    if (responseStatus > 0) {
                        String logMsg = String.format("Service request no %s has been deleted from your the of services cancelled", dataProvider.serviceNo());
                        NotificationRecords recordsObj = new NotificationRecords("DELETED SERVICE", logMsg, USER_ID.get(), SHOP_ID.get());
                        dataModel.logNotification(recordsObj);
                        UI.getCurrent().refreshCurrentRoute(false);
                    }
                });
            });

            //cancel button click implementation...
            cancelButton.addSingleClickListener(e ->
                    ServiceRequestComponents
                            .serviceTerminationDialog(dataProvider.serviceId(), dataProvider.serviceNo(), USER_ID.get(), SHOP_ID.get()));
//
            Span statusIndicator = new Span(dataProvider.statusValue());
            switch (dataProvider.serviceStatus()) {
                case "new" -> {
                    statusIndicator.getElement().getThemeList().add("badge primary pill small");
                    deleteButton.setVisible(false);
                }
                case "assigned" -> {
                    statusIndicator.getElement().getThemeList().add("badge warning primary pill small");
                    deleteButton.setVisible(false);
                    updateButton.setVisible(false);
                    assignButton.setVisible(false);
                }
                case "cancelled" -> {
                    statusIndicator.getElement().getThemeList().add("badge error primary pill small");
                    assignButton.setVisible(false);
                    noteText.setText(dataProvider.terminationNote());
                    cancellationNoteSection.setVisible(true);
                    updateButton.setVisible(false);
                    deleteButton.setVisible(true);
                    cancelButton.setVisible(false);
                }
//                case null, default -> {
//                    statusIndicator.getElement().getThemeList().add("badge success primary pill smaller");
//                    deleteButton.setVisible(true);
//                    cancelButton.setVisible(false);
//                    assignButton.setVisible(false);
//                    updateButton.setVisible(false);
//                }
            }

            var icon = VaadinIcon.TOOLBOX.create();
            HorizontalLayout serviceNoAndStatusBox = new HorizontalLayout(new H3(dataProvider.serviceNo()), statusIndicator);
            Section nameAndServiceDetail = new Section(serviceNoAndStatusBox, new Span(String.format("%s (%s)", dataProvider.customerName(), dataProvider.vehicle() + ", " + dataProvider.VIN())));
            Section serviceTypeAndDateBox = new Section(new H5(dataProvider.serviceType()), new Span("Reported: " + dataProvider.loggedDate().toLocalDateTime().toLocalDate().toString()));

            Section iconAndService = new Section(icon, nameAndServiceDetail);

            iconAndService.addClassNames("request-icon-and-service-box");
            serviceNoAndStatusBox.addClassNames("service-no-and-status-box");
            serviceTypeAndDateBox.addClassNames("service-type-and-date-box");
            icon.addClassNames("request-icon");
            nameAndServiceDetail.addClassNames("service-name-and-details-box");

            FlexLayout blockOne = new FlexLayout(iconAndService, serviceTypeAndDateBox);
            Div blockTwo = new Div(dataProvider.desc());
            FlexLayout blockThree = new FlexLayout(menuBar);

            blockOne.addClassNames("content-blocks", "block-on");
            blockTwo.addClassNames("content-blocks", "block-two");
            blockThree.addClassNames("content-blocks", "block-three");

            FormLayout blockFour = new FormLayout();
            blockFour.addClassNames("content-blocks", "block-four");
            FormLayout.ResponsiveStep oneCol = new FormLayout.ResponsiveStep("0", 1);
            FormLayout.ResponsiveStep twoCol = new FormLayout.ResponsiveStep("480px", 2);
            FormLayout.ResponsiveStep fourCol = new FormLayout.ResponsiveStep("768px", 4);
            blockFour.setResponsiveSteps(oneCol, twoCol, fourCol);

            var scheduleDateBox = ServiceRequestComponents.blockFourCard("Schedule Date:", dataProvider.preferredDate());
            scheduleDateBox.addComponentAsFirst(LineAwesomeIcon.CALENDAR_CHECK.create());
            var mechanicBox = ServiceRequestComponents.blockFourCard("Mechanic", dataProvider.mechanic());
            mechanicBox.addComponentAsFirst(LineAwesomeIcon.USERS_COG_SOLID.create());
            var urgencyBox = ServiceRequestComponents.blockFourCard("Urgency Level", dataProvider.urgencyLevel());
            urgencyBox.addComponentAsFirst(VaadinIcon.CHECK_CIRCLE.create());
            urgencyBox.setClassName("urgency-box");
            var pickupBox = ServiceRequestComponents.blockFourCard("Pickup/Drop-Off", dataProvider.pickupValue());
            pickupBox.addComponentAsFirst(LineAwesomeIcon.SHIELD_ALT_SOLID.create());

            blockFour.add(scheduleDateBox, mechanicBox, pickupBox, urgencyBox);

            layout.add(blockOne, blockTwo, blockFour, cancellationNoteSection, blockThree);
            layout.setWidthFull();
            layout.addClassNames("service-request-component-layout");


            return layout;
        });
    }


}//end of class...
