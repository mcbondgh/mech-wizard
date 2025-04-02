package com.mech.app.views.jobcards;

import com.mech.app.dataproviders.jobcards.JobCardDataProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.AttachNotifier;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Job Cards")
@Route("view/job-cards")
@Menu(order = 3, icon = LineAwesomeIconUrl.ADDRESS_CARD)
public class JobCardsView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private final Button jobCardButton = new Button("Add Job Card");
    private final Grid<JobCardDataProvider> jobCardGrid = new Grid<JobCardDataProvider>();

    public JobCardsView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    public void onAttach(AttachEvent event) {
        jobCardButton.setWidth("min-content");
        jobCardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
    }

    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/


    /*******************************************************************************************************************
     VIEW SECTION
     *******************************************************************************************************************/

    /*******************************************************************************************************************
     COMPONENT RENDERERS
     *******************************************************************************************************************/


}//end of class...
