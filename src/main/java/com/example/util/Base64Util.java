package com.example.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {
    private Base64Util() {}

    public static String encode(String text) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(data);
    }

    public static String decode(String encodedText) {
        byte[] decoded = Base64.getDecoder().decode(encodedText);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
