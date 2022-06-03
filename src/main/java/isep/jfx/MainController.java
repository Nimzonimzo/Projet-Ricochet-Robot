package isep.jfx;

import isep.ricrob.Game;
import isep.ricrob.Symbol;
import isep.ricrob.Tile;
import isep.ricrob.Token;
import isep.utility.SymbolsRessources;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static isep.ricrob.Token.Color.*;

public class MainController {

    public final int TILE_SIZE = 40;


    @FXML
    public GridPane boardPane;

    @FXML
    private StackPane targetDisplayed;

    @FXML
    public Label statusLabel;

    @FXML
    public Label timeLabel;

    @FXML
    public Button rejouerButton;

    @FXML
    public Label steps;

    List<Tile> tiles;

    // JavaFX appelle la fonction initialize() lorsqu'il affiche la fenêtre
    @FXML
    public void initialize() {

        //Lie les étiquettes à leurs valeurs
        steps.textProperty().bind(Game.context.getNumberOfSteps());
        timeLabel.textProperty().bind(Game.context.TIME_TO_CATCH.asString());

        // Affiche une alert pour le bonjour
        // Bonjour mes chères compatriotes...
        showAlert("Bienvenue sur le jeu Ricochet Robots \r Êtes-vous chaud pour une partie ?");


        // Construit le plateau du jeu
        // On me dit à l'oreillette qu'il est portugais
        Image tile = new Image("cell.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wl = new Image("WL.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wr = new Image("WR.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wu = new Image("WU.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wd = new Image("WD.png", TILE_SIZE, TILE_SIZE, false, true);

        // ... "cell.png" doit être placé à la racine de "resources/" (sinon PB)
        boardPane.setPadding(new Insets(2));

        int rows = Game.SIZE;
        int cols = Game.SIZE;
        //rows = cols = 4; (lignes et colonnes, I'm bilingue)
        tiles = Game.context.getBoard();

        //Lance le timer
        startTimer();

        //Génère des cellules du tableau
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                ImageView tileGui = new ImageView(tile);
                final int finalRow = row;
                final int finalCol = col;
                tileGui.setOnMouseClicked
                        (event -> {
                            String status = Game.context.processSelectTile(finalCol, finalRow);

                            if ("MOVE".equals(status)) {
                                updateSelectedRobotPosition();
                            } else if (status != null) {
                                if(Game.context.getStatus() == Game.Status.PLAYER_WIN_TOKEN){
                                    updateSelectedRobotPosition();
                                    showAlert(Game.Status.PLAYER_WIN_TOKEN.getToolTip() + "Temps de jeu " +
                                            (120 - (Game.context.TIME_TO_CATCH.getValue())) + " secs.  Nombre de " +
                                        Game.context.getNumberOfSteps().getValue());
                                    Game.context.setStatus(Game.Status.CHOOSE_PLAYER);
                                    restartGame();

                                }
                                showAlert(status);
                            }
                        });

                ImageView wlUL = new ImageView(wl);
                ImageView wrUL = new ImageView(wr);
                ImageView wuUL = new ImageView(wu);
                ImageView wdUL = new ImageView(wd);

                Pane pane = new Pane();
                pane.setPrefWidth(40);
                pane.setPrefHeight(40);
                tileGui.fitWidthProperty().bind(pane.widthProperty());
                pane.setLayoutX(0);
                pane.setLayoutY(0);
                wdUL.fitWidthProperty().bind(pane.widthProperty());
                wrUL.setX(0);


                pane.getChildren().addAll(tileGui, wlUL, wrUL, wuUL, wdUL);
                tiles.add(new Tile(pane, row, col, wlUL, wrUL, wuUL, wdUL));

                boardPane.add(pane, col, row);
            }
        }

        //Défini les positions des robots comme non-disponible
        for (var color : Token.Color.values()) {
            var robot = Game.context.getRobots().get(color);
            getTileAt(robot.getLig(), robot.getCol()).setAvailable(false);
        }

        // Setup des murs ici
        createWalls();


        //Génère des symboles sur les cases
        addSymbols();

        // Ajoute des pièces
        addRobot(RED);
        addRobot(GREEN);
        addRobot(BLUE);
        addRobot(YELLOW);


        //Ajoute les objectifs
        refreshTarget();

        // "Binding JFX" - Synchronisation du "Label" avec l'état du jeu
        statusLabel.textProperty().bind(Game.context.statusToolTipProperty);
        Game.context.setStatus(Game.Status.CHOOSE_PLAYER);
    }

    private Tile getTileAt(int row, int col) {
        int index = row * Game.SIZE + col;
        return tiles.get(index);
    }

    // Affiche une boîte de dialogue construite avec "SceneBuilder"
    public void showPlayerView(ActionEvent actionEvent) throws IOException {
        if (Game.context.getStatus() == Game.Status.CHOOSE_PLAYER) {
            FXMLLoader fxmlLoader = new FXMLLoader
                    (MainApplication.class.getResource("player-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
    }

    private void addRobot(Token.Color color) {
        Token robot = Game.context.getRobots().get(color);
        ImageView robotGui = new ImageView(new Image(
                color.name() + "_robot.png",
                TILE_SIZE, TILE_SIZE, false, true
        ));
        robotGui.setOnMouseClicked
                (event -> Game.context.processSelectRobot(color));
        boardPane.add(robotGui, robot.getCol(), robot.getLig());
        // Association de l' "ImageView" avec le robot stocké dans le jeu
        robot.setGui(robotGui);
    }

    private void updateSelectedRobotPosition() {

        Token robot = Game.context.getSelectedRobot();
        GridPane.setConstraints(robot.getGui(), robot.getCol(), robot.getLig());
    }

    private void showAlert(String message) {
        Alert startMessage
                = new Alert(Alert.AlertType.INFORMATION, message);
        startMessage.setHeaderText(null);
        startMessage.setGraphic(null);
        startMessage.showAndWait();
    }

    private void createWalls() {
        // Murs extérieurs
        getTileAt(0, 0).setWall(true, false, true, false);
        getTileAt(1, 0).setWall(true, false, false, false);
        getTileAt(2, 0).setWall(true, false, false, false);
        getTileAt(3, 0).setWall(true, false, false, false);
        getTileAt(4, 0).setWall(true, false, false, false);
        getTileAt(6, 0).setWall(true, false, true, false);
        getTileAt(7, 0).setWall(true, false, false, false);
        getTileAt(8, 0).setWall(true, false, false, false);
        getTileAt(9, 0).setWall(true, false, false, false);
        getTileAt(10, 0).setWall(true, false, false, false);
        getTileAt(11, 0).setWall(true, false, false, false);
        getTileAt(12, 0).setWall(true, false, false, false);
        getTileAt(13, 0).setWall(true, false, false, false);
        getTileAt(14, 0).setWall(true, false, false, false);
        getTileAt(15, 0).setWall(true, false, false, true);

        getTileAt(0, 1).setWall(false, false, true, false);
        getTileAt(0, 2).setWall(false, false, true, false);
        getTileAt(0, 3).setWall(false, false, true, false);
        getTileAt(0, 4).setWall(false, false, true, false);
        getTileAt(0, 5).setWall(false, false, true, false);
        getTileAt(0, 6).setWall(true, false, true, false);
        getTileAt(0, 7).setWall(false, false, true, false);
        getTileAt(0, 8).setWall(false, false, true, false);
        getTileAt(0, 9).setWall(true, false, true, false);
        getTileAt(0, 10).setWall(false, false, true, false);
        getTileAt(0, 11).setWall(false, false, true, false);
        getTileAt(0, 12).setWall(false, false, true, false);
        getTileAt(0, 13).setWall(false, false, true, false);
        getTileAt(0, 14).setWall(false, false, true, false);
        getTileAt(0, 15).setWall(false, true, true, false);

        getTileAt(15, 1).setWall(false, false, false, true);
        getTileAt(15, 2).setWall(false, false, false, true);
        getTileAt(15, 3).setWall(false, false, false, true);
        getTileAt(15, 4).setWall(false, false, false, true);
        getTileAt(15, 5).setWall(false, false, false, true);
        getTileAt(15, 6).setWall(false, true, false, true);
        getTileAt(15, 7).setWall(false, false, false, true);
        getTileAt(15, 8).setWall(false, false, false, true);
        getTileAt(15, 9).setWall(false, false, false, true);
        getTileAt(15, 10).setWall(false, false, false, true);
        getTileAt(15, 11).setWall(true, false, false, true);
        getTileAt(15, 12).setWall(false, false, false, true);
        getTileAt(15, 13).setWall(false, false, false, true);
        getTileAt(15, 14).setWall(false, false, false, true);
        getTileAt(15, 15).setWall(false, true, false, true);

        getTileAt(1, 15).setWall(false, true, false, false);
        getTileAt(2, 15).setWall(false, true, false, false);
        getTileAt(3, 15).setWall(false, true, false, false);
        getTileAt(4, 15).setWall(false, true, false, false);
        getTileAt(5, 15).setWall(false, true, true, false);
        getTileAt(6, 15).setWall(false, true, false, false);
        getTileAt(7, 15).setWall(false, true, false, false);
        getTileAt(8, 15).setWall(false, true, false, false);
        getTileAt(9, 15).setWall(false, true, false, false);
        getTileAt(10, 15).setWall(false, true, false, false);
        getTileAt(11, 15).setWall(false, true, false, false);
        getTileAt(12, 15).setWall(false, true, false, false);
        getTileAt(13, 15).setWall(false, true, true, false);
        getTileAt(14, 15).setWall(false, true, false, false);


        // Murs intérieurs

        getTileAt(1, 10).setWall(true, false, false, true);

        getTileAt(2, 12).setWall(false, true, false, true);

        getTileAt(3, 1).setWall(true, false, true, false);
        getTileAt(3, 12).setWall(true, false, false, false);

        getTileAt(4, 6).setWall(false, true, false, true);


        getTileAt(5, 0).setWall(true, false, false, false);
        getTileAt(5, 8).setWall(true, false, false, true);

        getTileAt(6, 2).setWall(false, true, true, false);
        getTileAt(6, 3).setWall(false, false, false, true);
        getTileAt(6, 7).setWall(false, false, false, true);
        getTileAt(6, 8).setWall(false, false, false, true);
        getTileAt(6, 13).setWall(false, true, true, false);

        //Centre
        getTileAt(7, 6).setWall(false, true, false, false);
        getTileAt(7, 9).setWall(true, false, false, false);


        getTileAt(8, 6).setWall(false, true, false, false);
        getTileAt(8, 9).setWall(true, false, false, false);

        //Du milieu au bas
        getTileAt(9, 7).setWall(false, false, true, false);
        getTileAt(9, 8).setWall(false, false, true, false);
        getTileAt(9, 2).setWall(true, false, false, false);
        getTileAt(9, 10).setWall(false, true, false, true);

        getTileAt(10, 2).setWall(false, true, true, false);

        getTileAt(11, 7).setWall(true, false, true, false);
        getTileAt(11, 12).setWall(false, false, true, false);
        getTileAt(11, 13).setWall(true, false, false, true);

        getTileAt(13, 0).setWall(true, false, true, false);
        getTileAt(13, 9).setWall(true, false, true, false);

        getTileAt(14, 5).setWall(false, true, false, true);
    }

    //Ajoute des symboles à des positions static
    private void addSymbols() {
        //Ajoute des symboles rouges
        addSymbol(1, 10, SymbolsRessources.redSymbols[2], RED);
        addSymbol(3, 1, SymbolsRessources.redSymbols[0], RED);
        addSymbol(11, 12, SymbolsRessources.redSymbols[3], RED);
        addSymbol(14, 5, SymbolsRessources.redSymbols[1], RED);

        //Ajoute des symboles bleus
        addSymbol(2, 12, SymbolsRessources.blueSymbols[0], BLUE);
        addSymbol(6, 3, SymbolsRessources.blueSymbols[1], BLUE);
        addSymbol(11, 7, SymbolsRessources.blueSymbols[3], BLUE);
        addSymbol(13, 9, SymbolsRessources.blueSymbols[2], BLUE);

        //Ajoute des symboles jaunes
        addSymbol(4, 6, SymbolsRessources.yellowSymbols[2], YELLOW);
        addSymbol(6, 13, SymbolsRessources.yellowSymbols[3], YELLOW);
        addSymbol(9, 2, SymbolsRessources.yellowSymbols[0], YELLOW);
        addSymbol(9, 10, SymbolsRessources.yellowSymbols[1], YELLOW);

        //Ajoute des symboles verts
        addSymbol(3, 12, SymbolsRessources.greenSymbols[1], GREEN);
        addSymbol(6, 2, SymbolsRessources.greenSymbols[3], GREEN);
        addSymbol(10, 2, SymbolsRessources.greenSymbols[2], GREEN);
        addSymbol(11, 13, SymbolsRessources.greenSymbols[0], GREEN);
    }


    private void addSymbol(int row, int col, String image, Token.Color color) {
        Token symbol = new Symbol(color);
        symbol.setPosition(col, row);
        var symbolGui = new ImageView(new Image(
                image,
                TILE_SIZE, TILE_SIZE, false, true
        ));

        boardPane.add(symbolGui, symbol.getCol(), symbol.getLig());
        // Association de l' "ImageView" avec le robot stocké dans le jeu
        symbol.setGui(symbolGui);

        Game.context.getSymbols().add(symbol);
    }

    private void refreshTarget() {
        //Supprime les éléments "child" précédents
        //Il va chercher du lait, il arrive
        targetDisplayed.getChildren().clear();

        //Obtiens nouvelle cible
        //Cherche proie sur tinder
        var random = new Random();
        var index = random.nextInt(Game.context.getSymbols().size());
        var newTarget = Game.context.getSymbols().get(index);

        var targetGui = new ImageView(new Image(
                ((ImageView) newTarget.getGui()).getImage().getUrl(),
                TILE_SIZE * 2, TILE_SIZE * 2, false, true
        ));
        Image centerImage = new Image("center.png", TILE_SIZE * 2, TILE_SIZE * 2, false, false);
        ImageView tileBackground = new ImageView(centerImage);
        targetDisplayed.getChildren().add(tileBackground);
        targetDisplayed.getChildren().add(targetGui);

        Game.context.setTarget((Symbol) newTarget);

    }

    private void startTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (Game.context.getStatus() != Game.Status.CHOOSE_PLAYER) {
                    Platform.runLater(()-> rejouerButton.setDisable(false));
                    var newValue = Game.context.TIME_TO_CATCH.getValue() - 1;
                    if (newValue == 0) {

                        Platform.runLater(() -> {
                            Alert startMessage
                                    = new Alert(Alert.AlertType.INFORMATION, "PERDU !! C'est décevant tout ça...\r Nul nul nul, fois nul, divisé par nul au carré t'es nuuuuuul...");
                            startMessage.setHeaderText(null);
                            startMessage.setGraphic(null);
                            startMessage.show();
                            restartGame();
                            Game.context.setStatus(Game.Status.CHOOSE_PLAYER);
                        });
                    }
                    if (newValue > 0) {
                        Platform.runLater(() -> Game.context.TIME_TO_CATCH.setValue(newValue));
                    }

                } else {
                    //Cache le bouton pour restart le jeu
                    //Cache-cache sur la Tour Eiffel, ça tourne mal
                    Platform.runLater(()-> rejouerButton.setDisable(true));
                }

            }

        }, 0, 1000);
    }

    public void resetRobotsPositions(){
        Game.context.getRobots().forEach((color,robot) ->{
            getTileAt(robot.getLig() ,robot.getCol()).setAvailable(true);
            Random random = new Random();
            var row = random.nextInt(Game.SIZE);
            var col = random.nextInt(Game.SIZE);
            robot.setPosition(col, row);
            getTileAt(row ,col).setAvailable(false);
            GridPane.setConstraints(robot.getGui(), robot.getCol(), robot.getLig());
        });
    }

    public void restartGame() {
        this.refreshTarget();
        Game.context.resetTimer();
        Game.context.resetSteps();
        resetRobotsPositions();
    }

    public void restartGameAction(ActionEvent actionEvent) {
        //TODO
        System.out.println("Le jeu redémarre"); //La ch'nille elle r'démarre
        Platform.runLater(this::restartGame);
    }
}
