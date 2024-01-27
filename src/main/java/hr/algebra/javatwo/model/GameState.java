package hr.algebra.javatwo.model;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {


    @Override
    public String toString() {
        return "GameState{" +
                "gameBoardState=" + gameBoardState +
                ", stepsToShowGold=" + stepsToShowGold +
                ", bag=" + bag +
                ", clank=" + clank +
                ", redPlayerTurn=" + redPlayerTurn +
                ", redLives='" + redLives + '\'' +
                ", blueLives='" + blueLives + '\'' +
                ", dragonPosition=" + dragonPosition +
                ", lastStep='" + lastStep + '\'' +
                '}';
    }

    private static final long serialVersionUID = 1L;

    public GameState(List<GridCell> gameBoardState, int stepsToShowGold, List<ClankColor> bag, List<ClankColor> clank,
                     boolean redPlayerTurn, String redLives, String blueLives, int dragonPosition, String lastStep, String winner) {
        this.gameBoardState = gameBoardState;
        this.stepsToShowGold = stepsToShowGold;
        this.bag = bag;
        this.clank = clank;
        this.redPlayerTurn = redPlayerTurn;
        this.redLives = redLives;
        this.blueLives = blueLives;
        this.dragonPosition = dragonPosition;
        this.lastStep = lastStep;
        this.winner=winner;
    }


    public GameState(String lastStep) {
        this.gameBoardState = null;
        this.stepsToShowGold = 0;
        this.bag = null;
        this.clank = null;
        this.redPlayerTurn = false;
        this.redLives = null;
        this.blueLives = null;
        this.dragonPosition = 0;
        this.lastStep = lastStep;
        this.winner=null;
    }


    private final List<GridCell> gameBoardState;
    private final int stepsToShowGold;
    private final List<ClankColor> bag;
    private final List<ClankColor> clank;
    private final boolean redPlayerTurn;
    private final String redLives;
    private final String blueLives;
    private final int dragonPosition;
    private final String lastStep;
    private final String winner;


    public List<GridCell> getGameBoardState() {
        return gameBoardState;
    }


    public int gettepsToShowGold() {
        return stepsToShowGold;
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


    public String getWinner() {
        return winner;
    }
}
