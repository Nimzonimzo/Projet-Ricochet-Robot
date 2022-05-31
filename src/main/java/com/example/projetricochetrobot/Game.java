package com.example.projetricochetrobot;

//import

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import static com.example.projetricochetrobot.Game.Status.*;
import static com.example.projetricochetrobot.Jeton.Couleur.*;

public class Game {

    public static Game toto;

    public static void start() {
        if (Game.toto != null ) {
            throw new RuntimeException( "Tu peux faire qu'une partie à la fois^^");
        }
        Game.toto = new Game();
        Game.toto.setStatus(choixDuJoueur);

    }
     public static final int taille = 16;

    private Game () {
        board = new Tuile[taille][taille];
        robots = new HashMap<>();
        robots.put(rouge, new Jeton(rouge));
        robots.put(vert, new Jeton(vert));
        robots.put(bleu, new Jeton(bleu));
        robots.put(jaune, new Jeton(jaune));
        Jeton.Couleur[] couleurs = Jeton.Couleur.values();
        int randomCouleurIndex = ( new Random() ).nextInt( couleurs.length );
        target = new Jeton( couleurs[randomCouleurIndex] );

    }

    // * Gestion des événements du jeu

    public void processSelectPlayer() {
        if (this.status == choixDuJoueur) {
            // Action suivante attendue : choisir la case cible
            setStatus(choixDuRobot);
        }
    }

    public void processSelectRobot(Jeton.Couleur couleur) {
        if (this.status == choixDuRobot) {
            this.selectedRobot = this.robots.get(couleur);
            // Action suivante attendue : choisir la case cible
            setStatus(choixDeLaTuile);
        }
    }

    public String processSelectTile(int colonne, int ligne) {
        if (this.status == choixDeLaTuile) {
            if (
                    (this.selectedRobot.getColonne() != colonne) && (this.selectedRobot.getLigne() != ligne)
            ) {
                return "Ce n'est pas un fou";
            } else {
                this.selectedRobot.setPosition(colonne,ligne);

                // Action suivante choisir un robot
                setStatus(choixDuRobot);
                return "MOVE";
            }
        }
        return null;
    }


    // * Etat courant du jeu

    public enum Status {
        choixDuJoueur("Cliquez sur le bouton [Jouer]"),
        choixDuRobot("Cliquez sur le robot à déplacer"),
        CHOOSE_TILE("Cliquez sur la case destination");
        Status(String toolTip) { this.toolTip = toolTip; }
        private String toolTip;
        public String getToolTip() { return this.toolTip; }
    }
    public Status getStatus() { return status; }
    public void setStatus(Status status) {
        this.status = status;

        // Mise à jour du libellé d'état sur l'affichage
        StringBuilder statusMessage = new StringBuilder();
        if (playerNameProperty.get() != null) {
            statusMessage.append(playerNameProperty.get());
            statusMessage.append(" : ");
        }
        statusMessage.append( status.getToolTip() );
        this.ajcf.set( statusMessage.toString() );
    }
    private Status status;
    // "Binding JFX" - Synchronisation avec "MainController.statusLabel"
    public StringProperty ajcf = new SimpleStringProperty();

    // "Binding JFX" - Synchronisation avec "PlayerController.name"
    public StringProperty playerNameProperty = new SimpleStringProperty();

    private Jeton selectedRobot;
    public Jeton getSelectedRobot() { return this.selectedRobot; }

    // * ---

    // Le plateau de taille x taille cases
    private Tuile[][] board;

    // Les 4 robots
    private Map<Jeton.Couleur, Jeton> robots;
    public Map<Jeton.Couleur, Jeton> getRobots() { return this.robots; }

    // La cible
    private Jeton target;
    public Jeton getTarget() { return this.target; }
}
