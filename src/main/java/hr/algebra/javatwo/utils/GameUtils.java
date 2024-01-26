package hr.algebra.javatwo.utils;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

public class GameUtils {



    private static Node getNodeByRowColIndex(int col, int row, GridPane gridPane) {
        Node res = null;
        ObservableList<Node> children = gridPane.getChildren();

        for (Node n : children) {
            if (GridPane.getColumnIndex(n) != null && GridPane.getColumnIndex(n) == col && GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) == row) {
                if (!(n instanceof ImageView image)) {
                    res = n;
                    break;
                } else {
                    String url = image.getImage().getUrl();
                    if (url.contains("gold.png")) {
                        res = n;
                    }
                }

            }

        }
        return res;
    }

    public static boolean stepOnDragon(int col, int row, GridPane boardGridPane) {
        Node node = getNodeByRowColIndex(col, row, boardGridPane);
        return node instanceof Label;
    }


    public static boolean stepOnGold(int col, int row, GridPane boardGridPane) {
        Node node = getNodeByRowColIndex(col, row, boardGridPane);
        return node instanceof ImageView;

    }

    public static void placeDragons(GridPane gridPane) {
        Random random = new Random();
        int randomRow = random.nextInt(5);
        int randomCol = random.nextInt(5);

        if (randomRow == 0 && randomCol == 0) {
            randomRow = 2;
            randomCol = 2;
        }

        addDragon(randomCol, randomRow, gridPane);
    }

    public static void addDragon(int col, int row, GridPane gridPane) {
        Label dragonLabel = new Label("D");
        dragonLabel.setTextFill(Color.RED);
        dragonLabel.setAlignment(Pos.BOTTOM_RIGHT);
        dragonLabel.setPrefSize(150, 150);
        gridPane.add(dragonLabel, col, row);
    }

    public static void ShowImage(GridPane gridPane, ImageView image) {
        Random random = new Random();
        int randomRow = random.nextInt(5);
        int randomCol = random.nextInt(5);
        image.setVisible(true);
        gridPane.add(image, randomCol, randomRow);
    }
    public static void hideElements(List<Node> elements) {
        for (Node element : elements) {
            element.setVisible(false);
        }
    }
    public static void clearChildren(List<Pane> containers) {
        for (Pane container : containers) {
            container.getChildren().clear();
        }
    }
    public static void setToStart(List<ImageView> images) {
        for (ImageView image : images) {
            GridPane.setRowIndex(image, 0);
            GridPane.setColumnIndex(image, 0);
        }
    }

    public static void rollDice(Button stepButton) {
        stepButton.setDisable(true);
        Random random = new Random();
        int step = random.nextInt(6) + 1;
        stepButton.setText(Integer.toString(step));
    }

    public static void ButtonsDisable(Button stepButton, boolean stepButtonDisable, Button skipButton,
                                      boolean skipButtonDisable, Button useButton, boolean useButtonDisable) {
        stepButton.setDisable(stepButtonDisable);
        skipButton.setDisable(skipButtonDisable);
        useButton.setDisable(useButtonDisable);
    }
}



