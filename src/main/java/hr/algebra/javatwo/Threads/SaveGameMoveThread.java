package hr.algebra.javatwo.Threads;

import hr.algebra.javatwo.model.GameMove;
import hr.algebra.javatwo.utils.GameMovesUtils;

public class SaveGameMoveThread implements  Runnable{

    private GameMove gameMove;
    public SaveGameMoveThread(GameMove gameMove) {
        this.gameMove=gameMove;
    }


    @Override
    public void run() {
        GameMovesUtils.saveNewGameMove(gameMove);
    }
}
