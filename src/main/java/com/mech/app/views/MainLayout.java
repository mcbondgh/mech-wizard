package com.mech.app.views;

import com.mech.app.components.MenuBarButtons;
import com.mech.app.specialmethods.ImageLoader;
import com.mech.app.views.customers.CustomersView;
import com.mech.app.views.dashboard.CustomerDashboardView;
import com.mech.app.views.dashboard.DashboardView;
import com.mech.app.views.employees.EmployeesView;
import com.mech.app.views.jobcards.CompletedJobView;
import com.mech.app.views.jobcards.JobCardsView;
import com.mech.app.views.notifications.NotificationsView;
import com.mech.app.views.reports.ReportsView;
import com.mech.app.views.servicerequests.CustomerServiceView;
import com.mech.app.views.servicerequests.ServiceRequestsView;
import com.mech.app.views.settings.SettingsView;
import com.mech.app.views.transactions.TransactionsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import javax.sound.sampled.Line;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
//@Layout
@PermitAll
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        setClassName("page-body");
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("ALI-BOAT");
        appName.setClassName("app-name");
        //LOGO
        var imageByte = ImageLoader.readLogoAsByte();
        var resource = new StreamResource("logo", () -> new ByteArrayInputStream(imageByte));
        Image logoAvatar = new Image(resource, "LOGO");
        logoAvatar.addClassName("header-logo");

        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(logoAvatar, appName);
        header.addClassName("drawer-header");
        header.getStyle().setAlignItems(Style.AlignItems.CENTER);

        Scroller scroller = new Scroller(createNavigation());
        scroller.addClassName("drawer-scroller");
        scroller.getContent().getChildren().forEach(e -> {
            e.addClassNames("menu-item-link");
        });
        addToDrawer(header, scroller, createFooter());
    }

    //SIDE NAVS

    private List<SideNavItem> adminMenuItems() {
        var dashboard = new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create());
        var notification = new SideNavItem("Notifications", NotificationsView.class, LineAwesomeIcon.BELL.create());
        var customer = new SideNavItem("Customers", CustomersView.class, LineAwesomeIcon.USERS_COG_SOLID.create());
        var jobCard = new SideNavItem("Job Cards", JobCardsView.class, LineAwesomeIcon.ADDRESS_CARD.create());
        var completedJobs = new SideNavItem("Completed Jobs", CompletedJobView.class, LineAwesomeIcon.CHECK_CIRCLE_SOLID.create());
        var serviceRequests = new SideNavItem("Service Requests", ServiceRequestsView.class, LineAwesomeIcon.TOOLBOX_SOLID.create());
        var transactions = new SideNavItem("Transactions", TransactionsView.class, LineAwesomeIcon.DOLLAR_SIGN_SOLID.create());
        var feedbacks = new SideNavItem("Feedback", "views/feedbacks", LineAwesomeIcon.STAR.create());
        var settings = new SideNavItem("Settings", SettingsView.class, LineAwesomeIcon.TOOLS_SOLID.create());
        var employees = new SideNavItem("Employees", EmployeesView.class, LineAwesomeIcon.USER_ALT_SOLID.create());
//        var inventory = new SideNavItem("Inventory", "/dashboard", LineAwesomeIcon.BOOK_SOLID.create());
        var report = new SideNavItem("Reports", ReportsView.class, LineAwesomeIcon.RECEIPT_SOLID.create());

        Span alertCounter = new Span("15");
        alertCounter.addClassName("notification-counter");
        alertCounter.getElement().getThemeList().add("badge small error primary");
        notification.setSuffixComponent(alertCounter);

        return List.of(dashboard, notification, customer, jobCard, completedJobs,
                serviceRequests, transactions, employees, feedbacks, report, settings);
    }

    private List<SideNavItem> customerMenuItems() {
        var dashboard = new SideNavItem("My Dashboard", CustomerDashboardView.class, LineAwesomeIcon.DESKTOP_SOLID.create());
        var services = new SideNavItem("My Services", CustomerServiceView.class, LineAwesomeIcon.STAR_AND_CRESCENT_SOLID.create());
        return List.of(dashboard, services);
    }

    private List<SideNavItem> mechanicMenuItems() {
        List<SideNavItem> items = new ArrayList<>();

        return items;
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
//
//        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
//        menuEntries.forEach(entry -> {
//            if (entry.icon() != null) {
//                nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
//
//            } else {
//                nav.addItem(new SideNavItem(entry.title(), entry.path()));
//            }
//        });

        List<SideNavItem> allMenus = new ArrayList<>();
        allMenus.addAll(adminMenuItems());
        allMenus.addAll(customerMenuItems());
        allMenus.addAll(mechanicMenuItems());
        for (var item : allMenus) {
            nav.addItem(item);
        }
        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassName("drawer-footer");

        var NotificationIcon = LineAwesomeIcon.USER_CIRCLE.create();
        NotificationIcon.setSize("24px");

        H5 popoverHeader = new H5("Properties");
        popoverHeader.getStyle().setPadding("5px");
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOpenOnClick(true);

        var profileLink = new MenuBarButtons("Profile", LineAwesomeIcon.USER_CIRCLE).createMenuButton();
        var singoutLink = new MenuBarButtons("Sign Out", LineAwesomeIcon.SIGN_OUT_ALT_SOLID).createMenuButton();

        contextMenu.add(popoverHeader, new Hr());
        contextMenu.setClassName("notification-icon-context-menu");
        contextMenu.addItem(profileLink, e -> {
            UI.getCurrent().navigate("/user-profile");
        });
        contextMenu.addItem(singoutLink, e -> {
           UI.getCurrent().getPage().setLocation("/");
        });

        

        H4 username = new H4("Admin");
        FlexLayout header = new FlexLayout(NotificationIcon, username);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        header.setClassName("header");

        contextMenu.setOpenOnClick(true);
        contextMenu.setTarget(header);

        layout.add(header);
        return layout;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
