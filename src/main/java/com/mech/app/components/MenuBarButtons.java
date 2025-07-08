package com.mech.app.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.lineawesome.LineAwesomeIcon;

public class MenuBarButtons{

    private static HorizontalLayout layout;
    public MenuBarButtons(String label, LineAwesomeIcon icon) {
        Span span = new Span(label);
        span.addClassName("menuitem-label");
        span.getStyle().setFontSize("small");

        var i = icon.create();
        i.getStyle().setFontSize("small");
        i.setClassName("menuitem-icon");

        layout = new HorizontalLayout(i, span);
        layout.addClassNames("menuitem-layout");
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
    }
    public MenuBarButtons(String label) {
        Span span = new Span(label);
        span.addClassName("menuitem-label");
        span.getStyle().setFontSize("small");

        layout = new HorizontalLayout(span);
        layout.addClassNames("menuitem-layout");
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
    }

    public HorizontalLayout createMenuButton() {
        return layout;
    }
}//end of class...
