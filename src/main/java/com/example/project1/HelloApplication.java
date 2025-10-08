package com.example.project1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;


//Creating Login Form
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

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
}

//Create Message Text field
/*
    public void message(Stage stage) throws IOException {
        //Label for Text Message
        Text lbl1 = new Text("Message");

        //TextField for message
        TextField text1 = new TextField();

        Button btn1 = new Button("Submit");
        Button btn2 = new Button("Clear");
        GridPane gridPane = new GridPane();
    }


 */