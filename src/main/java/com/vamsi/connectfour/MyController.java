package com.vamsi.connectfour;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyController implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final double CIRCLE_DIAMETER = 100;
    private static final String discColor1 = "#24303E"; //Outer space
    private static final String discColor2 = "#4CAA34"; //Mint leaf

    private static final String playerOne = "Player One";
    private static final String playerTwo = "Player Two";

    private static boolean isPlayerOne=true;

    private static final Disc[][] insertedDiscArray=new Disc[ROWS][COLUMNS];



    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertDiscPane;

    @FXML
    public Label playerNameLabel;


    public void createPlayGround(){
        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles,0,1);

        List<Rectangle> rectangleList = clickableRectangles();

        for(Rectangle rectangle : rectangleList){
            rootGridPane.add(rectangle,0,1);
        }


    }
    private Shape createGameStructuralGrid(){

        Shape rectangleWithHoles=new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);

        for(int row=0;row<ROWS;row++){
            for (int col=0;col<COLUMNS;col++){
                Circle circle=new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER/2);
                circle.setCenterY(CIRCLE_DIAMETER/2);
                circle.setSmooth(true);

                circle.setTranslateX(col*(CIRCLE_DIAMETER+6)+(CIRCLE_DIAMETER/4));
                circle.setTranslateY(row*(CIRCLE_DIAMETER+8)+(CIRCLE_DIAMETER/4));

                rectangleWithHoles=Shape.subtract(rectangleWithHoles,circle);
            }
        }

        rectangleWithHoles.setFill(Color.WHITE);

        return rectangleWithHoles;
    }

    private List<Rectangle> clickableRectangles(){

        List<Rectangle> rectangleList = new ArrayList<>();

        for(int col=0;col<COLUMNS;col++){
            Rectangle rectangle=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col*(CIRCLE_DIAMETER+6)+(CIRCLE_DIAMETER/4));

            rectangle.setOnMouseClicked(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column=col;
            rectangle.setOnMouseClicked(event -> {
                setAllRectanglesDisable(rectangleList);  //Modifications for simulating discs
                insertDiscs(new Disc(isPlayerOne), column);
                PauseTransition pause=new PauseTransition(Duration.seconds(0.4));
                pause.setOnFinished(event1 -> {
                    setAllRectanglesNotDisable(rectangleList);
                });
                pause.play();
            });
            rectangleList.add(rectangle);
        }

        return rectangleList;
    }


    private void setAllRectanglesNotDisable(List<Rectangle> rectangleList) {
        for(Rectangle rectangle:rectangleList){
            rectangle.setDisable(false);
        }
    }

    private void setAllRectanglesDisable(List<Rectangle> rectangleList) {
        for(Rectangle rectangle:rectangleList){
            rectangle.setDisable(true);
        }

    }

    private void insertDiscs(Disc disc, int col) {
        int row=ROWS-1;
        while(row>=0){
            if(insertedDiscArray[row][col] == null){
                break;
            }
            row--;
        }

        if(row<0){
            return;
        }
        insertedDiscArray[row][col]=disc;
        insertDiscPane.getChildren().add(disc);

        disc.setTranslateX(col*(CIRCLE_DIAMETER+6)+(CIRCLE_DIAMETER/4));
        TranslateTransition transition=new TranslateTransition(Duration.seconds(0.5),disc);
        transition.setToY(row*(CIRCLE_DIAMETER+8)+(CIRCLE_DIAMETER/4));
        final int currentRow=row;
        transition.setOnFinished(event ->{
            if(gameEnded(currentRow,col)){
                gameOver();
            }

            isPlayerOne = !isPlayerOne;
            playerNameLabel.setText(isPlayerOne?playerOne:playerTwo);
        });

        transition.play();

    }

    private void gameOver() {
        String winner=isPlayerOne?playerOne:playerTwo;

        Alert winningAlert=new Alert(Alert.AlertType.INFORMATION);
        winningAlert.setTitle("Connect Four Game");
        winningAlert.setHeaderText("The Winner is " + winner);
        winningAlert.setContentText("Do you want play, Again");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");

        winningAlert.getButtonTypes().setAll(yesBtn,noBtn);
        Platform.runLater(() -> {
            Optional<ButtonType> container = winningAlert.showAndWait();
            if(container.isPresent() && container.get()==yesBtn){
                resetGame();
            }else{
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        insertDiscPane.getChildren().clear();

        for(int row=0;row<ROWS;row++){
            for (int col=0;col<COLUMNS;col++){
                insertedDiscArray[row][col]=null;
            }
        }
        isPlayerOne=true;
        playerNameLabel.setText(playerOne);

        createPlayGround();

    }


    //Check any four discs connected horizontally or vertically. As of now this application
    //doesn't check diagonally. I am working on it
    private boolean gameEnded(int row,int col) {

        List<List<Integer>> verticalPoints=getVerticalPoints(row,col);

        List<List<Integer>> horizontalPoints=getHorizontalPoints(row,col);

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints);
        return isEnded;
    }

    //To get the horizontal points. There is a better way to get using Streams
    private List<List<Integer>> getHorizontalPoints(int row, int col) {
        List<List<Integer>> temporaryPoints=new ArrayList<>();
        for(col=0;col<COLUMNS;col++){
            List<Integer> list=new ArrayList<>();
            list.add(row);
            list.add(col);
            temporaryPoints.add(list);
        }

        return temporaryPoints;
    }

    private boolean checkCombinations(List<List<Integer>> points) {
        int chain=0;

        for(List<Integer> list:points){
            int x=list.get(0);
            int y=list.get(1);

            Disc disc=insertedDiscArray[x][y];

            if(insertedDiscArray[x][y] != null && disc.isPlayerOne==isPlayerOne){
                chain++;
                if(chain ==4) {
                    return true;
                }
            }else{
                chain=0;
            }
        }
        return false;
    }


    //To get the vertical points. There is a better way to get using Streams
    private List<List<Integer>> getVerticalPoints(int row, int col) {
        List<List<Integer>> temporaryPoints=new ArrayList<>();
        for(row=0;row<ROWS;row++){
          List<Integer> list=new ArrayList<>();
          list.add(row);
          list.add(col);
          temporaryPoints.add(list);
        }

        return temporaryPoints;
    }

    private static class Disc extends Circle{
        private final boolean isPlayerOne;

        public Disc(boolean isPlayerOne){
            this.isPlayerOne=isPlayerOne;
            setRadius(CIRCLE_DIAMETER/2);
            setFill(isPlayerOne?Color.valueOf(discColor1):Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);

        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
