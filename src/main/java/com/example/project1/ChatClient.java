package com.example.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final Consumer<String> messageHandler;
    private Thread listenerThread;

    public ChatClient(String host, int port, String username, String password, Consumer<String> handler) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.messageHandler = handler;

        // send credentials
        out.println(username);
        out.println(password);

        String response = in.readLine();
        if (!"AUTH_OK".equals(response)) {
            throw new IOException("Authentication failed");
        }

        // start listening for messages
        listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    messageHandler.accept(line);
                }
            } catch (IOException e) {
                // closed connection
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
