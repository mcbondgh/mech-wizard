package com.mech.app.configfiles;

import java.util.Optional;

public class MessageLoaders {

    public static String successMessage() {
        return "Nice! operation completed successfully.";
    }

    public static String errorMessage(String message) {
        return "Oops an error occurred during the operation. " + message;
    }
    public static String confirmationMessage(String action) {
        return "Are you sure you want to " + action + "? " + "Please CONFIRM to proceed, else CANCEL to abort.";
    }
}
