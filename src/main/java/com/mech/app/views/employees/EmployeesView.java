package com.mech.app.views.employees;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Employees")
@Route("employees")
@Menu(order = 6, icon = LineAwesomeIconUrl.USER_PLUS_SOLID)
public class EmployeesView extends Composite<VerticalLayout> {

    public EmployeesView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
    }
}
