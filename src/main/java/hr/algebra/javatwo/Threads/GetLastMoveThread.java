package hr.algebra.javatwo.Threads;

import hr.algebra.javatwo.model.GameMove;
import hr.algebra.javatwo.utils.GameMovesUtils;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.time.format.DateTimeFormatter;

public class GetLastMoveThread implements  Runnable{

    private Label lastMoveLabel;
    public  GetLastMoveThread(Label lastMoveLabel){
        this.lastMoveLabel= lastMoveLabel;
    }
    @Override
    public void run() {
        GameMove lastMove = GameMovesUtils.getLastGameMove();
        String player = lastMove.isRedTurn()?"Red player":"Blue player";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String formattedDateTime = lastMove.getDateTime().format(formatter);
        lastMoveLabel.setText("The last Move: " +  player + " moved for  " + lastMove.getStep() + " step(s) at " + formattedDateTime );
    }


}
