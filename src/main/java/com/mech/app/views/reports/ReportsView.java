package com.mech.app.views.reports;

import com.mech.app.components.HeaderComponent;
import com.mech.app.components.transactions.CardComponent;
import com.mech.app.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.LocalDate;
import java.util.Date;

import static com.vaadin.flow.component.Tag.H4;

@PageTitle("Reports")
@Route(value = "/reports", layout = MainLayout.class)
@PermitAll
public class ReportsView extends VerticalLayout implements BeforeEnterObserver {

    public ReportsView() {
        addClassName("page-view");
        setPadding(true);
        setSpacing(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    public void onAttach(AttachEvent event) {
        add(headerLayout(), pageBody());
    }

    private Component headerLayout() {
        var HeaderText = "Payment Transactions";
        var subText = "Process and view all payment transactions in your system";
        return new HeaderComponent().textHeader(HeaderText, subText);
    }

    public Component pageBody() {
        FormLayout formLayout = new FormLayout();
        FormLayout.ResponsiveStep oneCol = new FormLayout.ResponsiveStep("0", 1);
        FormLayout.ResponsiveStep fiveCol = new FormLayout.ResponsiveStep("768px", 5);
        formLayout.setResponsiveSteps(oneCol, fiveCol);
        formLayout.addClassNames("reports-body-form-layout");
        formLayout.add(filterSection(),1);
        formLayout.add(tabSheetSection(),4);

        VerticalLayout layout = new VerticalLayout(formLayout);
        layout.setClassName("page-body-content");
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        return layout;
    }

    /*******************************************************************************************************************
     COMPONENT RENDERERS...
     *******************************************************************************************************************/
    private Component filterSection() {
        H4 header = new H4("Report Date Range");
        Section parent = new Section( header, new Hr());
        parent.setWidthFull();
        parent.addClassNames("report-filter-div", "report-section");
        var startDatePicker = new DatePicker("Start Date", LocalDate.now());
        var endDatePicker = new DatePicker("End Date", LocalDate.now().plusWeeks(1));
        startDatePicker.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        endDatePicker.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        endDatePicker.setRequired(true);
        startDatePicker.setRequired(true);
        startDatePicker.setErrorMessage("Date is required");
        endDatePicker.setErrorMessage("Date is required");

        var button = new Button("Generate", LineAwesomeIcon.RECEIPT_SOLID.create(), e -> {

        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        button.addClassNames("default-button-style");
        parent.add(startDatePicker, endDatePicker, button);
        return parent;
    }

    private Component tabSheetSection() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_BORDERED,
                TabSheetVariant.LUMO_TABS_SMALL,TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS,
                TabSheetVariant.LUMO_TABS_MINIMAL);
        TextField filterField = new TextField("", "Filter by service name, job car or customer name...");

        var serviceTab = tabSheet.add("Services Report", servicesComponent());
        var jobsTab = tabSheet.add("Job Cards", jobCardsComponent());
        var transactionTab = tabSheet.add("Transactions", transactionsComponent());

        var filterSection = new HeaderComponent().searchFieldComponent(filterField);

        Section parent = new Section(filterSection, tabSheet);
        parent.setWidthFull();
        parent.addClassNames("tabs-section", "report-section");
        return parent;
    }

    private Component servicesComponent() {
        H4 headerText = new H4("Services Report");

        //create Grid here...

        Section parent = new Section(headerText);
        parent.setWidthFull();
        parent.addClassNames("tab-sheet-component");
        return parent;
    }

    private Component jobCardsComponent() {
        H4 headerText = new H4("Job Cards Report");

        //create Grid here...

        Section parent = new Section(headerText);
        parent.setWidthFull();
        parent.addClassNames("tab-sheet-component");
        return parent;
    }

    private Component transactionsComponent() {
     
        //create Grid here...
        var cardOne = new CardComponent().transactionReportCard(" Total Revenue", "Ghc0.00", "Collected amount by date range");
        cardOne.addClassName("trans-report-card");
        Section parent = new Section(cardOne);
        parent.setWidthFull();
        parent.addClassNames("tab-sheet-component");
        return parent;
    }

    /*******************************************************************************************************************
     REFERENCE METHODS IMPLEMENTATION
     *******************************************************************************************************************/


}//end of class...
