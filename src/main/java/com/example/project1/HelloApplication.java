package com.example.project1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
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
        Add a chat history log and place message box below like standard messengers
        Chat History should be saved when switching to a new user and
            presented as saved when switching back to that recipient
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

        //Create Text for Labels
        Text user = new Text("From: " + (username));
        Text recipientLabel = new Text("Recipient:");

        // Simple recipient chooser
        ComboBox<String> recipients = new ComboBox<>();
        recipients.getItems().addAll("Alice", "Bob", "Charlie");
        recipients.setValue("Alice"); // default

        TextArea textMSG = new TextArea();
        textMSG.setWrapText(true);
        textMSG.setMaxWidth(Double.MAX_VALUE);
        textMSG.setMaxHeight(Double.MAX_VALUE);

        Button submit = new Button("Submit");
        Button clear = new Button("Clear");

        clear.setOnAction(_e -> textMSG.clear());

        submit.setOnAction(_e -> {
            String picked = recipients.getValue();
            String msg = textMSG.getText();

            if (picked == null || picked.isEmpty()) {
                // minimal validation
                System.out.println("No recipient selected.");
                return;
            }

            // small confirmation window
            Stage confirm = new Stage();
            confirm.setTitle("Sent");
            confirm.initOwner(newStage);                    //set new window as child of message window
            confirm.initModality(Modality.WINDOW_MODAL);    //Block access to message window until this window closes

            Text confirmation = new Text("'" + picked + "' received the following message: '" + msg + "'");
            confirmation.wrappingWidthProperty().set(380);  //Text wrapping == no long horizontal line for message

            GridPane confirmPane = new GridPane();
            confirmPane.setPadding(new Insets(10));
            confirmPane.setAlignment(Pos.CENTER);
            confirmPane.add(confirmation, 0, 0);

            Scene confirmScene = new Scene(confirmPane, 420, 120);
            confirm.setScene(confirmScene);
            confirm.setResizable(false);
            confirm.show();

            System.out.println("Sent to " + picked + ": " + msg);
        });

        // Layout for message window
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(15));
        gridPane.setHgap(12);
        gridPane.setVgap(12);
        gridPane.setAlignment(Pos.CENTER);

        // allow the text area column/row to grow
        ColumnConstraints c0 = new ColumnConstraints();
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        c1.setMinWidth(300);
        gridPane.getColumnConstraints().addAll(c0, c1);

        gridPane.add(user, 0, 0, 2, 1);
        gridPane.add(recipientLabel, 0, 1);
        gridPane.add(recipients, 1, 1);
        gridPane.add(textMSG, 0, 2, 2, 1);
        gridPane.add(submit, 0, 3);
        gridPane.add(clear, 1, 3);

        GridPane.setHgrow(textMSG, Priority.ALWAYS);
        GridPane.setVgrow(textMSG, Priority.ALWAYS);

        newStage.setMinWidth(420);
        newStage.setMinHeight(320);
        Scene scene = new Scene(gridPane, 600, 360);
        newStage.setScene(scene);
        newStage.setResizable(true);
        newStage.show();
    }


}