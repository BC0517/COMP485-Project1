package com.example.project1;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

/**
 * ChatClient connects to ChatServer and handles encrypted messaging.
 */
public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> messageHandler;

    public ChatClient(String host, int port, String username, String password, Consumer<String> onMessage) throws IOException {
        this.messageHandler = onMessage;
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Authenticate with server
        in.readLine(); // "Enter username:"
        out.println(username);
        in.readLine(); // "Enter password:"
        out.println(password);

        String response = in.readLine();
        if (!"LOGIN_SUCCESS".equals(response)) {
            socket.close();
            throw new IOException("Login failed: " + response);
        }

        // Start background listener
        Thread listener = new Thread(() -> {
            try {
                String incoming;
                while ((incoming = in.readLine()) != null) {
                    String decrypted = EncryptionUtil.decrypt(incoming);
                    messageHandler.accept(decrypted);
                }
            } catch (IOException ignored) {}
        });
        listener.setDaemon(true);
        listener.start();
    }

    public void sendMessage(String message) {
        out.println(EncryptionUtil.encrypt(message));
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
