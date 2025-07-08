package com.mech.app.dataproviders.logs;

import java.sql.Timestamp;
import java.time.Instant;

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
    public record LogsRecord(int recordId, String title, String content, Timestamp entryDate, int shopId) {

        public Instant formatDate() {
            return entryDate.toInstant();
        }
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
