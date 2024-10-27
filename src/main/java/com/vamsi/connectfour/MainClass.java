package com.vamsi.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class MainClass extends Application {

    private MyController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader=new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane root=loader.load();

        controller=loader.getController();
        controller.createPlayGround();


        MenuBar menubar=createMenuBar();
        menubar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane menuPane=(Pane)root.getChildren().get(0);
        menuPane.getChildren().add(menubar);

        Scene scene=new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("ConnectFour Game");
        primaryStage.show();

    }

    private MenuBar createMenuBar() {

        Menu fileMenu=new Menu("File");
        Menu helpMenu=new Menu("Help");

        MenuItem newGame=new MenuItem("New Game");
        newGame.setOnAction(event -> resetGame());

        MenuItem resetGame=new MenuItem("Reset Game");
        resetGame.setOnAction(event -> resetGame());

        SeparatorMenuItem separatorMenuItem=new SeparatorMenuItem();

        MenuItem exitGame=new MenuItem("Exit");
        exitGame.setOnAction(event -> exitGame());

        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);

        MenuItem aboutGame=new MenuItem("About Connect4");
        aboutGame.setOnAction(event-> aboutGame());

        SeparatorMenuItem separatorMenuItem1=new SeparatorMenuItem();

        MenuItem aboutDeveloper=new MenuItem("About Developer");
        aboutDeveloper.setOnAction(event -> aboutDeveloper());

        helpMenu.getItems().addAll(aboutGame,separatorMenuItem1,aboutDeveloper);

        MenuBar menubar=new MenuBar();
        menubar.getMenus().addAll(fileMenu,helpMenu);
        return menubar;

    }

    private void resetGame() {
        controller.resetGame();
    }

    private void aboutDeveloper() {
        Alert aboutDeveloper=new Alert(Alert.AlertType.INFORMATION);
        aboutDeveloper.setTitle("About Developer");
        aboutDeveloper.setHeaderText("Vamsi");
        aboutDeveloper.setContentText("I love to play around the code and create games."+
                " Connect4 is one of them. In free time"+
                "I like to spend time with nears and dears");
        aboutDeveloper.setResizable(false);
        aboutDeveloper.setWidth(400);
        aboutDeveloper.setHeight(240);
        aboutDeveloper.show();
    }

    private void aboutGame() {
        Alert aboutGame=new Alert(Alert.AlertType.INFORMATION);
        aboutGame.setTitle("About Connect Four");
        aboutGame.setHeaderText("How to play?");
        aboutGame.setContentText("Connect Four is a two-player connection game in which the"+
                " players first choose a color and then take turns dropping colored discs"+
                " from the top into a seven-column, six-row vertically suspended grid."+
                "The pieces fall straight down, occupying the next available space within the column." +
                "The objective of the game is to be the first to form a horizontal, vertical, " +
                "or diagonal line of four of one's own discs. Connect Four is a solved game." +
                "The first player can always win by playing the right moves.");
        aboutGame.setResizable(false);
        aboutGame.setWidth(600);
        aboutGame.setHeight(400);
        aboutGame.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
