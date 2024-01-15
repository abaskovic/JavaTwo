package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.model.GridCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class GameStateUtils {

    public static List<GridCell> createGameBoardState(Pane boardGridPane) {

        List<GridCell> gameBoardState = new ArrayList<>();

        boardGridPane.getChildren().forEach(node -> {
            int row = GridPane.getRowIndex(node);
            int col = GridPane.getColumnIndex(node);

            GridCell cell = new GridCell(node.toString(), row, col);

            gameBoardState.add(cell);
        });

        return gameBoardState;
    }
}
