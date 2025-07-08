package com.mech.app.views.customers;

import com.mech.app.components.CustomDialog;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.configfiles.secutiry.SessionManager;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.transactions.CustomerAccountRecord;
import com.mech.app.dataproviders.transactions.TransactionLogs;
import com.mech.app.dataproviders.transactions.TransactionsDataProvider;
import com.mech.app.models.CustomerModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
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
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.jetbrains.annotations.NotNull;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "view/customer-account", layout = MainLayout.class)
@PageTitle("Customer Account")
@PermitAll
public class CustomerAccountView extends VerticalLayout implements BeforeEnterObserver {
    // This class represents the customer account view in the application.

    private static AtomicInteger USER_ID;
    private static AtomicInteger SHOP_ID;
    private static AtomicInteger CUSTOMER_ID;
    private static AtomicReference<String> CUSTOMER_NAME, MOBILE_NUMBER;
    private final CustomerModel DATA_MODEL;
    private CustomersDataProvider CUSTOMER_DATA_PROVIDER;
    private CustomDialog DIALOG_BOX;

    public CustomerAccountView() {
        setId("customer-account-view");
        addClassNames("page-body", "customer-account-view");
        setSizeFull();
        DATA_MODEL = new CustomerModel();
        CUSTOMER_DATA_PROVIDER = new CustomersDataProvider();
        USER_ID = new AtomicInteger(SessionManager.DEFAULT_USER_ID);
        SHOP_ID = new AtomicInteger(SessionManager.DEFAULT_SHOP_ID);
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
        var name = beforeEnterEvent.getLocation().getQueryParameters().getSingleParameter("name");
        var mobile = beforeEnterEvent.getLocation().getQueryParameters().getSingleParameter("mobile_number");

        customerId.ifPresentOrElse(param -> {
            CUSTOMER_ID = new AtomicInteger(Integer.parseInt(param));
        }, () -> {
            ;
            // If no customer ID is provided, navigate back to the customers view
            Notification.show("No customer ID provided. Redirecting to customers view.");
            getUI().ifPresent(ui -> ui.navigate("view/customers"));
        });
        CUSTOMER_NAME = new AtomicReference<>(name.get());
        MOBILE_NUMBER = new AtomicReference<>(mobile.get());
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
        //retrieve customer data from the data model by customer ID
        double accountBalance;
        try {
            var customerRecord = DATA_MODEL.getCustomerNameAndAccountBalanceOnly(CUSTOMER_ID.get()).getFirst();
            accountBalance = customerRecord.amount();
        } catch (NoSuchElementException ignored) {
            // If no customer data is found, we can set default values or handle the error accordingly.
            accountBalance = 0.00;
        }
        var box1 = accountCard("Customer Name: ", CUSTOMER_NAME.get());
        var box2 = accountCard("Mobile Number: ", MOBILE_NUMBER.get());

        var balanceLabel = new Paragraph("Current Balance: ");
        var balanceValue = new H3("GHc " + accountBalance);
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
        record itemRecords(SvgIcon icon, String label) {
        }
        var amountField = new NumberField("Amount", "0.00");
        var referenceField = new TextField("Reference No.", "Enter transaction reference");
        var textArea = new TextArea("Notes", "Enter any notes for this transaction");
        Button saveButton = new Button("Save Transaction", LineAwesomeIcon.SAVE.create());
//        saveButton.addClassNames("default-button-style");
        saveButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON);

        String[] items = {"Cash", "Card", "Mobile Money"};
        RadioButtonGroup<String> paymentMethod = new RadioButtonGroup<>("Payment Methods", items);
        paymentMethod.setValue("Cash");
        paymentMethod.addClassName("payment-method-radio-group");
        paymentMethod.addClassName("input-style");

        amountField.setRequired(true);
        referenceField.setVisible(false);


//        paymentMethod.setRenderer(new ComponentRenderer<Component, itemRecords>(item -> {
//            HorizontalLayout itemLayout = new HorizontalLayout();
//            SvgIcon iconComponent = item.icon();
//            iconComponent.addClassName("payment-icon");
//            itemLayout.add(iconComponent, new Span(item.label()));
//            itemLayout.setAlignItems(Alignment.CENTER);
//            itemLayout.addClassName("payment-radio-button");
//            return itemLayout;
//        }));

        layout.add(paymentMethod, amountField, referenceField, textArea, saveButton);

        //ADD EVENT LISTENERS...
        paymentMethod.addValueChangeListener(event -> {
            switch (event.getValue()) {
                case "Card" -> {
                    referenceField.setVisible(true);
                    referenceField.setRequired(true);
                    referenceField.setInvalid(referenceField.isEmpty());
                    referenceField.setHelperText("Enter transaction number");
                    amountField.setHelperText("Enter the amount to deposit using card");
                }
                case "Cash" -> {
                    referenceField.setVisible(false);
                    amountField.setHelperText("Enter the amount to deposit using cash");
                }
                case "Mobile Money" -> {
                    referenceField.setVisible(true);
                    referenceField.setRequired(true);
                    referenceField.setInvalid(referenceField.isEmpty());
                    referenceField.setHelperText("Enter transaction number");
                    amountField.setHelperText("Enter the amount to deposit using mobile money");
                }
            }
        });

        saveButton.addClickListener(event -> {
            DIALOG_BOX = new CustomDialog();
            // Logic to save the transaction

            var amountEmpty = amountField.isEmpty();
            var referenceEmpty = referenceField.isVisible() && referenceField.isInvalid();
            var cashMethod = paymentMethod.getValue().equals("Cash");
            var cardAndMomo = paymentMethod.getValue().equals("Card") || paymentMethod.getValue().equals("Mobile Money");

            if (amountEmpty && cashMethod) {
                amountField.setInvalid(true);
                DIALOG_BOX.showWarningNotification("Please fill in all required fields.");
                return;
            }
            if (referenceEmpty && cardAndMomo) {
                DIALOG_BOX.showErrorNotification("Reference number is required for non-cash transactions.");
                return;
            }

            // Assuming the transaction is saved successfully
            var action = DIALOG_BOX.showSaveDialog("SAVE TRANSACTION", MessageLoaders.confirmationMessage("save transaction"));
            action.addConfirmListener(confirm -> {
                UI ui = UI.getCurrent();
                ui.access(
                        () -> {
                            double accountBalance = 0.00;
                            try {
                                var customerRecord = DATA_MODEL.getCustomerNameAndAccountBalanceOnly(CUSTOMER_ID.get()).getFirst();
                                accountBalance = customerRecord.amount();
                            } catch (NoSuchElementException ignored) {
                                // If no customer data is found, we can set default values or handle the error accordingly.
                            }
                            var ACCOUNT_DATA = new TransactionLogs();
                            ACCOUNT_DATA.setCustomerId(CUSTOMER_ID.get());
                            ACCOUNT_DATA.setUserId(USER_ID.get());
                            ACCOUNT_DATA.setTransactionId(referenceEmpty ? "none" : referenceField.getValue());
                            ACCOUNT_DATA.setTransactionType("deposit");
                            ACCOUNT_DATA.setPaymentMethod(paymentMethod.getValue());
                            ACCOUNT_DATA.setNotes(textArea.getValue());
                            ACCOUNT_DATA.setAmount(amountField.getValue());

                            double amount = (accountBalance + amountField.getValue());
                            int response = DATA_MODEL.insertOrUpdateCustomerAccountByCustomerId(CUSTOMER_ID.get(), amount, USER_ID.get());
                            response += DATA_MODEL.logCustomerTransaction(ACCOUNT_DATA);

                            if (response > 0) {
                                String content = CUSTOMER_NAME.get() + " with mobile number " + MOBILE_NUMBER.get() + " has been credited with an amount of " + amountField.getValue() + ".";
                                var notificationLog = new NotificationRecords("CUSTOMER ACCOUNT LOADED", content, USER_ID.get(), SHOP_ID.getPlain());
                                DATA_MODEL.logNotification(notificationLog);
                                DIALOG_BOX.showSuccessNotification(MessageLoaders.successMessage());
                                ui.refreshCurrentRoute(false);
                            } else {
                                DIALOG_BOX.showErrorNotification(MessageLoaders.errorMessage("Unable to save transaction. Please try again."));
                            }
                        });
            });
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
        var transactionsData = DATA_MODEL.fetchCustomerTransactionLogsById(CUSTOMER_ID.get());

        Grid<TransactionLogs> grid = new Grid<>(TransactionLogs.class, false);
        grid.addClassName("alternative-grid-style");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setSizeFull();
        grid.addColumn(new LocalDateRenderer<>(e -> e.getTransactionDate().toLocalDateTime().toLocalDate())).setHeader("Date");
        grid.addColumn(TransactionLogs::getTransactionId).setHeader("Transaction ID").setSortable(true);
        grid.addColumn(TransactionLogs::getPaymentMethod).setHeader("Method");
        grid.addComponentColumn(this::gridAmountComponent).setHeader("Amount").setSortable(true);
        grid.addComponentColumn(this::gridPaymentTypeComponent).setHeader("Payment Type").setSortable(true);
        grid.addColumn(TransactionLogs::getNotes).setHeader("Description");
        grid.setEmptyStateComponent(ComponentLoader.emptyGridComponent("No transactions found for this customer."));
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setItems(transactionsData);
        return grid;
    }

    private Component gridAmountComponent(TransactionLogs transaction) {
        // This method creates a component to display the amount in the transaction grid.
        H5 amount = new H5("Ghc" + transaction.getAmount());
        amount.getStyle().set("color", "var(--lumo-success-text-color)");
        amount.addClassName("transaction-logs-amount");
        return amount;
    }

    private Component gridPaymentTypeComponent(TransactionLogs transaction) {
        // This method creates a component to display the payment type in the transaction grid.
        Span paymentType = new Span(transaction.getTransactionType());
        paymentType.addClassName("transaction-logs-payment-type");
        if ("deposit".equalsIgnoreCase(paymentType.getText())) {
            paymentType.getElement().getThemeList().add("badge success primary small pill");
        } else {
            paymentType.getElement().getThemeList().add("badge error primary small pill");
        }
        return paymentType;
    }

}//end of class...
