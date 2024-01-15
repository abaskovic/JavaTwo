package hr.algebra.javatwo.model;

import java.io.Serializable;
import java.util.List;

public class GameState  implements Serializable {

    private static final long serialVersionUID = 1L;
    public GameState(List<GridCell> gameBoardState, int timeInSeconds, List<ClankColor> bag, List<ClankColor> clank, boolean redPlayerTurn, String redLives, String blueLives, int dragonPosition, String lastStep) {
        this.gameBoardState = gameBoardState;
        this.timeInSeconds = timeInSeconds;
        this.bag = bag;
        this.clank = clank;
        this.redPlayerTurn = redPlayerTurn;
        this.redLives = redLives;
        this.blueLives = blueLives;
        this.dragonPosition = dragonPosition;
        this.lastStep = lastStep;
    }


    private final List<GridCell> gameBoardState;
    private final int timeInSeconds;
    private final List<ClankColor> bag;
    private final List<ClankColor> clank;
    private final boolean redPlayerTurn;
    private final String redLives;
    private final String blueLives;
    private final int dragonPosition;
    private final String lastStep;

    public List<GridCell> getGameBoardState() {
        return gameBoardState;
    }


    public int getTimeInSeconds() {
        return timeInSeconds;
    }


    public List<ClankColor> getBag() {
        return bag;
    }


    public List<ClankColor> getClank() {
        return clank;
    }


    public boolean isRedPlayerTurn() {
        return redPlayerTurn;
    }


    public String getRedLives() {
        return redLives;
    }



    public String getBlueLives() {
        return blueLives;
    }


    public int getDragonPosition() {
        return dragonPosition;
    }


    public String getLastStep() {
        return lastStep;
    }



}
