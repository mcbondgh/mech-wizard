package com.mech.app.views.employees;

import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("User profile")
@Route(value = "/user-profile", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "CUSTOMER", "MECHANIC"})
public class UserProfileView extends VerticalLayout implements BeforeEnterObserver {

    public UserProfileView() {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    public void onAttach(AttachEvent event) {

    }


}//end of class.