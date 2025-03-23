package com.mech.app.components;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import java.util.concurrent.atomic.AtomicBoolean;

public class CustomDialog {
    private ConfirmDialog dialog;
    private static Notification notification;
    private FlexLayout headerContainer;

    public CustomDialog(String title, String content){
        headerContainer = new FlexLayout(new H4(title));
        headerContainer.addClassName("confirm-dialog-header-container");
        headerContainer.setWidthFull();
        headerContainer.getStyle().setMargin("0").setPadding("10px");
        dialog = new ConfirmDialog();
        notification = new Notification();
        dialog.setHeader(headerContainer);
        dialog.setText(content);
        dialog.addConfirmListener(e -> e.getSource().close());
    }

    public boolean showSaveDialog() {
        headerContainer.addClassName("save-dialog-header-style");
        AtomicBoolean isClicked = new AtomicBoolean(false);
        dialog.addConfirmListener(event -> isClicked.set(event.isFromClient()));
        return isClicked.get();
    }

    public boolean showUpdateDialog() {
        headerContainer.addClassName("update-dialog-header-style");
        AtomicBoolean isClicked = new AtomicBoolean(false);
        dialog.addConfirmListener(event -> isClicked.set(event.isFromClient()));
        return isClicked.get();
    }

    public boolean showDeleteDialog() {
        headerContainer.addClassName("delete-dialog-header-style");
        AtomicBoolean isClicked = new AtomicBoolean(false);
        dialog.addConfirmListener(event -> isClicked.set(event.isFromClient()));
        return isClicked.get();
    }


}//end of class...
