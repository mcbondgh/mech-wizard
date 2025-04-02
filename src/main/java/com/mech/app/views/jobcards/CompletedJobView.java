package com.mech.app.views.jobcards;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Job Diagnosis")
@Route(value = "/view/completed-job")
@Menu(title = "Job Diagnosis", order = 4, icon = LineAwesomeIconUrl.HAMMER_SOLID)
public class CompletedJobView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    public CompletedJobView() {
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }



    @Override
    public Registration addAttachListener(ComponentEventListener<AttachEvent> listener) {

        return super.addAttachListener(listener);
    }



}//end of class...
