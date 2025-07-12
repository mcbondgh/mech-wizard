package com.mech.app.views.login;

import com.mech.app.configfiles.secutiry.DataEncryptor;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.dao.DAO;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "/login")
@RouteAlias("/auto-mechanic-login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private TextField usernameField;
    private PasswordField passwordField;
    private AtomicReference<String> ACCESS_TYPE;
    private final DAO DAO_MODEL;

    public LoginView() {
        setSizeFull();
        getStyle().set("background", "var(--lumo-contrast-5pct)");
        getStyle().set("align-items", "center");
        getStyle().set("justify-content", "center");
        getStyle().set("flex-direction", "column");
        setPadding(false);
        setSpacing(false);
        DAO_MODEL = new DAO();
    }

    @Override
    public void onAttach(AttachEvent event) {
        try {
            var accessType = event.getUI().getActiveViewLocation().getQueryParameters().getParameters("access").get(0);
            ACCESS_TYPE = new AtomicReference<>(accessType);
            // Load the login form component
            add(loginForm(ACCESS_TYPE.get()));

        } catch (Exception e) {
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    /*******************************************************************************************************************
     INPUT SECTION
     *******************************************************************************************************************/
    private Component inputSection() {
        usernameField = new TextField("Username");
        usernameField.setRequired(true);
        usernameField.setPlaceholder("Enter your username");
        usernameField.setPrefixComponent(VaadinIcon.USER.create());
        usernameField.setWidth("300px");
        usernameField.addClassNames("input-style", "username-field");

        passwordField = new PasswordField("Password");
        passwordField.setPlaceholder("Enter your password");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setWidth("300px");
        passwordField.setRequired(true);
        passwordField.addClassNames("input-style", "password-field");

        var loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
        loginButton.addClassName("login-button");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        loginButton.addClickShortcut(Key.ENTER);
        loginButton.addSingleClickListener(e -> {

            var ui = e.getSource().getUI().get();
            boolean emptyField = usernameField.isEmpty() || passwordField.isEmpty();
            if (emptyField) {
                usernameField.setErrorMessage("username required");
                passwordField.setErrorMessage("password required");
            }else {
                processLoginDetails(usernameField.getValue(), passwordField.getValue());
            }
        });

        var forgetButton = new Anchor("/forgot-password", "Forgot Password?");
        forgetButton.addClassName("forgot-password-link");

        VerticalLayout inputSection = new VerticalLayout(usernameField, passwordField, loginButton, forgetButton);
        inputSection.setAlignItems(Alignment.CENTER);
        inputSection.setSpacing(false);
        inputSection.addClassName("input-section");

        return inputSection;
    }

    private void processLoginDetails(String username, String userPassword) {
        var userDetails = DAO_MODEL.fetchUserByUsername(username);
        Notification popup = new Notification();
        try {
            String fullName = userDetails.getFirst().name();
            String hashedPassword = userDetails.getFirst().password();
            String role = userDetails.getFirst().role();
            String status = userDetails.getFirst().status();
            int userId = userDetails.getFirst().id();
            int shopId = userDetails.getFirst().shopId();
            int clientId = userDetails.getFirst().clientId();

            boolean checkPassword = DataEncryptor.verifyPassword(userPassword, hashedPassword);
            if (!checkPassword) {
                popup.setText("Invalid username or password");
                popup.setDuration(4000);
                popup.setPosition(Notification.Position.TOP_CENTER);
                popup.addThemeVariants(NotificationVariant.LUMO_ERROR);
                popup.open();
                return;
            }
            if (!status.equalsIgnoreCase("active")) {
                popup.setText("Your account is disabled");
                popup.setDuration(4000);
                popup.setPosition(Notification.Position.TOP_CENTER);
                popup.addThemeVariants(NotificationVariant.LUMO_ERROR);
                popup.open();
                return;
            }
            popup.setText("Successfully logged In");
            popup.setDuration(4000);
            popup.setPosition(Notification.Position.TOP_CENTER);
            popup.addThemeVariants(NotificationVariant.LUMO_ERROR);

            SessionManager.setAttribute("fullName", fullName);
            SessionManager.setAttribute("userId", userId);
            SessionManager.setAttribute("clientId", clientId);
            SessionManager.setAttribute("status", status);
            SessionManager.setAttribute("shopId", shopId);
            SessionManager.setAttribute("role", role);
            SessionManager.setAttribute("username", username);
            SessionManager.setAttribute("access", ACCESS_TYPE.get());

            if (ACCESS_TYPE.get().equalsIgnoreCase("admin-portal")) {
                UI.getCurrent().getPage().setLocation("dashboard");
            } else UI.getCurrent().getPage().setLocation("/customer-dashboard");

        } catch (NoSuchElementException ex) {
            popup.setText("Invalid username or password");
            popup.setDuration(4000);
            popup.setPosition(Notification.Position.TOP_CENTER);
            popup.addThemeVariants(NotificationVariant.LUMO_ERROR);
            popup.open();
        }

    }

    /*******************************************************************************************************************
     Logo Section
     *******************************************************************************************************************/
    private Component logoSection() {
        // Create a logo section with an image
        var logo = new Image("images/logo-icon.png", "Company Logo");
        logo.getStyle().setWidth("100px");

        VerticalLayout logoSection = new VerticalLayout(logo);
        logoSection.setAlignItems(Alignment.CENTER);
        logoSection.setSpacing(false);
        logoSection.addClassName("logo-section");

        return logoSection;
    }

    //Form layout for the login form
    private Component loginForm(String accessType) {

        String value = "client-portal".equalsIgnoreCase(accessType) ? "Client Portal" : "Technician Portal";

        H4 title = new H4(value);
        title.addClassName("login-title");

        // Create the login form layout
        Div formLayout = new Div();
        formLayout.add(logoSection(), title, inputSection());
        formLayout.addClassNames("login-form-layout");

        VerticalLayout layout = new VerticalLayout(formLayout);
        layout.addClassNames("login-view", "fade-in");
        return layout;
    }
}
