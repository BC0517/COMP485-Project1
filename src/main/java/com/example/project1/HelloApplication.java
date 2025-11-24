package com.example.project1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

// Helper class to represent a friend
class Friend {
    private String username;
    private boolean status;
    private Timer timer;

    public Friend(String username, boolean status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() { return username; }
    public boolean getStatus() { return status; }
    public void changeStatus(boolean status) { this.status = status; }

    public void resetTimer(TimerTask task) {
        if (timer != null) timer.cancel();
        timer = new Timer(true);
        timer.schedule(task, 10000); // 10 seconds inactivity → offline
    }
}

// Main Application
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        // Initialize embedded database on startup
        DatabaseManager.initializeDatabase();
        
        // Create login labels
        Text lbl1 = new Text("Username");
        Text lbl2 = new Text("Password");

        // Create text fields for username and password
        TextField text1 = new TextField();
        PasswordField text2 = new PasswordField();

        // Create buttons
        Button btn1 = new Button("Submit");
        Button btn2 = new Button("Clear");

        // On submit button click
        btn1.setOnAction(e -> {
            String username = text1.getText().trim();
            String password = text2.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please enter both username and password.");
                return;
            }

            //  Remove auto-register, require authentication
            boolean authenticated = UserRepository.authenticate(username, password);
            if (!authenticated) {
                showAlert("Login Failed", "Invalid username/password.");
                return; // Stop login if not authenticated
            }

            // If authenticated, open chat window
            stage.close();
            message(username, password);
        });

        // Clear text fields
        btn2.setOnAction(e -> {
            text1.clear();
            text2.clear();
        });

        // Layout: grid pane
        GridPane gridPane = new GridPane();
        gridPane.setMaxSize(400, 400);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setHgap(25);
        gridPane.setVgap(25);
        gridPane.setAlignment(Pos.CENTER);

        // Add components
        gridPane.add(lbl1, 0, 0);
        gridPane.add(text1, 1, 0);
        gridPane.add(lbl2, 0, 1);
        gridPane.add(text2, 1, 1);
        gridPane.add(btn1, 0, 2);
        gridPane.add(btn2, 1, 2);

        // Style
        stage.setMinHeight(300);
        stage.setMinWidth(400);
        btn1.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        btn2.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        text1.setStyle("-fx-font: normal bold 20px 'serif'");
        text2.setStyle("-fx-font: normal bold 20px 'serif'");

        // Scene setup
        Scene scene = new Scene(gridPane);
        stage.setTitle("Login Page");
        stage.setScene(scene);
        stage.show();
    }

    // Utility method for showing alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

 
   private void message(String username, String password) {
    // Create a new Stage for the chat window
    Stage chatStage = new Stage();
    chatStage.setTitle("Chat: " + username);
    chatStage.initModality(Modality.NONE); // Non-blocking

    // Chat UI setup
    Text sender = new Text("From: " + username);

    VBox friendList = new VBox();
    List<Friend> list = new ArrayList<>();

    Friend allFriend = new Friend("All", true);
    ObjectProperty<Friend> selectedFriend = new SimpleObjectProperty<>(allFriend);

    Text allLabel = new Text("All");
    allLabel.setStyle("-fx-font-weight: bold; -fx-fill: black;");
    TextFlow allFlow = new TextFlow(allLabel);
    HBox allRow = new HBox(10, allFlow);
    allRow.setStyle("-fx-background-color: lightblue;");
    friendList.getChildren().add(allRow);
    allRow.setOnMouseClicked(e -> selectedFriend.set(allFriend));

    selectedFriend.addListener((obs, oldVal, newVal) -> {
        if (newVal == allFriend) allRow.setStyle("-fx-background-color: lightblue;");
        else allRow.setStyle("-fx-background-color: lightgray;");
    });

    list.add(new Friend("Alice", false));
    list.add(new Friend("Bob", false));
    list.add(new Friend("Charlie", false));

    Map<String, Text> nameLabels = new HashMap<>();
    for (Friend curr : list) {
        Text name = new Text(curr.getUsername());
        nameLabels.put(curr.getUsername(), name);

        TextFlow txt = new TextFlow(name);
        HBox friend = new HBox(10, txt);
        friendList.getChildren().add(friend);
        friend.setStyle("-fx-background-color: lightgray;");

        friend.setOnMouseEntered(event -> {
            if (selectedFriend.get() != curr)
                friend.setStyle("-fx-background-color: lightyellow;");
        });
        friend.setOnMouseExited(event -> {
            if (selectedFriend.get() != curr)
                friend.setStyle("-fx-background-color: lightgray;");
        });
        friend.setOnMouseClicked(e -> selectedFriend.set(curr));

        selectedFriend.addListener((obs, oldVal, newVal) -> {
            if (newVal == curr)
                friend.setStyle("-fx-background-color: lightblue;");
            else
                friend.setStyle("-fx-background-color: lightgray;");
        });
    }

    VBox chatlog = new VBox();
    chatlog.setPadding(new Insets(10));
    chatlog.setFillWidth(true);

    ScrollPane chatScroll = new ScrollPane();
    chatScroll.setFitToWidth(true);
    chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    chatScroll.setContent(chatlog);

    TextField textMSG = new TextField();
    Button submit = new Button("Submit");

    HBox inputBar = new HBox(10, textMSG, submit);
    inputBar.setPadding(new Insets(20));
    inputBar.setAlignment(Pos.CENTER);
    HBox.setHgrow(textMSG, Priority.ALWAYS);

    // Connect client
    final ChatClient[] client = new ChatClient[1];
    try {
        client[0] = new ChatClient("localhost", 12345, username.toLowerCase(), password, incoming -> {
            Platform.runLater(() -> {
                int colon = incoming.indexOf(":");
                String messenger = colon > 0
                        ? incoming.substring(0, colon).trim()
                        : incoming;

                String body = colon > 0
                        ? incoming.substring(colon + 1).trim()
                        : "";

                boolean isPrivate = messenger.endsWith("(private)");
                String normalized = isPrivate
                        ? messenger.replace("(private)", "").trim()
                        : messenger;

                String display = isPrivate && normalized.equalsIgnoreCase(username)
                        ? normalized + ": " + body
                        : incoming;

                Text msgText = new Text(display);
                msgText.setFont(Font.font("Helvetica", 18));
                msgText.setFill(normalized.equalsIgnoreCase(username)
                        ? Color.RED
                        : Color.BLUE);

                TextFlow flow = new TextFlow(msgText);
                HBox hbox = new HBox(flow);
                hbox.setAlignment(normalized.equalsIgnoreCase(username)
                        ? Pos.CENTER_RIGHT
                        : Pos.CENTER_LEFT);

                chatlog.getChildren().add(hbox);
                chatScroll.setVvalue(1.0);
            });
        });
    } catch (Exception e) {
        showAlert("Error", "Unable to connect to chat server.");
        return;
    }

    // Send message handler
    submit.setOnAction(_e -> {
        String picked = selectedFriend.get().getUsername();
        String msg = textMSG.getText();

        if (msg == null || msg.isEmpty()) return;

        if ("All".equals(picked)) {
            client[0].sendMessage(msg);
            MessageRepository.saveMessage(username, "All", msg);
        } else {
            client[0].sendMessage("/pm " + picked + " " + msg);
            MessageRepository.saveMessage(username, picked, msg);
        }
        textMSG.clear();
        chatScroll.setVvalue(1.0);

    });

    chatStage.setOnCloseRequest(e -> client[0].disconnect());

    BorderPane layout = new BorderPane();
    layout.setTop(new HBox(10, sender));
    layout.setCenter(chatScroll);
    layout.setBottom(inputBar);
    layout.setLeft(friendList);

    Scene scene = new Scene(layout, 600, 360);
    chatStage.setScene(scene);
    chatStage.setResizable(true);
    chatStage.show();
}}
