package hr.algebra.javatwo.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GameMove implements Serializable {

    private boolean  isRedTurn;
    private int step;
    private LocalDateTime dateTime;

    public boolean isRedTurn() {
        return isRedTurn;
    }

    public void setRedTurn(boolean redTurn) {
        isRedTurn = redTurn;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public GameMove(boolean isRedTurn, int step, LocalDateTime dateTime) {
        this.isRedTurn = isRedTurn;
        this.step = step;
        this.dateTime = dateTime;
    }
}
