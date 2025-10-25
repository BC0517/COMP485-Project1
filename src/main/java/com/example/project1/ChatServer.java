package com.example.project1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

// Multi-client chat server with simple authentication
public class ChatServer {
    private static final int PORT = 12345;

    // username → password (simple local auth)
    private static final Map<String, String> USER_DB = new ConcurrentHashMap<>();

    // active clients
    private static final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        // Hardcoded users for demo (replace or load from file/DB later)
        USER_DB.put("Alice", EncryptionUtil.hash("123"));
        USER_DB.put("Bob", EncryptionUtil.hash("abc"));
        USER_DB.put("Charlie", EncryptionUtil.hash("pass"));

        System.out.println("ChatServer running on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast to all connected clients
    public static void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler c : clients) {
            if (c != exclude) c.send(message);
        }
    }

    // Send private message
    public static void sendPrivate(String recipient, String message) {
        for (ClientHandler c : clients) {
            if (c.getUsername().equalsIgnoreCase(recipient)) {
                c.send(message);
                break;
            }
        }
    }

    // Inner class for handling each client connection
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public String getUsername() {
            return username;
        }

        public void send(String msg) {
            out.println(msg);
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                out = new PrintWriter(socket.getOutputStream(), true);

                // First line: username
                username = in.readLine();
                String password = in.readLine();

                if (!authenticate(username, password)) {
                    out.println("AUTH_FAIL");
                    socket.close();
                    return;
                }

                out.println("AUTH_OK");
                clients.add(this);
                System.out.println(username + " joined the chat.");

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("/pm ")) {
                        String[] parts = line.split(" ", 3);
                        if (parts.length == 3)
                            sendPrivate(parts[1], username + " (private): " + parts[2]);
                    } else {
                        broadcast(username + ": " + line, this);
                    }
                }
            } catch (IOException e) {
                System.out.println(username + " disconnected.");
            } finally {
                clients.remove(this);
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private boolean authenticate(String user, String pass) {
            String stored = USER_DB.get(user);
            return stored != null && stored.equals(EncryptionUtil.hash(pass));
        }
    }
}
