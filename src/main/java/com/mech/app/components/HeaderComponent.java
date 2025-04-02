package com.mech.app.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Endpoint;

public class HeaderComponent {

    public HorizontalLayout textHeader(String title, String subtitle) {
        H4 titleH4 = new H4(title);
        Paragraph subtitleP = new Paragraph(subtitle);
        HorizontalLayout layout = new HorizontalLayout(titleH4, subtitleP);
        layout.setWidthFull();
        layout.addClassNames("page-header-container", "text-header-container");
        return layout;
    }

    public FlexLayout pageHeaderWithComponent(String title, String subtitle, Component component) {
        H3 titleH4 = new H3(title);
        Paragraph subtitleP = new Paragraph(subtitle);

        var section = new Section(titleH4, subtitleP);
        section.addClassNames("header-with-button-section");

        var layout = new FlexLayout(section, component);
        layout.setWidthFull();
        layout.setAlignContent(FlexLayout.ContentAlignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.addClassNames("page-header-with-button-container");
        return layout;
    }

    public FlexLayout searchFieldComponent(TextField field) {
        var fieldLayout = new FlexLayout();
        fieldLayout.setWidthFull();
        fieldLayout.addClassNames("search-field-component-box");
        fieldLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        fieldLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        field.setSizeUndefined();
        field.setPrefixComponent(VaadinIcon.SEARCH.create());
        field.getStyle().setPadding("14px");
        field.setClearButtonVisible(true);

        fieldLayout.add(field);
        return fieldLayout;
    }

}
