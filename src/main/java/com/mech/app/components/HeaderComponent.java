package com.mech.app.components;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class HeaderComponent {

    public HorizontalLayout textHeader(String title, String subtitle) {
        H4 titleH4 = new H4(title);
        Paragraph subtitleP = new Paragraph(subtitle);
        HorizontalLayout layout = new HorizontalLayout(titleH4, subtitleP);
        layout.setWidthFull();
        layout.addClassNames("page-header-container", "text-header-container");
        return layout;
    }
}
