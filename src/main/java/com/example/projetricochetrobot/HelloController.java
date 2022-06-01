package com.example.projetricochetrobot;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.projetricochetrobot.Jeton.Couleur.*;
//import a faire

public class HelloController {

    public final int taille_Tuile = 40;

    @FXML
    public GridPane plateauDeJeu;

    @FXML
    private Label statusLabel;

    // "initialize()" est appelé par JavaFX à l'affichage de la fenêtre

    @FXML
    public void miseEnPlace() {

        messageErreur("Ricochet Robots");
        // Construction du plateau
        Image tuile = new Image("case.png", taille_Tuile, taille_Tuile, false, true );
        //"case.png" doit être placé à la racine de "resources/" (sinon c'est la cata)

        for (int colonne = 0; colonne < Game.taille; colonne ++) {
            for (int ligne = 0; ligne < Game.taille; ligne ++) {
                ImageView tuileLambda = new ImageView(tuile);
                final int lambdaColonne = colonne;
                final int lambdaLigne = ligne;

                tuileLambda.setOnMouseClicked(event -> {
                    String status = Game.toto.choixDeLaTuile(lambdaColonne, lambdaLigne);
                    if ( "mouvement".equals(status)) {
                        majMouvementRobot();
                    } else if (status != null) {
                        messageErreur(status);
                    }
                });
                plateauDeJeu.add(tuileLambda, colonne, ligne);
            }
        }

        // Ajout des pièces
        ajoutRobot(rouge);
        ajoutRobot(vert);
        ajoutRobot(bleu);
        ajoutRobot(jaune);

        plateauDeJeu.add(new ImageView( new Image( "target-" + Game.toto.getTarget().getCouleur() + ".png", taille_Tuile, taille_Tuile, false, true)),
                Game.toto.getTarget().getColonne(),Game.toto.getTarget().getLigne()
        );


        // "Binding JFX" - Synchronisation du "Label" avec l'état du jeu
        statusLabel.textProperty().bind(Game.toto.ajcf);
    }

    // Affiche une boite de dialogue construite avec "SceneBuilder"
    public void visionJoueur(ActionEvent actionEvent) throws IOException{
        if (Game.toto.getStatus() == Game.Status.choixDuJoueur) {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("joueur_view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
    }


    private void ajoutRobot(Jeton.Couleur couleur) {
        Jeton robot = Game.toto.getRobots().get(couleur);
        ImageView robotLambda = new ImageView(new Image("pion-" + couleur.name() + ".png", taille_Tuile, taille_Tuile, false, true ));
        robotLambda.setOnMouseClicked(event -> Game.toto.choixDuRobot(couleur));

        plateauDeJeu.add(robotLambda, robot.getColonne(),robot.getLigne());
        //Association avec le robot stocké
        robot.setLambda(robotLambda);
    }


    private void majMouvementRobot() {
        Jeton robot = Game.toto.getSelectedRobot();
        GridPane.setConstraints(robot.getLambda(), robot.getColonne(), robot.getLigne());
    }

    private void messageErreur(String message) {
        Alert debutMessage = new Alert(Alert.AlertType.INFORMATION, message);
        debutMessage.setHeaderText(null);
        debutMessage.setGraphic(null);
        debutMessage.showAndWait();
    }



}