package com.mech.app.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomDialog {
    private ConfirmDialog dialog;
    private static Notification notification;
    private FlexLayout headerContainer;

    public CustomDialog() {
    }

    public CustomDialog(String title, String content) {
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

    public void defaultDialogBox(Component content, String title, String subtitle) {
        Dialog dialog = new Dialog(content);
        var headerText = new H4(title);
        var subTitle = new Span(subtitle);
        var container1 = new Div(headerText, subTitle);
        container1.setWidthFull();

        var closeButton = new Span(VaadinIcon.CLOSE_SMALL.create());
        closeButton.addClassNames("dialog-close-button");
        closeButton.addClickListener(e -> {
            dialog.removeAll(); dialog.close();
        });

        FlexLayout headerComponent = new FlexLayout(container1, closeButton);
        headerComponent.addClassNames("dialog-header-component", "add-employee-dialog-component");
        headerComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerComponent.setAlignContent(FlexLayout.ContentAlignment.CENTER);
        headerComponent.setWidthFull();
        dialog.addClassNames("default-dialog-style");
        dialog.open();
    }

}//end of class...
