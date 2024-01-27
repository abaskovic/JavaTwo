package hr.algebra.javatwo.Threads;

import hr.algebra.javatwo.model.GameMove;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class GameMoveThread {
    private static final String GAME_MOVES_FILE = "files/gameMoves.dat";
    private static boolean fileAccessInProgress = false;

    public synchronized void saveNewGameMove(GameMove gameMove) {
        while (fileAccessInProgress){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        fileAccessInProgress = true;
        List<GameMove> gameMoveList = getAllGameMove();
        gameMoveList.add(gameMove);
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream(GAME_MOVES_FILE)
        )) {
            objectOutputStream.writeObject(gameMoveList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileAccessInProgress = false;
        notify();

    }

    public synchronized List<GameMove> getAllGameMove() {
        List<GameMove> gameMoveList = new ArrayList<>();
        if (Files.exists(Path.of(GAME_MOVES_FILE))) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream(GAME_MOVES_FILE)
            )) {
                gameMoveList.addAll((List<GameMove>) objectInputStream.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }


        return gameMoveList;
    }
    protected  synchronized GameMove getLastGameMove() {
        while (fileAccessInProgress){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        fileAccessInProgress = true;

        GameMove gameMove= getAllGameMove().get(getAllGameMove().size()-1);
        fileAccessInProgress = false;
        notify();
        return gameMove;
    }
}
