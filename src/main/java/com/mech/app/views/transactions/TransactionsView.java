package com.mech.app.views.transactions;

import com.mech.app.components.HeaderComponent;
import com.mech.app.components.transactions.CardComponent;
import com.mech.app.components.transactions.TransactionDialogs;
import com.mech.app.dataproviders.transactions.TransactionsDataProvider;
import com.mech.app.models.TransactionsModel;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;
import java.util.Objects;

@PageTitle("Transactions")
@Route(value = "transactions", layout = MainLayout.class)
//@Menu(order = 6, icon = LineAwesomeIconUrl.DOLLAR_SIGN_SOLID)
public class TransactionsView extends Composite<VerticalLayout> {

    private final Grid<TransactionsDataProvider.TransactionRecord> transactionGrid = new Grid<>();
    private static final TransactionsModel DAO_OBJECT = new TransactionsModel();

    public TransactionsView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
        getContent().add(pageBody());
    }


    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/
    private List<TransactionsDataProvider.TransactionRecord> sampleData() {
        return DAO_OBJECT.fetchPaidAndUnpaidTransactions();
    }

    /*******************************************************************************************************************
     COMPONENT RENDERERS
     *******************************************************************************************************************/


    private Component headerLayout() {
        var HeaderText = "Payment Transactions";
        var subText = "Process and view all payment transactions in your system";
        var toggleButton = new Checkbox("Show Paid Jobs", true);
        toggleButton.addClassNames("check-box-style");

        toggleButton.addValueChangeListener(e -> {
            if (e.getValue()) {
                // Show both paid and unpaid invoices
                transactionGrid.setItems(sampleData());
            } else {
                // Show only unpaid invoices
                transactionGrid.setItems(sampleData().stream()
                        .filter(record -> "unpaid".equalsIgnoreCase(record.status()))
                        .toList());
            }
        });

        return new HeaderComponent().pageHeaderWithComponent(HeaderText, subText, toggleButton);
    }

    private Component cardHeaderLayout() {
        var dataObject = DAO_OBJECT.fetchTransactionsMetaData();

        //extract data objects
        var awaitingPayment = dataObject.getOrDefault("awaiting_payment", "0");
        var itemsCost = dataObject.getOrDefault("purchased_items_cost", "0.00");
        var paymentCount = dataObject.getOrDefault("payment_count", "0");
        var serviceCost = dataObject.getOrDefault("service_cost" , "0.00");
        var totalAmount = dataObject.getOrDefault("total_amount", "0.00");

        var sectionOne = new CardComponent().paymentTransactionsDashboardCard("Awaiting Payment", awaitingPayment, "Total Completed Jobs");
        var sectionTwo = new CardComponent().paymentTransactionsDashboardCard("Total Unpaid", totalAmount, "Outstanding Balance Without Labour Cost");
        var sectionThree = new CardComponent().paymentTransactionsDashboardCard("Collected", paymentCount, "Paid Amount");
        HorizontalLayout layout = new HorizontalLayout(sectionOne, sectionTwo, sectionThree);
        layout.setWidthFull();
        layout.addClassNames("payment-header-card-layout");
        return layout;
    }

    private Component gridLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addClassNames("transactions-grid-layout");

        var filterField = new TextField("", "Filter by job no, customer name or car number");
        var filterContainer = new HeaderComponent().searchFieldComponent(filterField);
        filterContainer.addClassNames("transaction-filter-container");

        layout.add(filterContainer, transactionsGridConfiguration());

        //action event listener
        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        filterField.addValueChangeListener(
                input -> {
                    transactionGrid.getListDataView().setFilter(filter -> {
                        if (input.getValue().isEmpty()) return true;
                        var matchesName = filter.customerName().toLowerCase().contains(input.getValue().toLowerCase());
                        var matchesJobNo = filter.jobNo().toLowerCase().contains(input.getValue().toLowerCase());
                        var matchesServiceType = filter.serviceType().toLowerCase().contains(input.getValue().toLowerCase());

                        return matchesName || matchesJobNo || matchesServiceType;
                    });
                }
        );
        return layout;
    }

    //GRID CONFIGURATION WITH COMPONENT RENDERERS
    private Grid<TransactionsDataProvider.TransactionRecord> transactionsGridConfiguration() {
        transactionGrid.addClassNames("alternative-grid-style");
        transactionGrid.setSizeUndefined();
        transactionGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COLUMN_BORDERS);
        transactionGrid.setColumnReorderingAllowed(true);

        transactionGrid.addColumn(TransactionsDataProvider.TransactionRecord::jobNo).setHeader("Job ID").setSortable(true);
        transactionGrid.addColumn(TransactionsDataProvider.TransactionRecord::customerName).setHeader("Customer");
        transactionGrid.addColumn(new LocalDateRenderer<>(data -> data.serviceDate().toLocalDate())).setHeader("Booked Date");
        transactionGrid.addColumn(TransactionsDataProvider.TransactionRecord::serviceType).setHeader("Service Type");
        transactionGrid.addColumn(transactionStatusRenderer()).setHeader("Status");
        transactionGrid.addColumn(gridActionButtonRenderer()).setHeader("Action");
        transactionGrid.getColumns().forEach(
                e -> {
                    e.setAutoWidth(true);
                    e.getStyle().setBackgroundColor("orange");
                }
        );
        transactionGrid.setItems(sampleData());

        return transactionGrid;
    }

    //GRID COMPONENT RENDERERS
    private Renderer<TransactionsDataProvider.TransactionRecord> transactionStatusRenderer() {
        return new ComponentRenderer<>(
                data -> {
                    var statusValue = Objects.equals("completed", data.status()) ? "unpaid" : data.status();
                    Span badge = new Span(statusValue);
                    badge.addClassName("badge");
                    if (statusValue.equals("paid")) {
                        badge.getElement().getThemeList().add("badge success pill small");
                    } else {
                        badge.getElement().getThemeList().add("badge error pill small");
                       
                    }
                    return badge;
                });
    }

    private Renderer<TransactionsDataProvider.TransactionRecord> gridActionButtonRenderer() {
        return new ComponentRenderer<>(
                data -> {
                    Button actionButton;

                    if ("paid".equalsIgnoreCase(data.status())) {
                        actionButton = new Button("Print Receipt", LineAwesomeIcon.RECEIPT_SOLID.create());
                        actionButton.addClassNames("print-receipt-button");
                        actionButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
                        actionButton.addSingleClickListener(e -> new TransactionDialogs(data).receiptDialog());
                    } else {
                        actionButton = new Button("Make Payment", LineAwesomeIcon.CREDIT_CARD.create());
                        actionButton.addClassNames("make-payment-button");
                        actionButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
                        actionButton.addSingleClickListener(e -> new TransactionDialogs(data).paymentDialog());
                    }
                    HorizontalLayout layout = new HorizontalLayout(actionButton);
                    layout.setWidthFull();
                    layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                    return layout;
                }
        );
    }

    /*******************************************************************************************************************
     PAGE BODY
     *******************************************************************************************************************/

    private Component pageBody() {
        VerticalLayout layout = new VerticalLayout(headerLayout(), cardHeaderLayout(), gridLayout());
        layout.setWidthFull();
        layout.setHeight("max-content");
        layout.addClassNames("job-card-page-layout");
        layout.setSizeUndefined();

        return layout;
    }

}//end of class...




