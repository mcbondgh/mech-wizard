package com.mech.app.views.employees;

import com.mech.app.components.UserProfileComponent;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.enums.MasterRoles;
import com.mech.app.enums.SubRoles;
import com.mech.app.views.MainLayout;
import com.mech.app.views.login.LoginView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("User profile")
@Route(value = "/user-profile", layout = MainLayout.class)
@RolesAllowed({"CUSTOMER", "MECHANIC"})
public class UserProfileView extends VerticalLayout implements BeforeEnterObserver {

    private int USER_ID, SHOP_ID;
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
        var profileComponent = new UserProfileComponent().userProfileComponent();
        add(profileComponent);
    }


}//end of class.