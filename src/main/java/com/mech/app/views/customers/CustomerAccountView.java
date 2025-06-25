package com.mech.app.views.customers;

import com.helger.commons.url.URLParameter;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.dataproviders.transactions.TransactionLogs;
import com.mech.app.models.CustomerModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Route(value = "view/customer-account", layout = MainLayout.class)
@PageTitle("Customer Account")
@PermitAll
public class CustomerAccountView extends VerticalLayout implements BeforeEnterObserver {
    // This class represents the customer account view in the application.

    private static AtomicInteger USER_ID;
    private static AtomicInteger STORE_ID;
    private static AtomicInteger CUSTOMER_ID;
    private final CustomerModel DATA_MODEL;
    private CustomersDataProvider CUSTOMER_DATA_PROVIDER;

    public CustomerAccountView() {
        setId("customer-account-view");
        addClassNames("page-body", "customer-account-view");
        setSizeFull();
        DATA_MODEL = new CustomerModel();
        CUSTOMER_DATA_PROVIDER = new CustomersDataProvider();
    }

    @Override
    public void onAttach(AttachEvent listener) {
        // Initialize components or data providers here if needed,
        // For example, you can load customer data and display it in the view
        add(
                headerSection(), pageBodySection()
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var customerId = beforeEnterEvent.getLocation().getQueryParameters().getSingleParameter("customer_id");
        customerId.ifPresentOrElse(param -> {
            CUSTOMER_ID = new AtomicInteger(Integer.parseInt(param));
        }, () -> {
            ;
            // If no customer ID is provided, navigate back to the customers view
            Notification.show("No customer ID provided. Redirecting to customers view.");
            getUI().ifPresent(ui -> ui.navigate("view/customers"));
        });
    }

    /*******************************************************************************************************************
     PAGE BODY SECTION
     *******************************************************************************************************************/
    @NotNull
    private Component headerSection() {
        var backButton = new Button("Back", VaadinIcon.ARROW_BACKWARD.create(), event -> {
            // Logic to navigate back to the previous view
            getUI().ifPresent(ui -> ui.navigate("view/customers"));
        });

        backButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        backButton.addClassNames("back-button");

        H4 title = new H4("Customer Account Balance and last 5 recorded deposits");
        // This method creates the header section of the customer account view.
        HorizontalLayout header = new HorizontalLayout(backButton, title);
        header.setWidthFull();
        header.addClassName("customer-account-header");
        // Add components to the header as needed
        return header;
    }

    private Component pageBodySection() {
        FormLayout formLayout = new FormLayout();
        FormLayout.ResponsiveStep responsiveStep = new FormLayout.ResponsiveStep("0", 1);
        FormLayout.ResponsiveStep responsiveStep2 = new FormLayout.ResponsiveStep("768px", 6);
        formLayout.setResponsiveSteps(responsiveStep, responsiveStep2);
        formLayout.addClassNames("form-layout-style");

        // Add components to the form layout
        formLayout.add(transactionSection(), 2);
        formLayout.add(depositHistorySection(), 4);

        VerticalLayout layout = new VerticalLayout(formLayout);
        layout.setWidthFull();
        layout.addClassNames("customer-account-body");

        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT SECTION
     *******************************************************************************************************************/

    // This method creates the customer account box that displays customer information and balance.
    private Component customerAccountBox() {
        var box1 = accountCard("Customer Name: ", "Customer Name Here");
        var box2 = accountCard("Mobile Number: ", "Customer Mobile Number Here");

        var balanceLabel = new Paragraph("Current Balance: ");
        var balanceValue = new H3("Ghc0.00");
        var box3 = new HorizontalLayout(balanceLabel, balanceValue);
        box3.addClassNames("customer-account-box", "account-box-3");

        var parentBox = new VerticalLayout(box1, box2, new Hr(), box3);
        parentBox.setClassName("customer-account-parent-box");
        parentBox.setWidthFull();

        var verticalLayout = new VerticalLayout(parentBox);
        verticalLayout.addClassName("customer-account-parent-layout");
        verticalLayout.setWidthFull();
        return verticalLayout;
    }

    private Component transactionSection() {
        var title = new H4("Load Customer Account");
        VerticalLayout layout = new VerticalLayout(title);
        layout.setSpacing(false);
        layout.addClassNames("transaction-section", "deposit-section");
        layout.setWidthFull();

        //add form components for performing transactions
        record itemRecords(SvgIcon icon, String label) {}
        var amountField = new NumberField("Amount", "0.00");
        var referenceField = new TextField("Reference No.", "Enter transaction reference");
        var textArea = new TextArea("Notes", "Enter any notes for this transaction");
        Button saveButton = new Button( "Save Transaction",LineAwesomeIcon.SAVE.create());
//        saveButton.addClassNames("default-button-style");
        saveButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON);
        RadioButtonGroup <itemRecords> paymentMethod = new RadioButtonGroup<>("Payment Methods");
        paymentMethod.addClassName("payment-method-radio-group");

        amountField.setRequired(true);
        referenceField.setVisible(false);

        var items = List.of(
                new itemRecords(LineAwesomeIcon.MONEY_BILL_SOLID.create(), "Cash"),
                new itemRecords(LineAwesomeIcon.CREDIT_CARD.create(), "Card"),
                new itemRecords(LineAwesomeIcon.MOBILE_SOLID.create(), "Mobile Money")
        );
        paymentMethod.setItems(items);
        paymentMethod.setValue(items.getFirst());

        paymentMethod.setRenderer(new ComponentRenderer<Component, itemRecords>(item -> {
            HorizontalLayout itemLayout = new HorizontalLayout();
            SvgIcon iconComponent = item.icon();
            iconComponent.addClassName("payment-icon");
            itemLayout.add(iconComponent, new Span(item.label()));
            itemLayout.setAlignItems(Alignment.CENTER);
            itemLayout.addClassName("payment-radio-button");
            return itemLayout;
        }));

        layout.add(paymentMethod, amountField, referenceField, textArea, saveButton);

        //ADD EVENT LISTENERS...
        paymentMethod.addValueChangeListener(event -> {
            switch (event.getValue().label) {
                case "Card" -> {
                    referenceField.setVisible(true);
                    amountField.setHelperText("Enter the amount to deposit using card");
                }
                case "Cash" -> {
                    referenceField.setVisible(false);
                    amountField.setHelperText("Enter the amount to deposit using cash");
                }
                case "Mobile Money" -> {
                    referenceField.setVisible(true);
                    amountField.setHelperText("Enter the amount to deposit using mobile money");
                }
            }
        });

        saveButton.addClickListener(event -> {
            // Logic to save the transaction
            String amount = amountField.getValue().toString();
            String reference = referenceField.getValue();
            String notes = textArea.getValue();
            if (amount.isEmpty() || Double.parseDouble(amount) <= 0) {
                Notification.show("Please enter a valid amount.");
                return;
            }
            // Save the transaction logic here
            Notification.show("Transaction saved successfully!");
        });
        return layout;
    }

    private Component depositHistorySection() {
        VerticalLayout layout = new VerticalLayout(customerAccountBox());
        layout.addClassNames("transaction-section", "deposit-history-section");
        layout.setSizeFull();

        // a grid or list of deposits
        var title = new H4("History");
        layout.add(title, transactionLogsGrid());

        return layout;
    }

    /*******************************************************************************************************************
     REFERENCE METHODS SECTION
     *******************************************************************************************************************/
    private Component accountCard(String label, String value) {
        var customerId = new Paragraph(label);
        var nameLabel = new H5(value);
        var box = new HorizontalLayout(customerId, nameLabel);
        box.addClassNames("customer-account-box", "account-box-1");
        return box;
    }

    private Grid<TransactionLogs> transactionLogsGrid() {
        Grid<TransactionLogs> grid = new Grid<>(TransactionLogs.class, false);
        grid.addClassName("alternative-grid-style");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();
        grid.addColumn(new LocalDateTimeRenderer<>(e -> e.getTransactionDate().toLocalDateTime())).setHeader("Date");
        grid.addColumn(TransactionLogs::getTransactionId).setHeader("Transaction ID").setSortable(true);
        grid.addColumn(TransactionLogs::getPaymentMethod).setHeader("Method");
        grid.addComponentColumn(this::gridAmountComponent).setHeader("Amount").setSortable(true);
        grid.addComponentColumn(this::gridPaymentTypeComponent).setHeader("Payment Type").setSortable(true);
        grid.addColumn(TransactionLogs::getNotes).setHeader("Description");
        grid.setEmptyStateComponent(ComponentLoader.emptyGridComponent("No transactions found for this customer."));
        return grid;
    }

    private Component gridAmountComponent(TransactionLogs transaction) {
        // This method creates a component to display the amount in the transaction grid.
        H3 amount = new H3("Ghc" + transaction.getAmount());
        amount.getStyle().set("color", "var(--lumo-success-text-color)");
        amount.addClassName("transaction-logs-amount");
        return amount;
    }
    private Component gridPaymentTypeComponent(TransactionLogs transaction) {
        // This method creates a component to display the payment type in the transaction grid.
        Span paymentType = new Span(transaction.getPaymentMethod());
        paymentType.getElement().getThemeList().add("badge success primary small pill");
        paymentType.addClassName("transaction-logs-payment-type");
        return paymentType;
    }

}//end of class...
