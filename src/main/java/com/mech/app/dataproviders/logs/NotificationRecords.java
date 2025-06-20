package com.mech.app.dataproviders.logs;

public class NotificationRecords {
    private final String title;
    private final String content;
    private final int userId;
    private final int shopId;

    public NotificationRecords(String title, String content, int userId, int shopId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.shopId = shopId;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }

    public int userId() {
        return userId;
    }

    public int getShopId() {
        return this.shopId;
    }
}
