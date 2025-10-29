package com.example.project1;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class ChatClient {
    private final String username;
    private final Consumer<String> messageHandler;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClient(String host, int port, String username, String password, Consumer<String> onMessage) throws IOException {
        this.username = username;
        this.messageHandler = onMessage;
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send username as first message
        out.println(username);

        // Start listener thread
        new Thread(this::listenForMessages).start();
    }

    private void listenForMessages() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (messageHandler != null) messageHandler.accept(line);
            }
        } catch (IOException e) {
            messageHandler.accept("Disconnected from server.");
        }
    }

    public void sendMessage(String msg) {
        if (out != null) out.println(msg);
        else System.out.println("Not connected to server.");
    }

    public void disconnect() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException ignored) {}
    }
}
