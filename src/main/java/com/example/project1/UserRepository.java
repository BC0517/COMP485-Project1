// UserRepository.java
package com.example.project1;

import java.sql.*;

// Handles user registration and authentication
public class UserRepository {

    // Register a new user with encrypted password
    public static boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.toLowerCase());
            pstmt.setString(2, SimpleAES.encrypt(password));
            pstmt.executeUpdate();
            System.out.println("User registered: " + username);
            return true;
        } catch (SQLException e) {
            System.err.println("User registration failed: " + e.getMessage());
            return false;
        }
    }

    // Authenticate user by decrypting stored password
    public static boolean authenticate(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String encrypted = rs.getString("password");
                String decrypted = SimpleAES.decrypt(encrypted);
                return password.equals(decrypted);
            }
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return false;
    }
}
