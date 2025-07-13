package com.mech.app.views.customers;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FeedbackCardComponent {
    H4 starsComponent;
    Span serviceInfoComponent;
    H4 customerNameComponent;
    Span vehicleInfoComponent;
    Span feedbackComponent;

    public FeedbackCardComponent(String stars, String serviceInfo, String customerName, String vehicleInfo, String feedback) {
        starsComponent = new H4(stars);
        serviceInfoComponent = new Span(serviceInfo);
        customerNameComponent = new H4(customerName);
        vehicleInfoComponent = new Span(vehicleInfo);
        feedbackComponent = new Span(feedback);
    }

    public VerticalLayout createFeedbackCard() {
        //create a vertical layout to hold all the components
        var parentBox = new VerticalLayout();
        parentBox.setWidthFull();
        parentBox.addClassName("feedback-card-parent");

        //section One
        var sectionOne = new FlexLayout(starsComponent, serviceInfoComponent);
        sectionOne.setWidthFull();
        sectionOne.getStyle().set("box-sizing", BoxSizing.BORDER_BOX.name());
        sectionOne.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        sectionOne.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionOne.addClassNames("feedback-card-item", "feedback-card-section-one");

        //section Two
        var sectionTwo = new VerticalLayout(customerNameComponent, vehicleInfoComponent);
        sectionTwo.setWidthFull();
        sectionTwo.getStyle().set("box-sizing", BoxSizing.BORDER_BOX.name());
        sectionTwo.addClassNames("feedback-card-item", "feedback-card-section-two");

        //section Three
        var sectionThree = new VerticalLayout(feedbackComponent);
        sectionThree.setWidthFull();
        sectionThree.getStyle().set("box-sizing", BoxSizing.BORDER_BOX.name());
        sectionThree.addClassNames("feedback-card-item", "feedback-card-section-three");

        parentBox.add(sectionOne, sectionTwo, sectionThree);
        return parentBox;
    }

}//end of class...
