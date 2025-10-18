package com.example.project1;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
private ChatClient client; // Added to link JavaFX UI with ChatClient
private ListView<String> userList; // Buddy list visible in whole class
private VBox chatlog; // Chat history area visible globally

// Predefined valid users and passwords
private static final Map<String, String> VALID_USERS = new HashMap<>();
static {
    VALID_USERS.put("alice", "1234");
    VALID_USERS.put("bob", "abcd");
    VALID_USERS.put("charlie", "pass");
}

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
    btn1.setOnAction(event -> {
        String username = text1.getText().trim().toLowerCase();
        String password = text2.getText().trim();

        //  STRICT LOGIN CHECK — only allow known usernames
        if (!VALID_USERS.containsKey(username)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unknown username. Please use alice, bob, or charlie.");
            alert.showAndWait();
            return;
        }

        // Check that password matches
        if (!VALID_USERS.get(username).equals(password)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect password.");
            alert.showAndWait();
            return;
        }

        // If both pass, proceed to connect
        try {
            client = new ChatClient("localhost", 12345, username, null);

            // Open chat window
            message(username, client);

            // Attach message handler
            client.setOnMessage(incoming -> Platform.runLater(() -> {
                System.out.println("Incoming: " + incoming);

                if (incoming.startsWith("USERS:")) {
                    if (userList != null) {
                        userList.getItems().clear();
                        userList.getItems().add("All");
                        String[] parts = incoming.split(" ");
                        for (int i = 1; i < parts.length; i++) {
                            userList.getItems().add(parts[i]);
                        }
                    }
                    return;
                }

                if (chatlog != null) {
                    Text msgText = new Text(incoming);
                    msgText.setFont(Font.font("Helvetica", 16));

                    if (incoming.contains(username + ":")) {
                        msgText.setFill(Color.RED);
                    } else if (incoming.startsWith("(Private")) {
                        msgText.setFill(Color.PURPLE);
                    } else if (incoming.startsWith("SERVER:")) {
                        msgText.setFill(Color.GREEN);
                    } else {
                        msgText.setFill(Color.BLUE);
                    }

                    TextFlow msgFlow = new TextFlow(msgText);
                    HBox msgContainer = new HBox(msgFlow);
                    msgContainer.setAlignment(Pos.CENTER_LEFT);
                    chatlog.getChildren().add(msgContainer);
                }
            }));

            // close login window
            stage.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not connect to server.");
            alert.showAndWait();
        }
    });

    //Clear Text field when clicking clear button
    btn2.setOnAction(ev -> {
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

// Message window: choose a recipient (up to 3), write a multi-line message,
// and show a small confirmation window when submitted.
private void message(String username, ChatClient client) {
    Stage newStage = new Stage();
    newStage.setTitle("Message");

    //Prevent User from looking at other window
    newStage.initModality(Modality.APPLICATION_MODAL); // optional: block owner

    // TOP AREA

    //Create Text for Labels
    Text sender = new Text("From: " + (username));
    Text recipientLabel = new Text("Recipient:");

    // Simple recipient chooser
    ComboBox<String> recipients = new ComboBox<>();
    recipients.getItems().add("All"); // Default "All" option
    recipients.setValue("All"); // default

    //  When user list updates, refresh recipients too
    userList = new ListView<>();
    userList.setPrefWidth(120);
    userList.setPlaceholder(new Label("No users online"));
    userList.getItems().add("All");

    // Create listener to keep recipients combo in sync
    userList.getItems().addListener((javafx.collections.ListChangeListener<String>) change -> {
        recipients.getItems().setAll(userList.getItems());
        recipients.setValue("All");
    });

    //Create an Hbox to contain these elements
    HBox topLayer = new HBox(10, sender, recipientLabel, recipients);

    // MIDDLE AREA
    chatlog = new VBox(); // Vertical box to hold chat log
    chatlog.setPadding(new Insets(10)); //Set padding
    chatlog.setFillWidth(true); // Each child in Vbox will expand to fit available width

    ScrollPane chatScroll = new ScrollPane();
    chatScroll.setFitToWidth(true);
    chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    chatScroll.setContent(chatlog);

    // BOTTOM AREA
    TextField textMSG = new TextField();
    Button submit = new Button("Submit");

    HBox inputBar = new HBox(10, textMSG, submit);
    inputBar.setPadding(new Insets(20));
    inputBar.setAlignment(Pos.CENTER);
    HBox.setHgrow(textMSG, Priority.ALWAYS);

    submit.setOnAction(_e -> {
        String picked = recipients.getValue();
        String msg = textMSG.getText();

        if (picked == null || msg.isEmpty()) {
            System.out.println("No recipient or message.");
            return;
        }

        //Change font color of username to RED
        Text userText = new Text(username + ": ");
        userText.setFill(Color.RED);
        userText.setFont(Font.font("Helvetica", 20));

        //Add msg to TEXT
        Text bodyText = new Text(msg);
        bodyText.setFill(Color.BLACK);
        bodyText.setFont(Font.font("Helvetica", 20));

        //Add TextFlow to bring it together
        TextFlow msgFlow = new TextFlow(userText, bodyText);

        HBox msgContainer = new HBox(msgFlow);
        msgContainer.setAlignment(Pos.CENTER_RIGHT);

        chatlog.getChildren().add(msgContainer);

        // Send to server (either broadcast or private)
        if (picked.equals("All")) {
            client.sendMessage(msg);
        } else {
            client.sendMessage("/pm " + picked + " " + msg);
        }

        textMSG.clear();
    });

    // Disconnect safely when closing chat window
    newStage.setOnCloseRequest(e -> {
        if (client != null) client.disconnect();
    });

    //Set the Layout of the window
    BorderPane layout = new BorderPane();
    layout.setTop(topLayer);
    layout.setCenter(chatScroll);
    layout.setLeft(userList);
    layout.setBottom(inputBar);

    BorderPane.setMargin(userList, new Insets(5));
    userList.setMinWidth(120);
    userList.setPrefWidth(150);
    userList.setMaxWidth(200);

    Scene scene = new Scene(layout, 800, 400);
    newStage.setScene(scene);
    newStage.setResizable(true);
    newStage.show();
}
}