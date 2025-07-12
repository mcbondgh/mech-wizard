package com.mech.app.views.home;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.communication.PushConnection;

import jakarta.annotation.security.PermitAll;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sound.sampled.Line;

@PageTitle("Homepage")
@Route("/")
@RouteAlias("/homepage")
@PermitAll
public class Homepage extends VerticalLayout {
    public Homepage() {
        addClassNames("home-page-body", "fade-in");
        setWidthFull();
        setBoxSizing(BoxSizing.BORDER_BOX);
    }

    @Override
    public void onAttach(AttachEvent event) {
        super.onAttach(event);
        add(heroSection(), servicesSection(), contactSection());
    }

    /*******************************************************************************************************************
     * COMPONENT SECTIONS
     *******************************************************************************************************************/

     private Component heroSection() {
        VerticalLayout parentLayout = new VerticalLayout();
        parentLayout.addClassNames("landing-hero-section");
        parentLayout.setSizeUndefined();
        

        H1 heroText = new H1("AUTO MECHANIC SHOP");
        H3 subText2 = new H3("Aliboat Motors");
        var subText = new Paragraph("Professional automotive service center providing" +
                " comprehensive vehicle maintenance and repair solutions");

        Div textSection = new Div(heroText, subText2, subText);
        textSection.addClassNames("landing-text-div");

        Image heroImage = new Image("images/landing-page-img.jpg", "HERO IMG");
        heroImage.addClassName("landing-hero-image");

        var imageAndLoginDiv = new HorizontalLayout(loginSection(), heroImage);
        imageAndLoginDiv.addClassNames("landing-image-and-login-div");

        // parentLayout.add(textSection, 2);
        parentLayout.add(textSection, imageAndLoginDiv);
        return parentLayout;
    }

    private Component loginSection() {
        VerticalLayout parentLayout = new VerticalLayout();
        parentLayout.addClassNames("landing-page-parent-layout", "fade-in.delay-1");
        parentLayout.setWidthFull();

        Button clientButton = new Button("Client Portal", e -> {
            UI.getCurrent().navigate("/login?access=client-portal");
        });
        clientButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        clientButton.addClassNames("client-button");

        Button adminButton = new Button("Technician Portal", e -> {
            UI.getCurrent().navigate("/login?access=admin-portal");
        });

        adminButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        adminButton.addClassNames("admin-button");

        Div clientDiv = heroButtonsDiv(clientButton, "Access Client Portal",
                "Access your service history, track requests, and schedule appointments", LineAwesomeIcon.USER);
        Div adminDiv = heroButtonsDiv(adminButton, "Access Technician Portal",
                "Manage operations, process orders, and oversee service delivery", LineAwesomeIcon.USER_COG_SOLID);
        parentLayout.add(clientDiv, adminDiv);
        return parentLayout;
    }

    @NotNull
    private static Div heroButtonsDiv(Button button, String title, String subTitle, LineAwesomeIcon icon) {
        Span clientText = new Span(subTitle);
        var svgIcon = icon.create();
        svgIcon.addClassName("landing-buttons-icon");
        var titleSpan = new H4(title);
        var titleDiv = new Div(titleSpan, clientText);
        titleDiv.addClassName("landing-login-client-title-div");
        Div clientDiv = new Div(svgIcon, titleDiv, button);

        Div buttonsDiv = new Div(clientDiv);
        buttonsDiv.addClassNames("landing-buttons-div");
        return buttonsDiv;
    }

    // SERVICES SECTION
    FormLayout.ResponsiveStep colOne = new FormLayout.ResponsiveStep("0", 1);
    FormLayout.ResponsiveStep colThree = new FormLayout.ResponsiveStep("768px", 3);

    private Component servicesSection() {
        FormLayout parentLayout = new FormLayout();
        parentLayout.setResponsiveSteps(colOne, colThree);
        parentLayout.addClassNames("landing-page-parent-layout", "landing-services-layout", "fade-in.delay-2");

        H3 titleText = new H3("Our Services");

        String diagString = "Advanced computer diagnostics using state-of-the-art technology for accurate engine analysis";
        String mainText = "Comprehensive maintenance services including oil changes, brake service, and scheduled inspections";
        String expertText = "Professional automotive consultation and personalized service recommendations";

        var diagnosisCard = servicesCard("icons/icons8-maintenance-100.png", "Engine Diagnostics", diagString);
        var mainCard = servicesCard("icons/icons8-car-service-100.png", "Routine Maintenance", mainText);
        var expertCard = servicesCard("icons/icons8-wrench-100.png", "Expert Consultation", expertText);

        parentLayout.add(titleText, 3);
        parentLayout.add(diagnosisCard, mainCard, expertCard);
        return parentLayout;
    }

    private Component servicesCard(String imageSrc, String title, String subTitle) {
        Image imageOne = new Image(imageSrc, "IMG");
        H4 headerOne = new H4(title);
        headerOne.getStyle().setTextAlign(Style.TextAlign.CENTER);
        Span subTextOne = new Span(subTitle);
        subTextOne.getStyle().setTextAlign(Style.TextAlign.CENTER);
        Div content = new Div(imageOne, headerOne, subTextOne);
        content.getStyle().setAlignItems(Style.AlignItems.CENTER);

        content.addClassNames("landing-services-card");
        return content;
    }

    // CONTACT SECTION
    private Component contactSection() {
        FormLayout parentLayout = new FormLayout();
        parentLayout.setResponsiveSteps(colOne, colThree);
        parentLayout.addClassNames("landing-page-parent-layout", "landing-contact-layout", "fade-in.delay-3");

        SvgIcon locationIcon = new SvgIcon(LineAwesomeIconUrl.SEARCH_LOCATION_SOLID);
        SvgIcon contactIcon = new SvgIcon(LineAwesomeIconUrl.ADDRESS_BOOK);
        SvgIcon timeIcon = new SvgIcon(LineAwesomeIconUrl.CALENDAR_TIMES);

        String locationInfo = """
                123 Auto Street\n
                Suite 100\n
                Mechanic City, MC 12345""";
        var locationCard = servicesCard(locationIcon.getSrc(), "Our Location", locationInfo);
        String contactInfo = """
                +233 0000000\n
                 +233 084348384\n
                aliboarmotors@email.com""";
        var contactCard = servicesCard(contactIcon.getSrc(), "Call Or Email On", contactInfo);

        var timeInfo = "Weekdays \n 8:00am - 6:00pm \n Saturdays \n 10am - 5:00pm";
        var timeCard = servicesCard(timeIcon.getSrc(), "Our Time", timeInfo);

        H3 titleText = new H3("Our Contact Information");
        Span subText = new Span("Get in touch with our expert automotive service team");

        Div headerDiv = new Div(titleText, subText);
        headerDiv.addClassNames("landing-contact-div");
        parentLayout.add(headerDiv, 3);

        parentLayout.add(contactCard, locationCard, timeCard);

        return parentLayout;
    }


}//end of class
