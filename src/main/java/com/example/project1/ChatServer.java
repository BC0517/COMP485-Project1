package com.example.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
private static final int PORT = 12345;
private static final Map<String, PrintWriter> clients = new ConcurrentHashMap<>();
public static void main(String[] args) {
    System.out.println("Chat server started on port " + PORT);
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

static class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private String username;

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);
            username = in.readLine(); // username is plain text
            if (username == null || username.isEmpty()) return;

            clients.put(username, out);
            System.out.println(username + " joined the chat.");
            broadcast("SERVER: " + username + " joined the chat.");
            sendOnlineUsers();

            String line;
            while ((line = in.readLine()) != null) {
                String msg = SimpleEncryptor.decrypt(line);
                if (msg == null || msg.isEmpty()) continue;

                if (msg.startsWith("/pm ")) {
                    String[] parts = msg.split(" ", 3);
                    if (parts.length == 3) {
                        sendPrivate(parts[1], "(Private) " + username + ": " + parts[2]);
                    }
                } else {
                    broadcast(username + ": " + msg);
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + username);
        } finally {
            if (username != null) {
                clients.remove(username);
                broadcast("SERVER: " + username + " left the chat.");
                sendOnlineUsers();
            }
        }
    }

    private void broadcast(String msg) {
        String encrypted = SimpleEncryptor.encrypt(msg);
        for (PrintWriter pw : clients.values()) {
            pw.println(encrypted);
            pw.flush();
        }
    }

    private void sendPrivate(String recipient, String msg) {
        PrintWriter target = clients.get(recipient);
        if (target != null) {
            target.println(SimpleEncryptor.encrypt(msg));
            target.flush();
        }
        PrintWriter sender = clients.get(username);
        if (sender != null) {
            sender.println(SimpleEncryptor.encrypt("(Private to " + recipient + "): " + msg));
            sender.flush();
        }
    }

    private void sendOnlineUsers() {
        StringBuilder sb = new StringBuilder("USERS:");
        for (String user : clients.keySet()) {
            sb.append(" ").append(user);
        }
        String encrypted = SimpleEncryptor.encrypt(sb.toString());
        for (PrintWriter pw : clients.values()) {
            pw.println(encrypted);
            pw.flush();
        }
    }
}
}