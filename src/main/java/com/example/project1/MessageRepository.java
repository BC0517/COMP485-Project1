package com.example.project1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    public static void saveMessage(String sender, String receiver, String content) {
        String sql = "INSERT INTO messages (sender, receiver, content) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setString(3, content);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
        }
    }

    public static List<Message> loadMessages(String user1, String user2) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT sender, receiver, content, timestamp
            FROM messages
            WHERE (sender = ? AND receiver = ?)
               OR (sender = ? AND receiver = ?)
            ORDER BY timestamp ASC;
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user1);
            pstmt.setString(2, user2);
            pstmt.setString(3, user2);
            pstmt.setString(4, user1);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message m = new Message(
                    rs.getString("sender"),
                    rs.getString("receiver"),
                    rs.getString("content"),
                    rs.getString("timestamp")
                );
                messages.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error loading messages: " + e.getMessage());
        }
        return messages;
    }
}
