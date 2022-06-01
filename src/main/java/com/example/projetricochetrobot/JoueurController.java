package com.example.projetricochetrobot;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class JoueurController {

    @FXML
    TextField nomTextField;

    @FXML
    public void miseEnPlace() {
        nomTextField.textProperty().bindBidirectional(Game.toto.joueurNom);
    }

    @FXML
    public void confirmer(ActionEvent actionEvent) {
        if ((nomTextField.getText() != null) && (! nomTextField.getText().isBlank())
        ){
            Game.toto.choixDuJoueur();
            // Fermeture de la boite de dialogue
            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
        }
    }
}
