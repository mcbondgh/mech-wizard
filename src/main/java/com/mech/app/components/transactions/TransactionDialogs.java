package com.mech.app.components.transactions;

import com.google.common.util.concurrent.AtomicDouble;
import com.mech.app.components.CustomDialog;
import com.mech.app.configfiles.MessageLoaders;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.transactions.TransactionsDataProvider;
import com.mech.app.models.TransactionsModel;
import com.mech.app.specialmethods.ComponentLoader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionDialogs {
    private static CustomDialog dialog;
    private final Tabs tabs = new Tabs();
    private final Tab cashTab = new Tab();
    private final Tab electronicTab = new Tab();
    private final Tab posTab = new Tab();
    private VerticalLayout dialogContent;
    private final TextField transactionIdField = new TextField("Transaction No:");
    private final NumberField discountField = new NumberField("Discount Amount", "eg 10.00");
    private final TextField accountNumberField = new TextField("Account Number");
    private Button actionButton;
    private static final TransactionsModel DAO_MODEL = new TransactionsModel();

    private TransactionsDataProvider.TransactionRecord data;
    private final RadioButtonGroup<String> paymentMethodSelector = new RadioButtonGroup<>("Select Payment Method");


    private Component setTabs(String name, LineAwesomeIcon icon) {
        Span text = new Span(name);
        text.addClassName("tabs-text");
        var i = icon.create();
        i.addClassName("tabs-icon");
        HorizontalLayout layout = new HorizontalLayout(text, i);
        layout.setWidthFull();
        layout.addClassNames("tabs-layout");
        return layout;
    }

    public TransactionDialogs() {
        dialogContent = new VerticalLayout();

        tabs.addClassNames("payment-tab");
        cashTab.addClassNames("payment-item-tab");
        posTab.addClassNames("payment-item-tab");
        discountField.addClassNames("input-style");
        electronicTab.addClassNames("payment-item-tab");
        dialogContent.addClassNames("transaction-dialog-content-box");
    }

    public TransactionDialogs(TransactionsDataProvider.TransactionRecord data) {
        this.data = data;
        dialogContent = new VerticalLayout();
        dialogContent.setWidthFull();
        cashTab.add(setTabs("Cash", LineAwesomeIcon.MONEY_BILL_SOLID));
        posTab.add(setTabs("POS|CARD", LineAwesomeIcon.CREDIT_CARD));
        electronicTab.add(setTabs("Mobile Money", LineAwesomeIcon.MOBILE_SOLID));
        tabs.add(cashTab, posTab, electronicTab);

        tabs.addClassNames("payment-tab");
        cashTab.addClassNames("payment-item-tab");
        posTab.addClassNames("payment-item-tab");
        discountField.addClassNames("input-style");
        electronicTab.addClassNames("payment-item-tab");
        dialogContent.addClassNames("transaction-dialog-content-box");
        paymentMethodSelector.setItems(ComponentLoader.getPaymentMethods());
        paymentMethodSelector.addClassName("payment-method-selector");
        paymentMethodSelector.setValue("Cash");
    }

    public void paymentDialog() {
        dialog = new CustomDialog();
        String title = String.format("Make Payment for Job No: %s", data.jobNo());
        String subtitle = "Proceed to finalize service and job completion by collecting payment";
        dialog.defaultDialogBox(dialogContent, title, subtitle);

        var balanceLabel = new Span("Account Balance");
        var balanceValue = new H4("Ghc" + data.account_balance());
        var balanceDiv = new Div(balanceLabel, balanceValue);
        balanceDiv.addClassNames("payment-inner-divs");

        Span totalLabel = new Span("Service Cost");
        var serviceAndItemsBill = data.serviceCost() + data.itemsCost();
        var totalBalance = new H4("Ghc" + serviceAndItemsBill);
        Div totalDiv = new Div(totalLabel, totalBalance);
        totalDiv.addClassNames("payment-inner-divs");

        FormLayout totalAndBalanceDiv = new FormLayout(totalDiv, balanceDiv);
        FormLayout.ResponsiveStep colOne = new FormLayout.ResponsiveStep("0", 1);
        FormLayout.ResponsiveStep colTwo = new FormLayout.ResponsiveStep("360px", 2);
        totalAndBalanceDiv.setResponsiveSteps(colOne, colTwo);
        totalAndBalanceDiv.addClassNames("total-and-balance-div");
        totalAndBalanceDiv.setWidthFull();
        totalAndBalanceDiv.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        totalAndBalanceDiv.getStyle().setAlignItems(Style.AlignItems.CENTER);

        NumberField labourField = new NumberField("Labour Cost", "0.00");
        labourField.setPrefixComponent(LineAwesomeIcon.WALLET_SOLID.create());
        labourField.setRequired(true);
        labourField.setInvalid(labourField.isEmpty());
        labourField.setErrorMessage("Provide Labour Cost");
        labourField.addClassNames("input-style", "labour-cost-input");

        TextField referenceField = new TextField("Reference(Optional)", "Provide transaction id or account number if momo was used.");
        referenceField.addClassNames("input-style", "reference-field");

        actionButton = new Button("Make Payment", VaadinIcon.MONEY_EXCHANGE.create());
        actionButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);

        NumberField payableField = new NumberField("Payable", "0.00");
        payableField.setValue(serviceAndItemsBill);
        payableField.setPrefixComponent(new H4("Ghc"));
        payableField.addClassNames("input-style", "payable-field");
        payableField.setReadOnly(true);

        dialogContent.add(paymentMethodSelector, totalAndBalanceDiv, payableField, labourField, referenceField, new Hr(), actionButton);

        //ACTION EVENT LISTENERS...
        labourField.setValueChangeMode(ValueChangeMode.EAGER);
        labourField.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                payableField.setValue(serviceAndItemsBill);
            } else {
                var sumValue = e.getValue() + serviceAndItemsBill;
                payableField.setValue(sumValue);
            }
        });

        actionButton.addSingleClickListener(e -> {
            var paymentMethodValue = paymentMethodSelector.getValue();
            var paymentSelector = !Objects.equals(paymentMethodValue, "Cash") && referenceField.isEmpty();
            if (paymentSelector) {
                referenceField.setInvalid(true);
                referenceField.setErrorMessage("Please provide transaction id as reference number");
                return;
            }

            //collect form data and parse them as arguments to processFormData() method.
            //compute payable and account balance values...
            double accountDebitValue = data.account_balance() - payableField.getValue();
            AtomicDouble finalDebitValue = new AtomicDouble(accountDebitValue < 0 ? 0.00 : accountDebitValue);
            double accountPayableValue = payableField.getValue();
            String reference = referenceField.getValue();
            String payMethod = paymentMethodSelector.getValue();
            double labourValue = labourField.getValue();

            var updateDataProvider = new TransactionsDataProvider.UpdateRecord(
                    data.jobId(), data.serviceId(), data.customerId(), finalDebitValue.get(), serviceAndItemsBill,
                    accountPayableValue, labourValue, reference, payMethod
            );
            //set dataProvider to the method
            UI ui = UI.getCurrent();
            ui.access(() -> {
                dialog = new CustomDialog();
                var confirmDialog = dialog.showSaveDialog("MAKE PAYMENT", MessageLoaders.confirmationMessage("Confirm job payment"));
                confirmDialog.addConfirmListener(cl -> {
                    int staus = DAO_MODEL.updateServiceTransaction(updateDataProvider);
                    Notification.show("status " + staus);
                    var logMsg = new NotificationRecords("JOB PAYMENT", String.format("Payment transaction for job no: %s has been processed and successful", data.jobNo()), 1, 1);
                    if (staus > 0) {
                        DAO_MODEL.logNotification(logMsg);
                        ui.refreshCurrentRoute(false);
                    } else
                        dialog.showErrorNotification(MessageLoaders.errorMessage("Failed to process job payment. Refresh and retry"));
                });
            });
        });
    }

    public void receiptDialog(int jobId) {
        var paymentData = DAO_MODEL.getPaymentDetails().stream().filter(x -> jobId == x.jobId()).toList();
       var filtererResult = paymentData.getFirst();

        // Receipt Details
        var paymentStatus = new Span(data.status().toUpperCase());
        var statusContainer = new HorizontalLayout(new Span("Payment status"), paymentStatus);
        statusContainer.setWidthFull();statusContainer.setPadding(true);
        statusContainer.getStyle().setAlignItems(Style.AlignItems.CENTER).setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        paymentStatus.getElement().getThemeList().add("badge pill success primary small");

        var customerName = new Paragraph(String.format("Customer Name: %s", data.customerName()));
        var serviceType = new Paragraph(String.format("Service Type: %s", data.serviceType()));
        var paymentMethod = new Paragraph("Payment Method: " +filtererResult.payMethod()); // Example payment method
        var paymentDate = new Paragraph("Date: " + DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(filtererResult.date().toLocalDateTime()));
        var serviceCost = new Paragraph("Service + Items: Ghc" + filtererResult.ServiceCost());
        var totalAmount = new H4(String.format("Total Amount Paid: Ghc%s", filtererResult.totalCost()));
        var discountApplied = new Paragraph("Discount Applied: Ghc" + filtererResult.discount()); // Example discount
        var receiptNo = new Paragraph("Receipt No: "+ filtererResult.receiptNo()); // Example receipt number
        var labourCost = new H6("Labour Cost: Ghc" + filtererResult.labourCost()); // Example labour cost

//        var image = new Image("images/logo-icon.png", "img");
        // Styling
        customerName.addClassNames("receipt-detail");
        serviceType.addClassNames("receipt-detail");
        paymentDate.addClassNames("receipt-detail");
        paymentMethod.addClassNames("receipt-detail");
        totalAmount.addClassNames("receipt-detail");
        discountApplied.addClassNames("receipt-detail");
        receiptNo.addClassNames("receipt-detail");
        labourCost.addClassNames("receipt-detail", "labour-cost-component");
        var printButton = new Button("Print", LineAwesomeIcon.PRINT_SOLID.create());
        printButton.setWidthFull();
        printButton.addClassNames("default-button-style");

        // Layout
        var receiptLayout = new VerticalLayout(
                statusContainer,
                new Hr(),
                receiptNo,
                paymentDate,
                customerName,
                serviceType,
                paymentMethod,
                discountApplied,
                serviceCost,
                labourCost,
                totalAmount,
                new Hr(),
                printButton
        );
        receiptLayout.addClassNames("receipt-layout");
        receiptLayout.setSpacing(false);
        receiptLayout.setPadding(true);

        // Add to dialog
        dialogContent.add(receiptLayout);
        dialog = new CustomDialog();
        dialog.defaultDialogBox(dialogContent, "Payment Receipt: #" + filtererResult.receiptNo(), "Payment receipt extracted by job status");
    }

    public void feedbackDialog(String jobNumber, int jobId) {
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
    }

}//end of class...
