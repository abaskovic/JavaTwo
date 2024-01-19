package hr.algebra.javatwo.utils;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

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

}



