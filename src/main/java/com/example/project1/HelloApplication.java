package com.example.project1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
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
        btn1.setOnAction(e -> message(text1.getText()));

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
    Create a list of dummy friends to receive messages
    */

    public void message(String username){

        //Set Stage
        Stage newStage = new Stage();
        newStage.setTitle("Message");

        //Label for Text Message
        Text lbl1 = new Text("Message");

        //Label for username
        Text user = new Text("Username: " + username);

        //TextArea for multi line messages
        TextArea textMSG = new TextArea();
        textMSG.setWrapText(true);
        textMSG.setPrefRowCount(5);
        textMSG.setMaxWidth(Double.MAX_VALUE);        // allow horizontal growth
        textMSG.setMaxHeight(Double.MAX_VALUE);       // allow vertical growth


        //Have text field grow vertically

        //Create Submit and Clear buttons
        Button submit = new Button("Submit");
        Button clear = new Button("Clear");

        //Set actions for buttons
        submit.setOnAction(e -> {
            System.out.println("Typed Message: " + textMSG.getText());
            newStage.close();
        });

        clear.setOnAction(e-> textMSG.clear());

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
        gridPane.add(user,0,0); //Username Label
        gridPane.add(lbl1,0,1); //Message Label
        gridPane.add(textMSG,1,1); //Message text field
        gridPane.add(submit,0,2); //Submit Button
        gridPane.add(clear,1,2); //Clear Button

        //Set Min Height and width for window
        newStage.setMinHeight(300);
        newStage.setMinWidth(400);

        //Allow TextArea to expand within its cell
        GridPane.setHgrow(textMSG, Priority.ALWAYS);
        GridPane.setVgrow(textMSG, Priority.ALWAYS);


        //Set Stage
        Scene scene = new Scene(gridPane, 600, 300); // Wider and taller
        newStage.setScene(scene);

        newStage.show();
    }

}




