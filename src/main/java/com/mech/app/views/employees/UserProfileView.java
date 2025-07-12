package com.mech.app.views.employees;

import com.mech.app.components.HeaderComponent;
import com.mech.app.components.UserProfileComponent;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.enums.MasterRoles;
import com.mech.app.enums.SubRoles;
import com.mech.app.views.MainLayout;
import com.mech.app.views.dashboard.CustomerDashboardView;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("User profile")
@Route(value = "/user-profile")
@RolesAllowed({"CUSTOMER", "MECHANIC"})
public class UserProfileView extends VerticalLayout implements BeforeEnterObserver {

    private int USER_ID, SHOP_ID, CUSTOMER_ID;
    private static AtomicReference<String> ACCESS_TYPE;

    public UserProfileView() {
        addClassName("page-body");
        setSpacing(true);
        setPadding(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            var allowedRole = List.of(SubRoles.values()).toString().toLowerCase();
            USER_ID = Integer.parseInt(SessionManager.getAttribute("userId").toString());
            CUSTOMER_ID = Integer.parseInt(SessionManager.getAttribute("clientId").toString());
            SHOP_ID = Integer.parseInt(SessionManager.getAttribute("shopId").toString());
            ACCESS_TYPE = new AtomicReference<>(SessionManager.getAttribute("role").toString());
            if (!allowedRole.contains(ACCESS_TYPE.get().toLowerCase())) {
                event.forwardTo("/login");
            }
        } catch (NullPointerException ex) {
            event.rerouteTo(LoginView.class);
        }
    }

    @Override
    public void onAttach(AttachEvent event) {

        var profileComponent = new UserProfileComponent().userProfileComponent(CUSTOMER_ID, USER_ID);

        Icon icon = VaadinIcon.BOOK_PERCENT.create();
        H2 viewTitle = new H2("User Profile");
        FlexLayout divOne = new FlexLayout(icon, new Span(),viewTitle);

        var navButton = new Button("Dashboard", VaadinIcon.DASHBOARD.create(), e -> {
            UI.getCurrent().navigate(CustomerDashboardView.class);
        });
        navButton.addClassNames("default-button-style");

        var headerComponent = new HeaderComponent().dashboardHeader(divOne, navButton);
        add(headerComponent, profileComponent);
    }


}//end of class.