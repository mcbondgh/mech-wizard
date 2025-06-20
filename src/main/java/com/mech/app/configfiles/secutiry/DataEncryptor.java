package com.mech.app.configfiles.secutiry;

import com.mech.app.configfiles.database.AppConnection;
import com.mech.app.configfiles.database.AppProperties;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Base64;

public class DataEncryptor extends AppProperties {

        private static final String ALGORITHM = "AES";
        private static final int KEY_SIZE = 128;


        // Hash a password
        public static String hashPassword(String plainPassword) {
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
            return hashedPassword;
        }

        // Verify a password
        public static boolean verifyPassword(String plainPassword, String hashedPassword) {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        }


        public static String generateCipherText(String userInput) {
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(userInput.getBytes());
        }

        public static String getOriginalText(String hashCode) {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] hashedByte = decoder.decode(hashCode);
            return new String(hashedByte);
        }

        public static String defaultPassword() {
            var plainPassword = itemProp().getProperty("default.password");
            return hashPassword(plainPassword);
        }



        // public static void main(String[] args) {
        //     String var1 = DataEncryption.generateCipherText(LocalDate.now().plusDays(10).toString());
        //     System.out.println(var1);
        //     String var2 = DataEncryption.getOriginalText(var1);
        //     System.out.println("Original " + var2);
        // }
    }// end of class...
