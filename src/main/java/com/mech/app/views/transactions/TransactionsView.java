package com.mech.app.views.transactions;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Transactions")
@Route("transactions")
@Menu(order = 5, icon = LineAwesomeIconUrl.MONEY_CHECK_SOLID)
public class TransactionsView extends Composite<VerticalLayout> {

    public TransactionsView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
    }
}
