package org.example.savemate.util;

import java.io.*;
import java.util.Properties;

public class RememberMe {
    private static final String FILE = "rememberme.properties";

    public static void saveCredentials(String email, String password) {
        Properties props = new Properties();
        props.setProperty("email", email);
        props.setProperty("password", password);
        try (FileOutputStream fos = new FileOutputStream(FILE)) {
            props.store(fos, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] loadCredentials() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(FILE)) {
            props.load(fis);
            return new String[]{props.getProperty("email", ""), props.getProperty("password", "")};
        } catch (IOException e) {
            return new String[]{"", ""};
        }
    }
}