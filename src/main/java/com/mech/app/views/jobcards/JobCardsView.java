package com.mech.app.views.jobcards;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Job Cards")
@Route("job-cards")
@Menu(order = 3, icon = LineAwesomeIconUrl.ADDRESS_CARD)
public class JobCardsView extends Composite<VerticalLayout> {

    public JobCardsView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        H3 h3 = new H3();
        Button buttonPrimary = new Button();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.addClassName(Padding.SMALL);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        layoutRow.setAlignItems(Alignment.CENTER);
        layoutRow.setJustifyContentMode(JustifyContentMode.START);
        h3.setText("Manage repair and maintenance jobs\nOnline");
        h3.setWidth("max-content");
        buttonPrimary.setText("Add Job Card");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        getContent().add(layoutRow);
        layoutRow.add(h3);
        layoutRow.add(buttonPrimary);
        getContent().add(layoutColumn2);
    }
}
