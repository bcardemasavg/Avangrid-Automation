package com.nttdata.utils;

import java.util.Base64;

public class Auth {
    public Auth() {
    }

    public static String getBasicAuth(String username, String password) {
        return "Basic " + getBase64fromString(username + ":" + password);
    }

    public static String getBase64fromString(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }
}