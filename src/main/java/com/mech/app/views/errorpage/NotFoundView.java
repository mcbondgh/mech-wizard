package com.mech.app.views.errorpage;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@Route("error-404")
@PageTitle("Page Not Found")
public class NotFoundView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    public NotFoundView() {
        addClassName("page-not-found-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Create background floating circles
        Div background = createBackgroundCircles();
        add(background);

        // Create main content container
        Div container = new Div();
        container.addClassName("container");

        // Error code
        H1 errorCode = new H1("404");
        errorCode.addClassName("error-code");

        // Error message
        H2 errorMessage = new H2("Page Not Found");
        errorMessage.addClassName("error-message");

        // Error description
        Paragraph errorDescription = new Paragraph(
                "Oops! It seems you've ventured into uncharted territory. " +
                        "The page you're looking for might have moved or doesn't exist."
        );
        errorDescription.addClassName("error-description");

        // Home button
        Button homeButton = new Button("Return to Homepage");
        homeButton.addClassName("home-button");
        homeButton.addClickListener(e -> UI.getCurrent().navigate(""));

        // Lost traveler icon
        Div lostTraveler = new Div();
        lostTraveler.addClassName("lost-traveler");
        Icon lockIcon = VaadinIcon.LOCK.create();
        lockIcon.setSize("150px");
        lockIcon.setColor("#6c63ff");
        lostTraveler.add(lockIcon);

        // Add all components to container
        container.add(errorCode, errorMessage, errorDescription, homeButton, lostTraveler);

        // Add container to layout
        add(container);
    }

    private Div createBackgroundCircles() {
        Div background = new Div();
        background.addClassName("background");

        // Create 4 floating circles
        for (int i = 0; i < 4; i++) {
            Div circle = new Div();
            circle.addClassName("circle");
            circle.addClassName("circle-" + (i + 1));
            background.add(circle);
        }

        return background;
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<NotFoundException> errorParameter) {
        beforeEnterEvent.forwardTo(NotFoundView.class);
        return 404;
    }
}
