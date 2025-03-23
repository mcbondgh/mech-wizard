package com.mech.app.specialmethods;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageLoader {
    private static final String absolutePath = "src/main/resources/META-INF/resources/images/";
    private static String iconsPath =  "src/main/resources/META-INF.resources/icons/";

    public static String getLogoPath()  {
        return absolutePath.concat("logo-icon.png");
    }

    public static byte[] readFileAsBytes(String filePath) {
        byte[] imageByte;
        try {
            imageByte = Files.readAllBytes(Paths.get(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return imageByte;
    }

    public static byte[] readLogoAsByte() {
        byte[] imageByte;
        try {
            imageByte = Files.readAllBytes(Paths.get(absolutePath.concat("logo-icon.png")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return imageByte;
    }

}//end of class...
