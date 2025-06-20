package com.mech.app.configfiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ErrorLoggerTemplate {
    private final String errorDate;
    private final String errorContent;
    private final String methodName;

    public ErrorLoggerTemplate(String errorDate, String errorContent, String methodName) {
        this.errorDate = errorDate;
        this.errorContent = errorContent;
        this.methodName = methodName;
    }

    public void logErrorToFile() {
        // Use a configurable or relative path
        String dirPath = System.getProperty("user.home") + File.separator + "Auto_Mechanic_Shop";
        String filePath = dirPath + File.separator + "error_log.txt";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("MECHANIC SHOP ERROR LOG FILE");
            writer.write("-------------------------------------------");
            writer.newLine();
            writer.write("Date: " + errorDate);
            writer.newLine();
            writer.write("Method: " + methodName);
            writer.newLine();
            writer.write("Error: " + errorContent);
            writer.newLine();
            writer.write("--------------------------------------------------");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
