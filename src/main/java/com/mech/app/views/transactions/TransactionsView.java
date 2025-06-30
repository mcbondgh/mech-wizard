package com.mech.app.views.transactions;

import com.mech.app.components.HeaderComponent;
import com.mech.app.components.transactions.CardComponent;
import com.mech.app.components.transactions.TransactionDialogs;
import com.mech.app.dataproviders.transactions.TransactionsDataProvider;
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
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@PageTitle("Transactions")
@Route(value = "transactions", layout = MainLayout.class)
//@Menu(order = 6, icon = LineAwesomeIconUrl.DOLLAR_SIGN_SOLID)
public class TransactionsView extends Composite<VerticalLayout> {

    private final Grid<TransactionsDataProvider.transactionRecord> transactionGrid = new Grid<>();

    public TransactionsView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
        getContent().add(pageBody());
    }


    /*******************************************************************************************************************
     REFERENCE METHODS
     *******************************************************************************************************************/
    private List<TransactionsDataProvider.transactionRecord> sampleData() {
        var first = new TransactionsDataProvider.transactionRecord("J0043", "John Atsu", "Toyota Corolla", "Oil Change", Date.valueOf(LocalDate.now()), "1,942.32", "paid");
        return List.of(
                first,
                new TransactionsDataProvider.transactionRecord("J001", "John Doe", "Toyota Corolla", "Oil Change", Date.valueOf(LocalDate.now()), "192.32", "paid"),
                new TransactionsDataProvider.transactionRecord("J002", "Jane Smith", "Honda Civic", "Brake Repair", Date.valueOf(LocalDate.now().minusDays(1)), "402.00", "unpaid"),
                new TransactionsDataProvider.transactionRecord("J003", "Mike Johnson", "Ford Focus", "Tire Replacement", Date.valueOf(LocalDate.now().minusDays(2)), "200.03", "paid"),
                new TransactionsDataProvider.transactionRecord("J004", "Emily Davis", "Chevrolet Malibu", "Battery Replacement", Date.valueOf(LocalDate.now().minusDays(3)), "102.22", "unpaid"),
                new TransactionsDataProvider.transactionRecord("J005", "Chris Brown", "Nissan Altima", "Engine Repair", Date.valueOf(LocalDate.now().minusDays(4)), "454.00", "paid"),
                new TransactionsDataProvider.transactionRecord("J006", "Sarah Wilson", "Mazda CX-5", "Transmission Repair", Date.valueOf(LocalDate.now().minusDays(5)), "2,343.00", "unpaid")
        );
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
        var sectionOne = new CardComponent().paymentTransactionsDashboardCard("Awaiting Payment", "5", "total jobs");
        var sectionTwo = new CardComponent().paymentTransactionsDashboardCard("Total Unpaid", "Ghc1,500.00", "outstanding balance");
        var sectionThree = new CardComponent().paymentTransactionsDashboardCard("Collected", "12", "paid amount");
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
                        var matchesServiceType = filter.service().toLowerCase().contains(input.getValue().toLowerCase());

                        return matchesName || matchesJobNo || matchesServiceType;
                    });
                }
        );
        return layout;
    }

    //GRID CONFIGURATION WITH COMPONENT RENDERERS
    private Grid<TransactionsDataProvider.transactionRecord> transactionsGridConfiguration() {
        transactionGrid.addClassNames("alternative-grid-style");
        transactionGrid.setSizeUndefined();
        transactionGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COLUMN_BORDERS);
        transactionGrid.setColumnReorderingAllowed(true);

        transactionGrid.addColumn(TransactionsDataProvider.transactionRecord::jobNo).setHeader("Job ID").setSortable(true);
        transactionGrid.addColumn(TransactionsDataProvider.transactionRecord::customerName).setHeader("Customer");
        transactionGrid.addColumn(TransactionsDataProvider.transactionRecord::car).setHeader("Vehicle");
        transactionGrid.addColumn(new LocalDateRenderer<>(data -> data.serviceDate().toLocalDate())).setHeader("Booked Date");
        transactionGrid.addColumn(TransactionsDataProvider.transactionRecord::service).setHeader("Service Type");
        transactionGrid.addColumn(TransactionsDataProvider.transactionRecord::amount).setHeader("Amount(Ghc)");
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
    private Renderer<TransactionsDataProvider.transactionRecord> transactionStatusRenderer() {
        return new ComponentRenderer<>(
                data -> {
                    Span badge = new Span(data.status());
                    badge.addClassName("badge");
                    if (data.status().equals("paid")) {
                        badge.getElement().getThemeList().add("badge success pill small");
                    } else {
                        badge.getElement().getThemeList().add("badge error pill small");
                       
                    }
                    return badge;
                });
    }

    private Renderer<TransactionsDataProvider.transactionRecord> gridActionButtonRenderer() {
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




