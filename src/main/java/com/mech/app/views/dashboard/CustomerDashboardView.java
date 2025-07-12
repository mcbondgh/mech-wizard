package com.mech.app.views.dashboard;

import com.mech.app.components.HeaderComponent;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.enums.MasterRoles;
import com.mech.app.enums.SubRoles;
import com.mech.app.views.MainLayout;
import com.mech.app.views.customers.AddCarsView;
import com.mech.app.views.customers.AddCustomerView;
import com.mech.app.views.employees.UserProfileView;
import com.mech.app.views.login.LoginView;
import com.mech.app.views.reports.CustomerReportsView;
import com.mech.app.views.servicerequests.CustomerServiceView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.format.FormatterRegistrar;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("My Dashboard")
@Route(value = "/customer-dashboard")
@Menu(title = "My Dashboard", icon = LineAwesomeIconUrl.CHART_PIE_SOLID)
@RolesAllowed("CUSTOMER")
public class CustomerDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private static AtomicReference<String> ACCESS_TYPE;

    public CustomerDashboardView() {
        addClassNames("page-body", "fade-in");
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            var allowedRole = List.of(SubRoles.values()).toString().toLowerCase();
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
        super.onAttach(event);
        add(bodyContent());
    }


    //PAGE BODY CONTAINER...
    private Component headerSection() {
        Paragraph label = new Paragraph("Welcome back,");
        Span username = new Span(SessionManager.getAttribute("username").toString());
        H3 clientNameValue = new H3(SessionManager.getAttribute("fullName").toString());

        HorizontalLayout header = new HorizontalLayout(label, clientNameValue);
        header.getStyle().setTextAlign(Style.TextAlign.CENTER);
        header.setWidthFull();
        header.addClassNames("dashboard-name-section");

        Div parentLayout = new Div(header, username);
        parentLayout.setWidthFull();
        parentLayout.addClassNames("dashboard-header-section");
        return parentLayout;
    }

    private Component summarySection() {
        //call and store data from the database model...
        int CLIENT_ID = Integer.parseInt(SessionManager.getAttribute("clientId").toString());
        var dataSource = new DAO().fetchCustomerDashboardValues(CLIENT_ID);

        var totalServiceCard = summaryCards(VaadinIcon.BOOK.create(), dataSource.get("totalService"), "Total Services");
        var completedCard = summaryCards(VaadinIcon.FLAG_CHECKERED.create(), dataSource.get("completed"), "Completed Services");
        var queuedServicesCard = summaryCards(VaadinIcon.LIST.create(), dataSource.get("queued"), "Queued Services");
        var accountCard = summaryCards(VaadinIcon.DOLLAR.create(),  dataSource.get("account"), "Account Balance");
        accountCard.addClassName("dashboard-account-card");

        H4 summaryLabel = new H4("Your System Summary");
        FormLayout layout = new FormLayout();
        layout.addClassNames("dashboard-summary-form-layout");
        var oneCol = new FormLayout.ResponsiveStep("0", 1);
        var fourCol = new FormLayout.ResponsiveStep("768px", 4);
        layout.setResponsiveSteps(oneCol, fourCol);

        layout.add(summaryLabel, 4);
        layout.add(totalServiceCard, completedCard, queuedServicesCard, accountCard);

        return layout;
    }

    private Component bodyContent() {
        VerticalLayout parentLayout = new VerticalLayout();
        parentLayout.addClassNames("dashboard-body-content");
        parentLayout.setSizeFull();
        parentLayout.setBoxSizing(BoxSizing.BORDER_BOX);

        Icon icon = VaadinIcon.DASHBOARD.create();
        H2 viewTitle = new H2("My Dashboard");
        FlexLayout divOne = new FlexLayout(icon, new Paragraph(), viewTitle);

        var signoutButton = new Button("Sign Out", VaadinIcon.SIGN_OUT.create(), e -> {
            SessionManager.destroySession();
            UI.getCurrent().getPage().setLocation("/");
        });

        signoutButton.addClassNames("default-button-style");

        var viewTitleLayout = new HeaderComponent().dashboardHeader(divOne, signoutButton);
        viewTitleLayout.addClassNames("dashboard-view-title-layout");
        parentLayout.add(viewTitleLayout, headerSection(), summarySection(), menuSection());
        return parentLayout;
    }


    //CUSTOMER MENU AND MINI ANALYSIS BOX.
    private Component menuSection() {
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.addClassNames(".grid-cols-4", "dashboard-menu-box");
        UI ui = UI.getCurrent();

        var serviceMenu = menuCards(VaadinIcon.BOOK.create(), "Book a Service", "Click me to book a new service for your vehicle");
        serviceMenu.addClassName("dashboard-service-menu");
        serviceMenu.getElement().addEventListener("click", e -> {
           ui.navigate(CustomerServiceView.class);
        });

        var historyMenu = menuCards(VaadinIcon.BOOKMARK_O.create(), "Service History", "Click me all your completed service history here");
        historyMenu.addClassName("dashboard-history-menu");
        historyMenu.getElement().addEventListener("click",e -> {
            ui.navigate(CustomerReportsView.class);
        });

        var vehiclesMenu = menuCards(VaadinIcon.CAR.create(), "Manage Vehicles", "Click me to view and manage your fleet of cars");
        vehiclesMenu.addClassName("dashboard-active-menu");
        vehiclesMenu.getElement().addEventListener("click", e -> {
           ui.navigate(AddCarsView.class);
        });

        var profileMenu = menuCards(VaadinIcon.USER.create(), "My Profile", "Click me to see or update your profile details");
        profileMenu.addClassName("dashboard-profile-menu");
        profileMenu.getElement().addEventListener("click", e -> {
           ui.navigate(UserProfileView.class);
        });

        menuLayout.add(serviceMenu, historyMenu, vehiclesMenu, profileMenu);
        return menuLayout;
    }

    //COMPONENTS DESIGN
    private Component menuCards(Icon icon, String title, String description) {
        H3 menuTitle = new H3(title);
        Paragraph menuDescription = new Paragraph(description);
        Div parentBox = new Div(icon, menuTitle, menuDescription);
        parentBox.addClassNames("dashboard-menu-card");
        parentBox.setWidthFull();
        return parentBox;
    }

    private Component summaryCards(Icon icon, String title, String description) {
        H3 menuTitle = new H3(title);
        Paragraph menuDescription = new Paragraph(description);
        Div parentBox = new Div(icon, menuTitle, menuDescription);
        parentBox.addClassNames("dashboard-summary-card");
        parentBox.setWidthFull();
        return parentBox;
    }



}// end of class...
