package com.example.project1;

/**
 * Simple Caesar cipher encryption/decryption for demonstration purposes.
 */
public class EncryptionUtil {
    private static final int SHIFT = 3;

    public static String encrypt(String msg) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : msg.toCharArray()) {
            encrypted.append((char) (c + SHIFT));
        }
        return encrypted.toString();
    }

    public static String decrypt(String msg) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : msg.toCharArray()) {
            decrypted.append((char) (c - SHIFT));
        }
        return decrypted.toString();
    }
}
