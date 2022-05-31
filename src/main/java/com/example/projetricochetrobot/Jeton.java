package com.example.projetricochetrobot;

import java.util.Random;

public class Jeton {
    public class Jeton {

        public Jeton(Couleur couleur) {
            this.couleur = couleur;
            Random random = new Random();
            setPosition( random.nextInt(Game.taille), random.nextInt(Game.taille) );
        }

        private Couleur couleur;
        public Couleur getCouleur() { return this.couleur; }

        // * Position

        public int col;
        public int lig;

        public void setPosition(int col, int lig) {
            this.col = col;
            this.lig = lig;
        }
        public int getCol() { return col; }
        public int getLig() { return lig; }

        // Composant "JFX" associé
        Node gui;
        public void setGui(ImageView gui) { this.gui = gui; }
        public Node getGui() { return gui; }

        // * ---

        public enum Couleur {rouge, vert, bleu, jaune}

    }
}
