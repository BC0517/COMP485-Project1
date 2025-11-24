// ChatServer.java
package com.example.project1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Handles multiple clients and message routing
public class ChatServer {

    // Map of online users: username → PrintWriter (to send messages)
    private final Map<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        new ChatServer().startServer();
    }

    // Start the server and listen for client connections
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Chat server started on port 12345...");

            // Accept clients continuously
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle each client in a separate thread
    private void handleClient(Socket socket) {
        String username = null;
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // First messages: username and password
            username = in.readLine().toLowerCase();
            String password = in.readLine();

            // Authenticate user
            if (!UserRepository.authenticate(username, password)) {
                out.println(SimpleAES.encrypt("SERVER: User not registered or wrong password. Disconnecting."));
                socket.close();
                return;
            }

            clients.put(username, out);
            System.out.println(username + " connected. Online users: " + clients.keySet());

            // Listen for messages
            String line;
            while ((line = in.readLine()) != null) {
                line = SimpleAES.decrypt(line).trim();

                // Private message handling
                if (line.startsWith("/pm ")) {
                    String[] parts = line.split(" ", 3);
                    if (parts.length < 3) {
                        out.println(SimpleAES.encrypt("SERVER: Invalid PM format."));
                        continue;
                    }

                    String targetUser = parts[1].toLowerCase();
                    String msg = parts[2];

                    PrintWriter targetOut = clients.get(targetUser);
                    if (targetOut == null) {
                        out.println(SimpleAES.encrypt("SERVER: User not found or offline."));
                    } else {
                        String formatted = username + "(private): " + msg;
                        targetOut.println(SimpleAES.encrypt(formatted));
                        out.println(SimpleAES.encrypt(formatted)); // show sender too
                    }

                } else {
                    // Broadcast to all online users
                    String formatted = username + ": " + line;
                    broadcast(formatted);
                }
            }

        } catch (IOException e) {
            System.out.println("Connection lost: " + username);
        } finally {
            // Remove client on disconnect
            if (username != null) {
                clients.remove(username);
                System.out.println(username + " disconnected. Online users: " + clients.keySet());
            }
        }
    }

    // Broadcast a message to all connected clients
    private void broadcast(String message) {
        String encrypted = SimpleAES.encrypt(message);
        for (PrintWriter writer : clients.values()) {
            writer.println(encrypted);
        }
    }
}
