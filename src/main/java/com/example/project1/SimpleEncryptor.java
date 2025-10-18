package com.example.project1;

import java.util.Base64;

public class SimpleEncryptor {
private static final String KEY = "chatkey";
public static String encrypt(String message) {
    if (message == null) return "";
    char[] key = KEY.toCharArray();
    char[] msg = message.toCharArray();
    char[] result = new char[msg.length];
    for (int i = 0; i < msg.length; i++) {
        result[i] = (char) (msg[i] ^ key[i % key.length]);
    }
    return Base64.getEncoder().encodeToString(new String(result).getBytes());
}

public static String decrypt(String encrypted) {
    if (encrypted == null || encrypted.isEmpty()) return "";
    try {
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        String decodedStr = new String(decoded);
        char[] key = KEY.toCharArray();
        char[] msg = decodedStr.toCharArray();
        char[] result = new char[msg.length];
        for (int i = 0; i < msg.length; i++) {
            result[i] = (char) (msg[i] ^ key[i % key.length]);
        }
        return new String(result);
    } catch (IllegalArgumentException e) {
        // Username or other plain text (not Base64)
        return encrypted;
    }
}
}