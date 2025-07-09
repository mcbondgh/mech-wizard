package com.mech.app.views.reports;

import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.enums.SubRoles;
import com.mech.app.views.MainLayout;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "my-report", layout = MainLayout.class)
@PageTitle("My Reports")
@RolesAllowed("CUSTOMER")
public class CustomerReportsView extends VerticalLayout implements BeforeEnterObserver {
    private int USER_ID;
    private int SHOP_ID;
    private static AtomicReference<String> ACCESS_TYPE;
    public CustomerReportsView() {
        setPadding(true);
        setSpacing(true);
        addClassName("page-body");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            var allowedRole = List.of(SubRoles.values()).toString().toLowerCase();
            ACCESS_TYPE = new AtomicReference<>(SessionManager.getAttribute("role").toString());
            System.out.println("Notification view: "+ allowedRole.toLowerCase().contains(ACCESS_TYPE.get().toLowerCase()));
            if (!allowedRole.contains(ACCESS_TYPE.get().toLowerCase())) {
                event.forwardTo("/login");
            }
        }catch (NullPointerException ex) {
            event.rerouteTo(LoginView.class);
        }
    }
}//END OF CLASS..
