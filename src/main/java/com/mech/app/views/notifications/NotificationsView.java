package com.mech.app.views.notifications;

import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Notifications")
@Route(value = "notifications", layout = MainLayout.class)
@Menu(order = 1, icon = LineAwesomeIconUrl.BELL)
@JsModule("./js/scripts.js")
public class NotificationsView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    public NotificationsView() {
        getContent().setClassName("page-body");

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

}//end of class...
