package com.mech.app.components;

import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.models.CustomerModel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

public class UserProfileComponent {
    private CustomerModel DATA_MODEL;
    public UserProfileComponent(){
        DATA_MODEL = new CustomerModel();
    }

    public VerticalLayout userProfileComponent(int customerId, int userId) {
        //extract users data here by customerId;
        var UserData = DATA_MODEL.fetchUserByUserId(userId);


        H3 headerText = new H3("Your Profile");
        VerticalLayout layout = new VerticalLayout();
        Image image = new Image(LineAwesomeIconUrl.USER, "LOGO");
        image.addClassName("profile-image-style");

        var otherNumber = UserData.getOtherNumber() == null ? UserData.getMobileNumber() : UserData.getOtherNumber();

        TextField nameField = new TextField("Full Name", UserData.getFullName(), "Your name");
        TextField numberField = new TextField("Mobile Number", UserData.getMobileNumber(), "0200000000");
        TextField otherNumberField = new TextField("Other Number", otherNumber, "0200000000");
        EmailField emailField = new EmailField("Email Address", "admin@example.com");
        emailField.setValue(UserData.getUsername());
        TextField addressField = new TextField("Digital Address", UserData.getAddress(), "EK-0001-0001");
        Button saveButton = new Button("Update Details", LineAwesomeIcon.RECYCLE_SOLID.create());
        saveButton.setWidthFull();
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
        otherNumberField.addClassNames("input-style", "profile-other-number-field");

        emailField.setReadOnly(true);
        emailField.setHelperText("Email is set as your username and cannot be changed");

//        FormLayout formLayout = new FormLayout();
//        responsiveForm = new FormColumns(formLayout );
//        responsiveForm.threeColumns();

        Section formLayout = new Section(nameField, numberField, otherNumberField, emailField,
                addressField, saveButton);

        formLayout.addClassName("settings-profile-formlayout");

        layout.addClassNames("tab-content-container", "profile-tab-content");
        layout.setWidthFull();

        layout.add(headerText, imageSection, formLayout);
        return layout;
    }

}
