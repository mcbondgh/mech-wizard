package com.mech.app.views.dashboard;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Dashboard")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.CHALKBOARD_SOLID)
public class DashboardView extends Composite<VerticalLayout> {

    public DashboardView() {
        VerticalLayout layoutColumn2 = new VerticalLayout();
        TextField textField = new TextField();
        Button buttonPrimary = new Button();
        RouterLink routerLink = new RouterLink();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Paragraph textSmall = new Paragraph();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        textField.setLabel("Text field");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.START, textField);
        textField.setWidth("min-content");
        buttonPrimary.setText("Button");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        routerLink.setText("Custom View");
        routerLink.setRoute(DashboardView.class);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        textSmall.setText("(c) Copyright 2025 - Mc's republic");
        layoutRow.setAlignSelf(FlexComponent.Alignment.CENTER, textSmall);
        textSmall.getStyle().set("flex-grow", "1");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        getContent().add(layoutColumn2);
        layoutColumn2.add(textField);
        layoutColumn2.add(buttonPrimary);
        layoutColumn2.add(routerLink);
        getContent().add(layoutRow);
        layoutRow.add(textSmall);
    }
}
