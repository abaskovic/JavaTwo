package hr.algebra.javatwo;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.stream.Collectors;

public class HelloController {


    @FXML
    private Button stepButton;

    @FXML
    private Label redLivesLabel;

    @FXML
    private Label blueLivesLabel;


    @FXML
    private ImageView redPlayerImage;
    @FXML
    private ImageView bluePlayerImage;

    @FXML
    private ImageView dragonStepImage;

    @FXML
    private GridPane boardGridPane;

    @FXML
    private GridPane dragonStepGridPane;

    @FXML
    private Pane clankPane;
    @FXML
    private Pane bagPane;

    private boolean redPlayerTurn = true;
    private List<String> bag = new ArrayList<String>();
    private int dragonLocation = 0;


    public final int NUM_DRAGONS = 7;
    public final int NUM_DRAGONS_IN_BAG = 15;

    public void initialize() {
        boardGridPane.add(bluePlayerImage, 0, 0);
        boardGridPane.add(redPlayerImage, 0, 0);
        dragonStepGridPane.add(dragonStepImage, 0, 0);
        for (int i = 0; i < NUM_DRAGONS; i++) {
            placeDragons();
        }

        for (int i = 0; i < NUM_DRAGONS_IN_BAG; i++) {
            bag.add("D");
        }
    }

    private void placeDragons() {
        Random random = new Random();
        int randomRow = random.nextInt(5);
        int randomCol = random.nextInt(5);

        if (randomRow == 0 && randomCol == 0) {
            randomRow = 2;
            randomCol = 2;
        }

        Label dragonLabel = new Label("D");
        dragonLabel.setTextFill(Color.RED);
        dragonLabel.setAlignment(Pos.BOTTOM_RIGHT);
        dragonLabel.setPrefSize(150, 150);

        boardGridPane.add(dragonLabel, randomCol, randomRow);

    }

    @FXML
    protected void onStepButtonClick() {
        rollDice();
    }

    private void rollDice() {


        Random random = new Random();
        int step = random.nextInt(6) + 1;
        stepButton.setText(Integer.toString(step));
        ImageView currentPlayer = redPlayerTurn ? redPlayerImage : bluePlayerImage;
        movePlayer(currentPlayer, step);

        if (GridPane.getColumnIndex(dragonStepImage) < 5)MoveDragon();
        CheckWinner();
        redPlayerTurn = !redPlayerTurn;


    }

    private void CheckWinner() {
        int redLives = Integer.parseInt(redLivesLabel.getText());
        int blueLives = Integer.parseInt(blueLivesLabel.getText());

        if (redLives<1){
            System.out.println("BLUE WIN");
        }
        if (blueLives<1){
            System.out.println("RED WIN");
        }

    }

    private void MoveDragon() {
        dragonLocation = GridPane.getColumnIndex(dragonStepImage) + 1;
        GridPane.setColumnIndex(dragonStepImage, dragonLocation);
    }


    private void movePlayer(ImageView player, int step) {

        int currentRow = GridPane.getRowIndex(player);
        int currentCol = GridPane.getColumnIndex(player);

        currentCol += step;

        if (currentCol > 4) {
            int dif = currentCol - 5;
            currentRow++;
            currentCol = dif;
        }

        currentRow=Math.min(currentRow,4);
        currentCol=Math.min(currentCol,4);


        if (currentCol==4 && currentRow==4){
            currentRow=0;
            currentCol=0;
        }


        System.out.println(dragonLocation);
        if (currentCol*currentRow>24){
            System.out.println("ces");
            return;
        }
        GridPane.setRowIndex(player, currentRow);
        GridPane.setColumnIndex(player, currentCol);


        int rectangleAdd = (int) Math.ceil((double) step / 2);

        addPaneRectangle(redPlayerTurn ? Color.RED : Color.BLUE, rectangleAdd, clankPane);

        if (stepOnDragon(currentCol, currentRow)) {

            Collections.shuffle(bag);

            List<String> selectedFromBag = new ArrayList<String>(bag.subList(0, dragonLocation + 1));

            bagPane.getChildren().clear();
            for (String element : selectedFromBag) {
                System.out.println(element);

                Color color = null;
                switch (element) {
                    case "D" -> {
                        color = Color.BLACK;
                        if (dragonLocation > 0) dragonLocation--;
                        GridPane.setColumnIndex(dragonStepImage, dragonLocation);
                    }
                    case "R" -> {
                        color = Color.RED;
                        int redLives = Integer.parseInt(redLivesLabel.getText()) - 1;
                        redLivesLabel.setText(Integer.toString(redLives));
                        CheckWinner();
                    }
                    case "B" -> {
                        color = Color.BLUE;
                        int blueLives = Integer.parseInt(blueLivesLabel.getText()) - 1;
                        blueLivesLabel.setText(Integer.toString(blueLives));
                        CheckWinner();
                    }
                }


                addPaneRectangle(color, 1, bagPane);
            }

            clankPane.getChildren().clear();

            bag.subList(0, dragonLocation + 1).clear();

            bag = bag.stream().filter(el -> el.equals("D")).collect(Collectors.toList());


        }


    }

    private void addPaneRectangle(Color color, int rectangleAdd, Pane pane) {
        for (int i = 0; i < rectangleAdd; i++) {
            Rectangle playerRectangle = new Rectangle(20, 20, color);
            int clankSize = pane.getChildren().size();
            playerRectangle.setTranslateX(clankSize * 25);
            pane.getChildren().add(playerRectangle);
            bag.add(redPlayerTurn ? "R" : "B");
        }

    }

    private boolean stepOnDragon(int col, int row) {
        Node node = getNodeByRowColIndex(col, row, boardGridPane);
        return node instanceof Label;
    }

    private Node getNodeByRowColIndex(int col, int row, GridPane gridPane) {
        Node res = null;
        ObservableList<Node> children = gridPane.getChildren();

        for (Node n : children) {
            if (GridPane.getColumnIndex(n) != null && GridPane.getColumnIndex(n) == col && GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) == row) {
                if (!(n instanceof ImageView)) {
                    res = n;
                    break;
                }

            }

        }
        return res;
    }
}