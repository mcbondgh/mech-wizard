package com.mech.app.configfiles.secutiry;

//import com.ucmas.views.SessionExpiredView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.VaadinSession;

public class SessionManager {
    public static final  int DEFAULT_USER_ID = 1;
    public static  final int DEFAULT_SHOP_ID = 1;

    public static void setAttribute(String key, Object value) {
        VaadinSession.getCurrent().setAttribute(key, value);
    }

    public static Object getAttribute(String key) {
        return VaadinSession.getCurrent().getAttribute(key);
    }

    public static void destroySession() {
//        System.out.println(VaadinSession.getCurrent().getSession().getMaxInactiveInterval());
        VaadinSession.getCurrent().getSession().invalidate();
    }

    public static void validateSession(BeforeEnterEvent event) {
        try {
            SessionManager.getAttribute("userId").toString();
        } catch (NullPointerException e) {
            var currentRoute = event.getLocation();
            event.forwardTo("/login");
//            new SessionExpiredView();
        }
    }
    public static String accessControl(String route, String query) {
       return "/" + route + "?access=" + query;
    }

}//end of class...
