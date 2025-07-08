package com.mech.app.views.dashboard;

import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Dashboard")
@Route(value = "/dashboard", layout = MainLayout.class)
@PermitAll
//@Menu(order = 0, icon = LineAwesomeIconUrl.DESKTOP_SOLID)
public class DashboardView extends Composite<VerticalLayout> implements BeforeEnterObserver{

    public DashboardView() {
        getContent().addClassName("page-body");
        getContent().setWidth("100%");
        getContent().setSizeFull();
        getContent().getStyle().set("flex-grow", "1");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // This method can be used to perform actions before entering the view, like checking permissions
        // or initializing data.
        // For now, we will just set the title of the page.

    }
    @Override
    public void onAttach(AttachEvent event) {
        // This method can be used to perform actions after navigation, like updating the view title.
        getContent().add(pageBody(), footerSection());
    }

    /*******************************************************************************************************************
     COMPONENT SECTION
     *******************************************************************************************************************/
    private Component cardComponent(String title, String value, VaadinIcon icon) {
        Section section = new Section();
        section.addClassName("dashboard-card-section");
        section.setWidthFull();

        Paragraph titleParagraph = new Paragraph(title);
        titleParagraph.addClassName("dashboard-card-title");

        H3 valueHeading = new H3(value);
        valueHeading.addClassName("dashboard-card-value");

        var iconComponent = icon.create();
        iconComponent.addClassName("dashboard-card-icon");

        var horizontalLayout = new HorizontalLayout(valueHeading, iconComponent);
        horizontalLayout.addClassName("dashboard-card-content");
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        section.add(titleParagraph, horizontalLayout);
        return section;
    }

    private Component recentServicesAndJobCardsSection() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.addClassName("dashboard-recent-services");

        //SERVICES GRID SECTION
        H3 title = new H3("Recent Services");
        VerticalLayout servicesGrid = new VerticalLayout(title);
        servicesGrid.addClassNames("dashboard-grid-box", "dashboard-service-section");


        //JOB CARD GRID SECTION
        H3 jobTitle = new H3("Recent Jobs");
        VerticalLayout jobsGrid = new VerticalLayout(jobTitle);
        jobsGrid.addClassNames("dashboard-grid-box", "dashboard-jobcard-section");

        layout.add(servicesGrid, jobsGrid);
        return layout;
    }

    //    PAGE BODY SECTION
    private Component pageBody() {
        VerticalLayout bodyLayout = new VerticalLayout();
        bodyLayout.setWidthFull();
        bodyLayout.setPadding(true);
        bodyLayout.addClassName("dashboard-body");
        bodyLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        bodyLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Example cards
        var customersCard = cardComponent("Total Customers", "1,234", VaadinIcon.USERS);
        var serviceCard = cardComponent("Service Requests", "567", VaadinIcon.CHECK_CIRCLE);
        var jobsCard = cardComponent("Active Jobs", "89", VaadinIcon.TASKS);
        var assignedCard = cardComponent("Assigned Tasks", "123", VaadinIcon.SCREWDRIVER);

        var cardsLayout = new HorizontalLayout(
                customersCard, serviceCard, jobsCard, assignedCard);
        cardsLayout.addClassName("dashboard-cards-layout");

        bodyLayout.add(cardsLayout, recentServicesAndJobCardsSection());
        return bodyLayout;
    }

    // FOOTER SECTION
    private Component footerSection() {
        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.addClassName("dashboard-footer");
        footerLayout.setWidthFull();
        footerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        var paragraph = new Paragraph("© 2023 Mech App. All rights reserved.");
        paragraph.addClassName("footer-text");
        paragraph.getStyle().set("color", "#888888").set("font-size", "14px");
        paragraph.setWidthFull();

        footerLayout.add(paragraph);
        return footerLayout;
    }

}//END OF CLASS...
