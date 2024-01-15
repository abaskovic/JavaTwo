package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.model.ClankColor;
import hr.algebra.javatwo.model.GameState;
import hr.algebra.javatwo.model.GridCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.*;
import java.util.List;

import static hr.algebra.javatwo.model.Constants.FILE_NAME;

public class FileUtils {


    public static void saveGame(
            List<GridCell> gameBoardState,
            int timeInSeconds,
            List<ClankColor> bag,
            List<ClankColor> clank,
            boolean redPlayerTurn,
            String redLives,
            String blueLives,
            int dragonPosition,
            String lastStep
    ) {
        GameState gameStateToBeSaved = new GameState(gameBoardState, timeInSeconds, bag, clank, redPlayerTurn, redLives, blueLives, dragonPosition, lastStep);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(gameStateToBeSaved);
            DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                    "Game saved!", "Your game has been successfully saved!");
        } catch (IOException e) {
            DialogUtils.showDialog(Alert.AlertType.ERROR,
                    "Game not saved!", "Your game has not been successfully saved!");
            throw new RuntimeException(e);
        }
    }


    public static GameState loadGame() {
        GameState recoveredGameState;

        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_NAME))) {
            recoveredGameState = (GameState) ois.readObject();
        }
        catch(Exception ex) {
            DialogUtils.showDialog(Alert.AlertType.ERROR,
                    "Game not loaded!", "Your game has not been successfully loaded!");
            throw new RuntimeException(ex);
        }

        return recoveredGameState;
    }
}
