package com.example.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChatServer handles multiple client connections.
 * Each client must authenticate with a valid username and password.
 * The server routes public and private messages and provides basic encryption.
 */
public class ChatServer {
    // Predefined users (username:password)
    private static final Map<String, String> VALID_USERS = Map.of(
        "Alice", "1234",
        "Bob", "abcd",
        "Charlie", "pass"
    );

    // Keeps track of connected clients
    private static final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 12345;
        System.out.println("Chat Server running on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    // Handles each client connection
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Step 1: Login
                out.println("Enter username:");
                out.flush();
                username = in.readLine();

                out.println("Enter password:");
                out.flush();
                String password = in.readLine();

                if (!VALID_USERS.containsKey(username) || !VALID_USERS.get(username).equals(password)) {
                    out.println("LOGIN_FAILED");
                    out.flush();
                    socket.close();
                    return;
                }

                if (clients.containsKey(username)) {
                    out.println("LOGIN_DUPLICATE");
                    out.flush();
                    socket.close();
                    return;
                }

                out.println("LOGIN_SUCCESS");
                out.flush();
                clients.put(username, this);
                broadcast(username + " joined the chat.", username);

                // Step 2: Listen for messages
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    inputLine = EncryptionUtil.decrypt(inputLine);

                    if (inputLine.startsWith("/pm")) {
                        // Private message format: /pm recipient message
                        String[] parts = inputLine.split(" ", 3);
                        if (parts.length == 3) {
                            sendPrivate(parts[1], username + " (private): " + parts[2]);
                        }
                    } else {
                        broadcast(username + ": " + inputLine, username);
                    }
                }

            } catch (IOException e) {
                System.err.println("Connection error for " + username);
            } finally {
                if (username != null) {
                    clients.remove(username);
                    broadcast(username + " left the chat.", username);
                }
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        private void broadcast(String message, String sender) {
            String encrypted = EncryptionUtil.encrypt(message);
            for (ClientHandler client : clients.values()) {
                if (!client.username.equals(sender)) {
                    client.out.println(encrypted);
                    client.out.flush();
                }
            }
        }

        private void sendPrivate(String recipient, String message) {
            ClientHandler target = clients.get(recipient);
            if (target != null) {
                target.out.println(EncryptionUtil.encrypt(message));
                target.out.flush();
            } else {
                out.println(EncryptionUtil.encrypt("User " + recipient + " not found."));
                out.flush();
            }
        }
    }
}
