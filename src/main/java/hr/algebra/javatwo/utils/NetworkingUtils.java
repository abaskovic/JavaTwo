package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.model.GameState;
import hr.algebra.javatwo.model.NetworkConfiguration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkingUtils {
    public  static  void  sendGameStateToServer(GameState gameState){

        try (Socket cleintSocket = new Socket(NetworkConfiguration.SERVER_HOST,NetworkConfiguration.SERVER_PORT)){
            sendSerializableRequest(cleintSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private static void sendSerializableRequest(Socket cleintSocket, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(cleintSocket.getInputStream());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(cleintSocket.getOutputStream());
        objectOutputStream.writeObject(gameState);
        System.out.println("msg server " + objectInputStream.readObject());

    }
}
