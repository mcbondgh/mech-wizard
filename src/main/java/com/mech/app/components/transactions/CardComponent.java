package com.mech.app.components.transactions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.lineawesome.LineAwesomeIcon;

public class CardComponent {

    public Component paymentTransactionsDashboardCard(String title, String value, String subTitle) {
        var h4 = new H4(title);
        var h2 = new H2(value);
        var sub = new Paragraph(subTitle);
        h4.addComponentAsFirst(LineAwesomeIcon.MONEY_BILL_ALT_SOLID.create());
        VerticalLayout layout = new VerticalLayout(h4, h2, sub);
        layout.addClassNames("payment-transaction-card");
        return layout;
    }

    public Component transactionReportCard(String title, String value, String subTitle) {
        var h4 = new H4(title);
        var h2 = new H2(value);
        var sub = new Paragraph(subTitle);
        h4.addComponentAsFirst(LineAwesomeIcon.MONEY_BILL_ALT_SOLID.create());
        VerticalLayout layout = new VerticalLayout(h4, h2, sub);
        layout.addClassNames("transactions-report-card");
        return layout;
    }

}
