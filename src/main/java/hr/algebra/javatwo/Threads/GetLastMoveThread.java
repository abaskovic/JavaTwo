package hr.algebra.javatwo.Threads;

import hr.algebra.javatwo.model.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

public class GetLastMoveThread extends GameMoveThread implements  Runnable{

    private Label lastMoveLabel;
    public  GetLastMoveThread(Label lastMoveLabel){
        this.lastMoveLabel= lastMoveLabel;
    }
    @Override
    public void run() {

        while (true){
        GameMove lastMove = getLastGameMove();
        String player = lastMove.isRedTurn()?"Red player":"Blue player";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String formattedDateTime = lastMove.getDateTime().format(formatter);
        Platform.runLater(()-> {lastMoveLabel.setText("The last Move: " +  player + " moved for  " + lastMove.getStep()
                + " step(s) at " + formattedDateTime );
        });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
