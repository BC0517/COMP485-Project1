package com.example.project1;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("ChatServer running on port " + PORT);
        DatabaseManager.initializeDatabase(); // ensure DB & tables exist

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Each connected user gets one handler thread
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private String username;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                username = in.readLine(); // first line = username
                if (username == null) return;
                clients.put(username, this);

                broadcast("Server", username + " has joined the chat.");
                System.out.println(username + " connected.");

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("/pm")) {
                        String[] parts = line.split(" ", 3);
                        if (parts.length >= 3) {
                            String receiver = parts[1];
                            String message = parts[2];
                            privateMessage(username, receiver, message);
                            saveMessage(username, receiver, message);
                        }
                    } else {
                        broadcast(username, line);
                        saveMessage(username, "All", line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection lost: " + username);
            } finally {
                disconnect();
            }
        }

        // --- Send a private message ---
        private void privateMessage(String sender, String receiver, String msg) {
            ClientHandler target = clients.get(receiver);
            if (target != null) {
                target.out.println(sender + " (private): " + msg);
                out.println("To " + receiver + " (private): " + msg);
            } else {
                out.println("Server: " + receiver + " is not connected.");
            }
        }

        // --- Broadcast message to everyone ---
        private void broadcast(String sender, String msg) {
            for (ClientHandler ch : clients.values()) {
                ch.out.println(sender + ": " + msg);
            }
        }

        // --- Save message to SQLite ---
        private void saveMessage(String sender, String receiver, String content) {
            try (Connection conn = DatabaseManager.getConnection()) {
                String sql = "INSERT INTO messages (sender, receiver, content) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, sender);
                    stmt.setString(2, receiver);
                    stmt.setString(3, content);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.err.println("Error saving message: " + e.getMessage());
            }
        }

        // --- Handle user disconnect ---
        private void disconnect() {
            try {
                if (username != null) {
                    clients.remove(username);
                    broadcast("Server", username + " has left the chat.");
                }
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
