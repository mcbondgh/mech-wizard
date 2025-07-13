package com.mech.app.views.feedback;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.mech.app.components.HeaderComponent;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.customers.CustomerFeedbackRecords;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.enums.MasterRoles;
import com.mech.app.views.MainLayout;
import com.mech.app.views.customers.FeedbackCardComponent;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Feedbacks")
@Route(value = "view/feedbacks", layout = MainLayout.class)
public class FeedbackView extends VerticalLayout implements BeforeEnterObserver {

    private AtomicInteger USER_ID, SHOP_ID;
    private AtomicReference<String> ACCESS_TYPE;

    public FeedbackView() {
        addClassNames("page-body", "fade-in");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            USER_ID = new AtomicInteger(Integer.parseInt(SessionManager.getAttribute("userId").toString()));
            SHOP_ID = new AtomicInteger(Integer.parseInt(SessionManager.getAttribute("shopId").toString()));
            var allowedRole = List.of(MasterRoles.values()).toString().toLowerCase();
            ACCESS_TYPE = new AtomicReference<>(SessionManager.getAttribute("role").toString());
            if (!allowedRole.contains(ACCESS_TYPE.get().toLowerCase())) {
                event.rerouteTo(LoginView.class);
            }

        } catch (NullPointerException ex) {
            event.rerouteTo(LoginView.class);
        }
    }

    @Override
    public void onAttach(AttachEvent attachEvent) {
        add(createHeaderContent(), createPageContent());
    }

    //REFERENCE METHODS

    private List<CustomerFeedbackRecords> feedbackData() {
        return new DAO().fetchCustomerFeedbacks();
    }


    /********************************************************************************************************************
     header regions
     ********************************************************************************************************************/
    private Component createHeaderContent() {
        return new HeaderComponent().textHeader(" Customer Feedbacks", " View and respond to customer feedback and reviews");
    }

    private Component createPageContent() {
        VerticalLayout content = new VerticalLayout();
        content.addClassName("page-content");
        content.setWidthFull();

        TextField searchField = new TextField("", "Search for feedbacks by customer name or review");
        var filterBox = new HeaderComponent().searchFieldComponent(searchField);
        content.add(filterBox);

        FormLayout formLayout = new FormLayout();
//        FormLayout.ResponsiveStep oneCol = new FormLayout.ResponsiveStep("0", 1);
//        FormLayout.ResponsiveStep fourCol = new FormLayout.ResponsiveStep("768px", 4);
//        formLayout.setResponsiveSteps(oneCol, fourCol);
        formLayout.setClassName("feedback-form-layout");

        feedbackData().forEach(feedback -> {
            var feedbackCard = new FeedbackCardComponent(feedback.rateValue(), feedback.jobNoAndDate(), feedback.name(), feedback.vehicleAndVin(), feedback.review());
            formLayout.add(feedbackCard.createFeedbackCard());
        });

        //add value change listener to the search field
        searchField.addValueChangeListener(e -> {
            formLayout.removeAll();
            feedbackData().forEach(feedback -> {
                boolean matchesName = feedback.name().contains(searchField.getValue().toLowerCase());
                boolean matchesJobNo = feedback.stars().toLowerCase().contains(searchField.getValue().toLowerCase());
                boolean matchesStars = feedback.jobNo().toLowerCase().contains(searchField.getValue().toLowerCase());
                if (matchesName || matchesJobNo || matchesStars) {
                    var feedbackCard = new FeedbackCardComponent(feedback.rateValue(), feedback.jobNoAndDate(), feedback.name(), feedback.vehicleAndVin(), feedback.review());
                    formLayout.add(feedbackCard.createFeedbackCard());
                }
            });
        });

        content.add(formLayout);
        return content;
    }


}//end of class...