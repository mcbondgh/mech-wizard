package com.mech.app.views.login;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.time.temporal.ValueRange;

@Route(value = "")
@RouteAlias("/login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private TextField usernameField;
    private PasswordField passwordField;

    public LoginView() {
        setSizeFull();
        getStyle().set("background", "var(--lumo-contrast-5pct)");
        getStyle().set("align-items", "center");
        getStyle().set("justify-content", "center");
        getStyle().set("flex-direction", "column");
        setPadding(false);
        setSpacing(false);

        // Load the login form component
        add(loginForm());
    }

    /*******************************************************************************************************************
        INPUT SECTION
     *******************************************************************************************************************/
    private Component inputSection() {
        usernameField = new TextField("Username");
        usernameField.setPlaceholder("Enter your username");
        usernameField.setPrefixComponent(VaadinIcon.USER.create());
        usernameField.setWidth("300px");
        usernameField.addClassNames("input-style", "username-field");

        passwordField = new PasswordField("Password");
        passwordField.setPlaceholder("Enter your password");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setWidth("300px");
        passwordField.addClassNames("input-style", "password-field");

        var loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
        loginButton.addClassName("login-button");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        loginButton.addSingleClickListener(e -> UI.getCurrent().navigateToClient("/dashboard"));

        var forgetButton = new Anchor("/forgot-password", "Forgot Password?");
        forgetButton.addClassName("forgot-password-link");

        VerticalLayout inputSection = new VerticalLayout(usernameField, passwordField, loginButton, forgetButton);
        inputSection.setAlignItems(Alignment.CENTER);
        inputSection.setSpacing(false);
        inputSection.addClassName("input-section");

        return inputSection;
    }

    /*******************************************************************************************************************
     Logo Section
     *******************************************************************************************************************/

    private Component logoSection() {
        // Create a logo section with an image
        var logo = new Image("images/logo-icon.png", "Company Logo");
        logo.getStyle().setWidth("150px");

        VerticalLayout logoSection = new VerticalLayout(logo);
        logoSection.setAlignItems(Alignment.CENTER);
        logoSection.setSpacing(false);
        logoSection.addClassName("logo-section");

        return logoSection;
    }

    //Form layout for the login form
    private Component loginForm() {

        H4 title = new H4("User Login");
        title.addClassName("login-title");

        // Create the login form layout
        Div formLayout = new Div();
        formLayout.add(logoSection(), title, inputSection());
        formLayout.addClassNames("login-form-layout");

        VerticalLayout layout = new VerticalLayout(formLayout);
        layout.addClassName("login-view");
        return layout;
    }
}
