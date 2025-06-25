package com.mech.app.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class CustomDialog {
    private static ConfirmDialog dialog;
    private static Notification notification;
    private FlexLayout headerContainer;
    private static Button button;
    private static Image icon;
    private static Paragraph paragraph;

    public CustomDialog() {
        headerContainer = new FlexLayout();
        headerContainer.addClassName("confirm-dialog-header-container");
        headerContainer.setWidthFull();
        headerContainer.getStyle().setMargin("0").setPadding("10px");
        dialog = new ConfirmDialog();
        dialog.addClassNames("confirm-dialog-style");
        dialog.setHeader(headerContainer);
        dialog.setCancelable(true);

        var cancelButton = new Button("Cancel");
        cancelButton.getStyle().setWidth("fit-content");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

        dialog.setCancelButton(cancelButton);

        icon = new Image();
        icon.setClassName("notification-icon");

        paragraph = new Paragraph();
        paragraph.setClassName("notification-text");

    }

    public ConfirmDialog showSaveDialog(String title, String content) {
        headerContainer.addClassName("save-dialog-header-style");
        button = new Button("Confirm");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        headerContainer.add(new H4(title));
        dialog.setText(content);
        dialog.setConfirmButton(button);
        dialog.open();
        return dialog;
    }

    public ConfirmDialog showUpdateDialog(String title, String content) {
        headerContainer.addClassName("update-dialog-header-style");
        button = new Button("Update");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        button.addClassNames("update-dialog-button", "confirm-dialog-button");
        dialog.setConfirmButton(button);
        headerContainer.add(new H4(title));
        dialog.setText(content);
        dialog.open();
        return dialog;
    }

    public ConfirmDialog showDeleteDialog(String title, String content) {
        headerContainer.addClassName("delete-dialog-header-style");
        dialog.addClassNames("delete-dialog-style", "confirm-dialog-button");
        button = new Button("Delete");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        dialog.setConfirmButton(button);
        headerContainer.add(new H4(title));
        dialog.setText(content);
        dialog.open();
        return dialog;
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
            dialog.close();
            UI.getCurrent().refreshCurrentRoute(false);
        });

        FlexLayout headerComponent = new FlexLayout(container1, closeButton);
        headerComponent.addClassNames("dialog-header-component", "add-employee-dialog-component");
        headerComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerComponent.setAlignContent(FlexLayout.ContentAlignment.CENTER);
        headerComponent.setWidthFull();
        dialog.addClassNames("default-dialog-style");
        dialog.getHeader().add(headerComponent);
        dialog.open();
    }
//    CUSTOM NOTIFICATION SECTION
    public void showSuccessNotification(String content) {
        icon.setSrc("icons/icons8-check-mark-30.png");
        setNotificationText(content);
    }

    public void showErrorNotification(String content) {
        icon.setSrc("icons/icons8-delete-48.png");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR, NotificationVariant.LUMO_PRIMARY);
        paragraph.getStyle().setColor(LumoUtility.TextColor.ERROR);
        setNotificationText(content);
    }

    public void showWarningNotification(String content) {
        icon.setSrc("icons/icons8-warning-30.png");
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING, NotificationVariant.LUMO_PRIMARY);
        setNotificationText(content);
    }

    private void setNotificationText(String content) {
        paragraph.setText(content);
        headerContainer = new FlexLayout(icon, paragraph);
        headerContainer.getStyle().setWidth("100%")
                .setAlignItems(Style.AlignItems.CENTER)
                .setJustifyContent(Style.JustifyContent.INITIAL);
        headerContainer.addClassName("notification-header-container");
        notification = new Notification(headerContainer);
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }

}//end of class...
