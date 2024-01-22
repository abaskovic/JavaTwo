package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.Threads.ThreadState;
import hr.algebra.javatwo.model.GameMove;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GameMovesUtils  {
    private static final String GAME_MOVES_FILE = "files/gameMoves.dat";

    public synchronized static void saveNewGameMove(GameMove gameMove) {

        while (ThreadState.fileAccessInProgress){
//            wait();
        }
        ThreadState.fileAccessInProgress = true;

        List<GameMove> gameMoveList = getAllGameMove();
        gameMoveList.add(gameMove);
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream(GAME_MOVES_FILE)
        )) {
            objectOutputStream.writeObject(gameMoveList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ThreadState.fileAccessInProgress = false;
//        notifyAll();

    }

    private synchronized static List<GameMove> getAllGameMove() {
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

    public static GameMove getLastGameMove() {
        return getAllGameMove().getLast();
    }
}

