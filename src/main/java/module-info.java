module com.example.projetricochetrobot {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projetricochetrobot to javafx.fxml;
    exports com.example.projetricochetrobot;
}