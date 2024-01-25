package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.controller.GameController;
import hr.algebra.javatwo.model.ClankColor;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

    public static void ShowImage(int a) {
        a--;

    }

}



