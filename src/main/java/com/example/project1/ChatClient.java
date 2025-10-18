package com.example.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Consumer<String> onMessage;

    public ChatClient(String host, int port, String username, Consumer<String> onMessage) throws IOException {
        this.username = username;
        this.onMessage = onMessage;

        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send username first
        out.println(username);
        out.flush(); // make sure it’s sent right away

        // Start listener AFTER sending username
        startListener();
    }

    private void startListener() {
        Thread listener = new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("RAW from server: " + msg);
                    String decrypted = SimpleEncryptor.decrypt(msg);
                    System.out.println("DECRYPTED: " + decrypted);
                    if (onMessage != null) {
                        onMessage.accept(decrypted);
                    }
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            } finally {
                try {
                    if (socket != null) socket.close();
                } catch (IOException ignored) {}
            }
        });
        listener.setDaemon(true);
        listener.start();
    }

    //  FIX: add this setter so HelloApplication can register its message callback
    public void setOnMessage(Consumer<String> onMessage) {
        this.onMessage = onMessage;
    }

    // Send encrypted message and force flush to ensure delivery
    public void sendMessage(String message) {
        if (message == null || message.trim().isEmpty()) return;
        out.println(SimpleEncryptor.encrypt(message));
        out.flush(); // make sure it’s sent immediately
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
