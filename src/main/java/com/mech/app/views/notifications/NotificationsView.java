package com.mech.app.views.notifications;

import com.mech.app.components.HeaderComponent;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Notifications")
@Route(value = "notifications", layout = MainLayout.class)
@Menu(order = 1, icon = LineAwesomeIconUrl.BELL)
@JsModule("./js/scripts.js")
public class NotificationsView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private static DAO DAO_MODEL;

    public NotificationsView() {
        getContent().setClassName("page-body");
        DAO_MODEL = new DAO();

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }
    @Override
    public void onAttach(AttachEvent event) {
        getContent().add(pageBody());
    }

    public VerticalLayout pageBody() {
        var header = new HeaderComponent().textHeader("Notifications", "Your top 15 Latest Notifications");

        VerticalLayout layout = new VerticalLayout(header, contentLayout());
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        layout.setPadding(true);
        layout.setSpacing(true);

        layout.setSizeFull();
        return layout;
    }

    public Component contentLayout() {
        var listItems = DAO_MODEL.fetchSystemNotificationLogs(1);
        var icon = LineAwesomeIconUrl.ENVELOPE_OPEN_SOLID;
        List<MessageListItem> listArray = new ArrayList<>();
        listItems.forEach(data -> {
            MessageListItem items = new MessageListItem();
            items.addClassNames("notification-message-list");
            items.setUserImage(icon);
            items.setTime(data.formatDate());
            items.setText(data.content());
            items.setUserName(data.title());
            listArray.add(items);
        });
        MessageList messageList = new MessageList(listArray);
        messageList.setClassName("notification-message-list");
        messageList.setWidthFull();
        return messageList;
    }

}//end of class...
