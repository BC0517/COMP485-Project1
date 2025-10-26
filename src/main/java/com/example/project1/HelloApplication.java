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

class Friend {
    private String username;
    private boolean status;
    private Timer timer;

    public Friend(String username, boolean status){
        this.username = username;
        this.status = status;
    }

    public String getUsername () {
        return username;
    }

    public boolean getStatus() {
        return status;
    }

    public void changeStatus (boolean status){
        this.status = status;
    }

    public void resetTimer(TimerTask task) {
        if (timer != null) timer.cancel();
        timer = new Timer(true);
        timer.schedule(task, 10000); // 10 seconds of inactivity → mark offline
    }
}

//Creating Login Form
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        //Creating Label Username
        Text lbl1 = new Text("Username");

        //Create a Label Password
        Text lbl2 = new Text("Password");

        //Create Text field for Username and password
        TextField text1 = new TextField();
        PasswordField text2 = new PasswordField();

        //Create buttons
        Button btn1 = new Button("Submit");
        Button btn2 = new Button("Clear");

        //On submit button click move to another window
        btn1.setOnAction(e -> {
            String username = text1.getText().trim();
            String password = text2.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please enter both username and password.");
                return;
            }

            try {
                // Try to connect to server and authenticate
                ChatClient client = new ChatClient("localhost", 12345, username, password, msg -> {
                    Platform.runLater(() -> {
                        // Once logged in, this will be used in message window
                    });
                });
                // If successful, open message window and close login
                stage.close();
                message(username, password);
            } catch (Exception ex) {
                showAlert("Login Failed", "Invalid username/password or server not running.");
            }
        });

        //Clear Text field when clicking clear button
        btn2.setOnAction(e -> {
            text1.clear();
            text2.clear();
        });

        //Create a grid pane
        GridPane gridPane = new GridPane();

        //Size settings for pane
        gridPane.setMaxSize(400, 400);

        //Padding size for pane
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Horizontal and vertical gaps between columns
        gridPane.setHgap(25);
        gridPane.setVgap(25);

        //Grid Alignment
        gridPane.setAlignment(Pos.CENTER);

        //arrange all nodes in the grid (node, column, row)
        gridPane.add(lbl1, 0, 0); //Username Label
        gridPane.add(text1, 1, 0); //Username text field
        gridPane.add(lbl2, 0, 1); //Password Label
        gridPane.add(text2, 1, 1); //Password text field
        gridPane.add(btn1, 0, 2); //Submit Button
        gridPane.add(btn2, 1, 2); //Clear Button

        //Set Min Height and width for window
        stage.setMinHeight(300);
        stage.setMinWidth(400);

        //Styling nodes
        btn1.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        btn2.setStyle("-fx-background-color: blue; -fx-text-fill: white;");

        text1.setStyle("-fx-font: normal bold 20px 'serif'");
        text2.setStyle("-fx-font: normal bold 20px 'serif'");

        //Create a scene
        Scene scene = new Scene(gridPane);
        //Setting Title to Stage
        stage.setTitle("Login Page");

        //Add scene to page
        stage.setScene(scene);

        //Display contents of the stage
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

    // Message window (Chat)
    private void message(String username, String password) {
        Stage newStage = new Stage();
        newStage.setTitle("Message");

        newStage.initModality(Modality.APPLICATION_MODAL); // Prevent switching windows

        // Chat UI setup \
        Text sender = new Text("From: " + (username));

        //Replace Recipients with the following
        VBox friendList = new VBox();
        List<Friend> list = new ArrayList<>();

        Friend allFriend = new Friend("All", true);
        ObjectProperty<Friend> selectedFriend = new SimpleObjectProperty<>(allFriend);

        Text allLabel = new Text("All");
        allLabel.setStyle("-fx-font-weight: bold; -fx-fill: black;");
        TextFlow allFlow = new TextFlow(allLabel);
        HBox allRow = new HBox(10, allFlow);
        allRow.setStyle("-fx-background-color: lightblue;"); // Default selected
        friendList.getChildren().add(allRow);

        allRow.setOnMouseClicked(e -> selectedFriend.set(allFriend));

        selectedFriend.addListener((obs, oldVal, newVal) -> {
            if (newVal == allFriend) {
                allRow.setStyle("-fx-background-color: lightblue;");
            } else {
                allRow.setStyle("-fx-background-color: lightgray;");
            }
        });


        list.add(new Friend("Alice", false));
        list.add(new Friend("Bob", false));
        list.add(new Friend("Charlie", false));

        Map<String,Text> nameLabels = new HashMap<>();
        for (Friend curr : list) {
            Text name = new Text(curr.getUsername());
            nameLabels.put(curr.getUsername(), name);

            TextFlow txt = new TextFlow(name);
            HBox friend = new HBox(10, txt);
            friendList.getChildren().add(friend);

            friend.setStyle("-fx-background-color: lightgray;"); // Default background color

            friend.setOnMouseEntered(event -> {
                if (selectedFriend.get() != curr){
                    friend.setStyle("-fx-background-color: lightyellow;"); // Change on hover
                }
            });

            friend.setOnMouseExited(event -> {
                if (selectedFriend.get() != curr){
                    friend.setStyle("-fx-background-color: lightgray;"); // Change on hover
                }
            });

            friend.setOnMouseClicked(e ->{
                selectedFriend.set(curr);
            });

            selectedFriend.addListener((obs, oldVal, newVal) -> {
                if (newVal == curr) {
                    friend.setStyle("-fx-background-color: lightblue;");
                } else {
                    friend.setStyle("-fx-background-color: lightgray;");
                }
            });
        }

        HBox topLayer = new HBox(10, sender);
        //HBox topLayer = new HBox(10, sender);

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

        // Connect client to server
        ChatClient client;
        try {
            client = new ChatClient("localhost", 12345, username, password, incoming -> {
                Platform.runLater(() -> {
                    System.out.println("Received: [" + incoming + "]");
                    Text msgText = new Text(incoming);
                    msgText.setFont(Font.font("Helvetica", 18));
                    TextFlow flow = new TextFlow(msgText);
                    HBox hbox = new HBox(flow);


                    // Detect sender name (before the first colon)
                    String senderName = null;
                    if (incoming.contains(":")) {
                        // If it's a private message, cut before " (private):"
                        if (incoming.contains("(private):")) {
                            senderName = incoming.substring(0, incoming.indexOf(" (private):")).trim();
                        } else {
                            senderName = incoming.substring(0, incoming.indexOf(":")).trim();
                        }
                    }

                    final String senderFinal = senderName;
                    // ----- Message alignment & color -----
                    if (senderName != null && senderName.equals(username)) {
                        msgText.setFill(Color.RED);
                        hbox.setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        msgText.setFill(Color.BLUE);
                        hbox.setAlignment(Pos.CENTER_LEFT);
                    }

                    // ----- Online status detection -----
                    if (senderName != null && !senderName.equals(username)) {
                        for (Friend curr : list) {
                            if (curr.getUsername().equals(senderName)) {
                                curr.changeStatus(true);
                                Text label = nameLabels.get(senderName);
                                if (label != null) {
                                    label.setStyle(null);
                                    label.setStyle("-fx-font-weight: bold; -fx-fill: black;");
                                }

                                // Restart inactivity timer for this friend
                                TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Platform.runLater(() -> {
                                            curr.changeStatus(false);
                                            Text label2 = nameLabels.get(senderFinal);
                                            if (label2 != null) {
                                                label2.setStyle(null);
                                                label2.setStyle("-fx-font-weight: normal; -fx-fill: grey;");
                                            }
                                            System.out.println("Marked " + senderFinal + " as OFFLINE (inactivity)");
                                        });
                                    }
                                };
                                curr.resetTimer(task);
                            }
                        }
                    }

                    chatlog.getChildren().add(hbox);
                    chatScroll.setVvalue(1.0);
                });
            });
        } catch (Exception e) {
            showAlert("Error", "Unable to connect to chat server.");
            return;
        }

        // Message send button
        submit.setOnAction(_e -> {
            //String picked = recipients.getValue();
            String picked = selectedFriend.get().getUsername();
            String msg = textMSG.getText();

            if (msg == null || msg.isEmpty()) return;

            if ("All".equals(picked)) {
                client.sendMessage(msg);
            } else {
                client.sendMessage("/pm " + picked + " " + msg);
            }

            TextFlow msgFlow = new TextFlow(new Text(username + ": " + msg));
            HBox msgBox = new HBox(msgFlow);
            msgBox.setAlignment(Pos.CENTER_RIGHT);
            chatlog.getChildren().add(msgBox);

            textMSG.clear();
            chatScroll.setVvalue(1.0);
        });

        newStage.setOnCloseRequest(e -> client.disconnect());

        BorderPane layout = new BorderPane();
        layout.setTop(topLayer);
        layout.setCenter(chatScroll);
        layout.setBottom(inputBar);
        layout.setLeft(friendList);

        Scene scene = new Scene(layout, 600, 360);
        newStage.setScene(scene);
        newStage.setResizable(true);
        newStage.show();
    }
}
