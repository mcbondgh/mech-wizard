package com.mech.app.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

public class UserProfileComponent {
    public UserProfileComponent(){}

    public VerticalLayout userProfileComponent() {
        H3 headerText = new H3("Profile Settings");
        VerticalLayout layout = new VerticalLayout();
        Image image = new Image(LineAwesomeIconUrl.USER, "LOGO");
        image.addClassName("profile-image-style");

        TextField nameField = new TextField("Full Name", "Ama Mohammed Selassi");
        TextField numberField = new TextField("Mobile Number", "029999999");
        EmailField emailField = new EmailField("Email Address", "ama1234@example.com");
        TextField addressField = new TextField("Digital Address", "EK-0001-0001");
        PasswordField firstPassField = new PasswordField("Password", "*********");
        PasswordField secondPassField = new PasswordField("Confirm Password", "********");
        Button saveButton = new Button("Save Changes", LineAwesomeIcon.SAVE.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload imageUpload = new Upload(buffer);

        Section imageSection = new Section(image, imageUpload);

        //add class names to components
        nameField.addClassNames("input-style", "profile-name-field");
        numberField.addClassNames("input-style", "profile-number-field");
        emailField.addClassNames("input-style", "profile-email-field");
        addressField.addClassNames("input-style", "profile-address-field");
        saveButton.addClassNames("default-button-style");
        imageSection.addClassName("settings-profile-container");
        firstPassField.addClassNames("input-style", "profile-password-field");
        secondPassField.addClassNames("input-style", "profile-password-field");

//        FormLayout formLayout = new FormLayout();
//        responsiveForm = new FormColumns(formLayout );
//        responsiveForm.threeColumns();

        Section formLayout = new Section(nameField, numberField, emailField,
                addressField, firstPassField, secondPassField, saveButton);

        formLayout.addClassName("settings-profile-formlayout");

        layout.addClassNames("tab-content-container", "profile-tab-content");
        layout.setWidthFull();

        layout.add(headerText, imageSection, formLayout);
        return layout;
    }

}
