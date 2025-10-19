package com.example.project1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        Text recipientLabel = new Text("Recipient:");

        ComboBox<String> recipients = new ComboBox<>();
        recipients.getItems().addAll("All", "Alice", "Bob", "Charlie");
        recipients.setValue("All");

        HBox topLayer = new HBox(10, sender, recipientLabel, recipients);

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
                    Text msgText = new Text(incoming);
                    msgText.setFont(Font.font("Helvetica", 18));
                    TextFlow flow = new TextFlow(msgText);
                    HBox hbox = new HBox(flow);

                    if (incoming.startsWith(username + ":")) {
                        msgText.setFill(Color.RED);
                        hbox.setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        msgText.setFill(Color.BLUE);
                        hbox.setAlignment(Pos.CENTER_LEFT);
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
            String picked = recipients.getValue();
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

        Scene scene = new Scene(layout, 600, 360);
        newStage.setScene(scene);
        newStage.setResizable(true);
        newStage.show();
    }
}
