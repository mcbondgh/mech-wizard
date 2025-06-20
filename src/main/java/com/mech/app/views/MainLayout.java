package com.mech.app.views;

import com.mech.app.specialmethods.ImageLoader;
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
import com.vaadin.flow.component.orderedlayout.Scroller;
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

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
//@Layout
@PermitAll
public class MainLayout extends AppLayout implements AfterNavigationObserver{

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
        addToNavbar(true,toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("");

        //LOGO
        var imageByte = ImageLoader.readLogoAsByte();
        var resource = new StreamResource("logo", ()-> new ByteArrayInputStream(imageByte));
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

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassName("drawer-footer");

        var NotificationIcon = LineAwesomeIcon.USER_CIRCLE.create();
        NotificationIcon.setSize("24px");

        H5 popoverHeader = new H5("Properties");
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOpenOnClick(true);

        contextMenu.add(popoverHeader, new Hr());
        contextMenu.setClassName("notification-icon-context-menu");
        var profileLink = contextMenu.addItem("Profile", e -> {
            Notification.show("Notification 1 clicked");
        });
        var settingsLink = contextMenu.addItem("Settings", e -> {
            Notification.show("Notification 2 clicked");
        });

        var signoutLink = new Anchor();
        signoutLink.setText(" Sign Out");
        signoutLink.addComponentAsFirst(LineAwesomeIcon.SIGN_OUT_ALT_SOLID.create());
        signoutLink.getStyle().setColor("black").setTextDecoration("none").setFontSize("small");
        signoutLink.getElement().addEventListener("click", e -> {
            signoutLink.setHref("/");
            contextMenu.setVisible(false);
        });
        contextMenu.add(signoutLink);

        NotificationIcon.addSingleClickListener(e -> {
            contextMenu.setOpenOnClick(true);
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
