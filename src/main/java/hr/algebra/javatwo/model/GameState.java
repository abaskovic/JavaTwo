package hr.algebra.javatwo.model;

import java.io.Serializable;
import java.util.List;

public class GameState  implements Serializable {
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


    private List<GridCell> gameBoardState;
    private int timeInSeconds;
    private List<ClankColor> bag;
    private List<ClankColor> clank;
    private boolean redPlayerTurn;
    private String redLives;
    private String blueLives;
    private int dragonPosition;
    private String lastStep;

    public List<GridCell> getGameBoardState() {
        return gameBoardState;
    }

    public void setGameBoardState(List<GridCell> gameBoardState) {
        this.gameBoardState = gameBoardState;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public void setTimeInSeconds(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public List<ClankColor> getBag() {
        return bag;
    }

    public void setBag(List<ClankColor> bag) {
        this.bag = bag;
    }

    public List<ClankColor> getClank() {
        return clank;
    }

    public void setClank(List<ClankColor> clank) {
        this.clank = clank;
    }

    public boolean isRedPlayerTurn() {
        return redPlayerTurn;
    }

    public void setRedPlayerTurn(boolean redPlayerTurn) {
        this.redPlayerTurn = redPlayerTurn;
    }

    public String getRedLives() {
        return redLives;
    }

    public void setRedLives(String redLives) {
        this.redLives = redLives;
    }

    public String getBlueLives() {
        return blueLives;
    }

    public void setBlueLives(String blueLives) {
        this.blueLives = blueLives;
    }

    public int getDragonPosition() {
        return dragonPosition;
    }

    public void setDragonPosition(int dragonPosition) {
        this.dragonPosition = dragonPosition;
    }

    public String getLastStep() {
        return lastStep;
    }

    public void setLastStep(String lastStep) {
        this.lastStep = lastStep;
    }


}
