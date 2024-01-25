package hr.algebra.javatwo.Threads;

import hr.algebra.javatwo.model.GameMove;

public class SaveGameMoveThread extends GameMoveThread implements  Runnable{

    private GameMove gameMove;
    public SaveGameMoveThread(GameMove gameMove) {
        this.gameMove=gameMove;
    }


    @Override
    public void run() {
      saveNewGameMove(gameMove);
    }
}
