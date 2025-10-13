package com.example.project1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
            if(text1.getText() != null && !(text1.getText().isEmpty())){
                message(text1.getText());
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
        gridPane.setMaxSize(400,400);

        //Padding size for pane
        gridPane.setPadding(new Insets(10,10,10,10));

        //Horizontal and vertical gaps between columns
        gridPane.setHgap(25);
        gridPane.setVgap(25);

        //Grid Alignment
        gridPane.setAlignment(Pos.CENTER);

        //arrange all nodes in the grid (node, column, row)
        gridPane.add(lbl1,0,0); //Username Label
        gridPane.add(text1,1,0); //Username text field
        gridPane.add(lbl2,0,1); //Password Label
        gridPane.add(text2,1,1); //Password text field
        gridPane.add(btn1,0,2); //Submit Button
        gridPane.add(btn2,1,2); //Clear Button

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

    //Create Message Text field
    /*
    To Do:
        Modify chat log to have all messages sent be LEFT aligned
        To make it easer to know who sent a message the username will be turned red or blue
        Create option to register new friends
            - Call recipients.getItems().add("Dana") to add more recipients
            - Remove: recipients.getItems().remove("Bob"); — UI updates immediately.


    */

    // Message window: choose a recipient (up to 3), write a multi-line message,
    // and show a small confirmation window when submitted.
    private void message(String username) {
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
        recipients.getItems().addAll("Alice", "Bob", "Charlie");
        recipients.setValue("Alice"); // default

        //Create an Hbox to contain these elements
        HBox topLayer = new HBox(10,sender, recipientLabel, recipients);

        // MIDDLE AREA
        //Create Chat history
        VBox chatlog = new VBox(); // Vertical box to hold chat log
        chatlog.setPadding(new Insets(10)); //Set padding
        chatlog.setFillWidth(true); // Each child in Vbox will expand to fit available width

        //Make chat history scrollable
        ScrollPane chatScroll = new ScrollPane();
        chatScroll.setFitToWidth(true); // Allows chat log to be resized proportional ot the window
        chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Shows a scrollbar if chat exceeds capacity
        chatScroll.setContent(chatlog); //Add Chat history to ScrollPane

        // BOTTOM AREA

        TextField textMSG = new TextField();

        Button submit = new Button("Submit");

        // Creates a horizontal container to contain the messsage and submit button
        HBox inputBar = new HBox(10, textMSG, submit);
        inputBar.setPadding(new Insets(20));
        inputBar.setAlignment(Pos.CENTER); //Center position
        HBox.setHgrow(textMSG, Priority.ALWAYS); //Have the text box grow to fit container

        submit.setOnAction(_e -> {
            String picked = recipients.getValue();
            String msg = textMSG.getText();

            if (picked == null || picked.isEmpty()) {
                // minimal validation
                System.out.println("No recipient selected.");
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

            //Create a new container to hold every message sent
            HBox msgContainer = new HBox(msgFlow);
            msgContainer.setAlignment(Pos.CENTER_LEFT);

            //Add each message container to the Vbox
            chatlog.getChildren().add(msgContainer);


        });

        //Set the Layout of the window
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