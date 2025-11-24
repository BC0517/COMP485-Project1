// ChatClient.java
package com.example.project1;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

// Handles client-side connection to the chat server
public class ChatClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Constructor: connects to server, sends credentials, starts listener thread
    public ChatClient(String host, int port, String username, String password, Consumer<String> messageHandler) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Send username and password immediately to server
        out.println(username.toLowerCase());
        out.println(password);

        // Listen for incoming messages from server in a separate thread
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    // Decrypt incoming message and pass to handler
                    String decrypted = SimpleAES.decrypt(line);
                    messageHandler.accept(decrypted);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }).start();
    }

    // Send a message to the server (encrypted)
    public void sendMessage(String message) {
        out.println(SimpleAES.encrypt(message));
    }

    // Disconnect from the server
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
