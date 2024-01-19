package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.model.ConfigurationKey;
import hr.algebra.javatwo.model.ConfigurationReader;
import hr.algebra.javatwo.model.GameState;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkingUtils {
    private static final String host = ConfigurationReader.getInstance().readStringValueForKey(ConfigurationKey.HOST);

    public static void sendGameStateToServer(GameState gameState) {
        int serverPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.SERVER_PORT);

        try (Socket clientSocket = new Socket(host, serverPort)) {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendGameStateToClient(GameState gameState) {
        int clientPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.CLIENT_PORT);

        try (Socket clientSocket = new Socket(host, clientPort)) {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendSerializableRequest(Socket cleintSocket, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(cleintSocket.getOutputStream());
        oos.writeObject(gameState);
    }



}
