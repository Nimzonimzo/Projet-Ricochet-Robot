package com.example.projetricochetrobot;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.util.Random;


public class Jeton {

    public Jeton(Couleur couleur) {
        this.couleur = couleur;
        Random random = new Random();
        setPosition( random.nextInt(Game.taille), random.nextInt(Game.taille) );
    }

    private Couleur couleur;
    public Couleur getCouleur() { return this.couleur; }

    // * Position

    public int colonne;
    public int ligne;

    public void setPosition(int colonne, int ligne) {
        this.colonne = colonne;
        this.ligne = ligne;
    }
    public int getColonne() { return colonne; }
    public int getLigne() { return ligne; }

    // Composant "JFX" associé
    Node lambda;
    public void setLambda(ImageView lambda) { this.lambda = lambda; }
    public Node getLambda() { return lambda; }

    // * ---

    public enum Couleur {rouge, vert, bleu, jaune}

}
