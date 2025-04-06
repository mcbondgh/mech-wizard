package com.mech.app.components.transactions;

import com.mech.app.dataproviders.servicesrequest.ServiceRequestDataProvider;
import com.mech.app.dataproviders.transactions.TransactionsDataProvider;
import com.mech.app.specialmethods.ComponentLoader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;

public class TransactionDialogs {
    private Dialog dialog;
    private final Tabs tabs = new Tabs();
    private final Tab cashTab = new Tab("Cash");
    private final Tab electronicTab = new Tab("E-Cash");
    private final Tab posTab = new Tab("Card | POS");
    private VerticalLayout dialogContent;
    private final TextField transactionIdField = new TextField("Transaction No:");
    private final NumberField discountField = new NumberField("Discount Amount", "eg 10.00");
    private final TextField accountNumberField = new TextField("Account Number");
    private Button actionButton;

    private TransactionsDataProvider.transactionRecord data;
    private final RadioButtonGroup<String> paymentMethodSelector = new RadioButtonGroup<>("Select Payment Method");

    public TransactionDialogs() {
        dialogContent = new VerticalLayout();
        dialog = new Dialog(dialogContent);
        dialog.addClassNames("transaction-dialog");
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setModal(false);
        dialogContent.setSpacing(false);

        dialog.addClassNames("transaction-dialog");
        tabs.addClassNames("payment-tab");
        cashTab.addClassNames("payment-item-tab");
        posTab.addClassNames("payment-item-tab");
        discountField.addClassNames("input-style");
        electronicTab.addClassNames("payment-item-tab");
        dialogContent.addClassNames("transaction-dialog-content-box");
    }

    public TransactionDialogs(TransactionsDataProvider.transactionRecord data) {
        this.data = data;
        dialogContent = new VerticalLayout();
        dialog = new Dialog(dialogContent);
        dialog.addClassNames("transaction-dialog");
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setModal(false);
        dialogContent.setSpacing(false);

        dialog.addClassNames("transaction-dialog");
        tabs.addClassNames("payment-tab");
        cashTab.addClassNames("payment-item-tab");
        posTab.addClassNames("payment-item-tab");
        discountField.addClassNames("input-style");
        electronicTab.addClassNames("payment-item-tab");
        dialogContent.addClassNames("transaction-dialog-content-box");
        paymentMethodSelector.setItems(ComponentLoader.getPaymentMethods());
    }

    private Component headerComponent(String headerText) {
        var closeBox = new Div(VaadinIcon.CLOSE_SMALL.create());
        closeBox.addClassNames("close-button");
        var headerLayout = new HorizontalLayout(new H3(String.format("%s %s", headerText, data.jobNo())), closeBox);
        headerLayout.addClassNames("dialog-header-box");
        headerLayout.setFlexGrow(1);
        closeBox.addSingleClickListener(e -> dialog.close());
        return headerLayout;
    }

    public void paymentDialog() {
        
        dialog.getHeader().add(headerComponent("Make Payment For"));

        //section one
        tabs.add(cashTab, electronicTab, posTab);
        var sectionOne = new VerticalLayout(new H5("Select Payment Method"), tabs);
        sectionOne.addClassNames("payment-section-box");
        paymentMethodSelector.addClassName("transaction-payment-selector");

        //section two
        var sectionTwo = new VerticalLayout(new H5("Transaction Details"), accountNumberField, transactionIdField);
        sectionTwo.setSpacing(false);
        sectionTwo.addClassNames("payment-section-box");

        var sectionThree = new VerticalLayout();
        sectionThree.addClassNames("payment-section-box", "successful-payment-box");
        var icon = VaadinIcon.CHECK_CIRCLE_O.create();
        var h2 = new H2("Nice, Payment Collected");
        var subText = new Paragraph(String.format("Payment successfully collected for job %s", data.jobNo()));
        var button = new Button("Make Another Collection");
        sectionThree.add(icon, h2, subText, button);

        button.addSingleClickListener(e -> dialog.close());

        actionButton = new Button("Make Payment", LineAwesomeIcon.CC_VISA.create());
        actionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        actionButton.addClassNames("default-btn-style");

        //section four
        var amountLabel = new H3("Total Amount");
        var amountValue = new H3(String.format("Ghc%s", data.amount()));
        var sectionFour = new FlexLayout(amountLabel, amountValue);
        sectionFour.addClassNames("transaction-amount-box");
        sectionFour.setWidthFull();
        sectionFour.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionFour.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        var paymentComponentBox = new Section(sectionOne, sectionTwo, discountField, sectionFour, actionButton);
        paymentComponentBox.getStyle().setMargin("0").setPadding("0");
        dialogContent.add(paymentComponentBox);
        dialog.add(dialogContent);
        dialog.open();

        //ACTION EVENT LISTENERS
        actionButton.addClickListener(e -> dialogContent.replace(paymentComponentBox, sectionThree));
    }

    public void receiptDialog() {
        // Header
        dialog.getHeader().add(headerComponent("Transaction Receipt For"));

        // Receipt Details
        var customerName = new Paragraph(String.format("Customer Name: %s", data.customerName()));
        var serviceType = new Paragraph(String.format("Service Type: %s", data.service()));
        var vehicle = new Paragraph(String.format("Vehicle: %s", data.car()));
        var technician = new Paragraph("Technician: John Smith"); // Example technician name
        var carParts = new Paragraph("Car Parts Bought: Oil Filter - Ghc50.00, Air Filter - Ghc30.00");
        var paymentMethod = new Paragraph("Payment Method: Cash"); // Example payment method
        var totalAmount = new Paragraph(String.format("Total Amount Paid: Ghc%s", data.amount()));
        var discountApplied = new Paragraph("Discount Applied: Ghc10.00"); // Example discount
        var receiptNo = new Paragraph("Receipt No: RCT-2025-001"); // Example receipt number
        var labourCost = new Paragraph("Labour Cost: Ghc100.00"); // Example labour cost

//        var image = new Image("images/logo-icon.png", "img");
        // Styling
        customerName.addClassNames("receipt-detail");
        serviceType.addClassNames("receipt-detail");
        vehicle.addClassNames("receipt-detail");
        technician.addClassNames("receipt-detail");
        carParts.addClassNames("receipt-detail");
        paymentMethod.addClassNames("receipt-detail");
        totalAmount.addClassNames("receipt-detail");
        discountApplied.addClassNames("receipt-detail");
        receiptNo.addClassNames("receipt-detail");
        labourCost.addClassNames("receipt-detail");

        // Layout
        var receiptLayout = new VerticalLayout(
            customerName,
            serviceType,
            vehicle,
            technician,
            carParts,
            paymentMethod,
            totalAmount,
            discountApplied,
            receiptNo,
            labourCost
        );
        receiptLayout.addClassNames("receipt-layout");
        receiptLayout.setSpacing(false);
        receiptLayout.setPadding(true);

        // Add to dialog
        dialogContent.add(receiptLayout);
        dialog.open();
    }

    public void feedbackDialog(String jobNumber) {
        dialog.getHeader().add("Share Your Feedback For " + jobNumber);
        TextArea textArea = new TextArea("Please enter your comments here", "Your may choose to leave this field empty.");
        textArea.addClassNames("feedback-comment-field");
        textArea.setWidthFull();
        textArea.setHeight("30px");

        RadioButtonGroup<Component> starRates = new RadioButtonGroup<>("How would you rate your service");
        starRates.addClassName("feedback-rates");
        var star1 = new Div(LineAwesomeIcon.STAR_SOLID.create(), new Span("1"));
        var star2 = new Div(LineAwesomeIcon.STAR_SOLID.create(), new Span("2"));
        var star3 = new Div(LineAwesomeIcon.STAR_SOLID.create(), new Span("3"));
        var star4 = new Div(LineAwesomeIcon.STAR_SOLID.create(), new Span("4"));
        var star5 = new Div(LineAwesomeIcon.STAR_SOLID.create(), new Span("5"));
        starRates.setItems(List.of(star1, star2, star3, star4, star5));

        var button = new Button("Submit Feedback", LineAwesomeIcon.STAR.create());

        var body = new VerticalLayout(starRates, textArea, button);
        body.setBoxSizing(BoxSizing.BORDER_BOX);
        body.setWidthFull();
        body.addClassNames("feedback-body");
        dialogContent.add(body);
        dialog.open();
    }

}//end of class...
