module com.example.project1 {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.project1 to javafx.fxml;
    exports com.example.project1;
}