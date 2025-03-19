package com.mech.app.views.settings;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Settings")
@Route("settings")
@Menu(order = 8, icon = LineAwesomeIconUrl.TOOLS_SOLID)
public class SettingsView extends Composite<VerticalLayout> {

    public SettingsView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
    }
}
