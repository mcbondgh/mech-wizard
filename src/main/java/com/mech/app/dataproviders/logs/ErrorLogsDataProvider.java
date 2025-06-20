package com.mech.app.dataproviders.logs;

public class ErrorLogsDataProvider {
    private String message;
    private String stackTrace;
    private String appInstance;
    private String timestamp;
    private long id;

    public ErrorLogsDataProvider(String message, String stackTrace, String appInstance) {
        this.message = message;
        this.stackTrace = stackTrace;
        this.appInstance = appInstance;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public String getAppInstance() {
        return appInstance;
    }
}
