package com.mech.app.views.dashboard;

import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("My Dashboard")
@Route(value = "/customer-dashboard", layout = MainLayout.class)
@Menu(title = "My Dashboard", icon = LineAwesomeIconUrl.CHART_PIE_SOLID)
@PermitAll
public class CustomerDashboardView extends VerticalLayout {

    public CustomerDashboardView() {
        addClassName("page-body");
        setSizeFull();
    }



}// end of class...
