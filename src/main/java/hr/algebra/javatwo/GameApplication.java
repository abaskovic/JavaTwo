package hr.algebra.javatwo;

import hr.algebra.javatwo.controller.GameController;
import hr.algebra.javatwo.model.GameState;
import hr.algebra.javatwo.model.NetworkConfiguration;
import hr.algebra.javatwo.model.RoleName;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class GameApplication extends Application {
    public static RoleName loggedInRoleName;
    public static GameController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1050, 800);
        controller = fxmlLoader.getController();
        stage.setTitle("Clank! - " + loggedInRoleName.name());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        String inputRoleName = args[0];
        Boolean userLoggedIn = false;

        for (RoleName roleName : RoleName.values()) {
            if (roleName.name().equals(inputRoleName)) {
                userLoggedIn = true;
                loggedInRoleName = roleName;
                break;
            }
        }

        if (userLoggedIn) {
            if (RoleName.SERVER.equals(loggedInRoleName)) {
                new Thread(GameApplication::startServer).start();
            } else {
                new Thread(GameApplication::startClient).start();
            }
        }
        new Thread(Application::launch).start();
    }

    private static void startClient() {
        acceptRequestsClient();
    }

    private static void acceptRequestsClient() {
        try (ServerSocket serverSocket = new ServerSocket(NetworkConfiguration.CLIENT_PORT)) {
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                new Thread(() -> processSerializableClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void startServer() {
        acceptRequestsServer();
    }

    private static void acceptRequestsServer() {
        try (ServerSocket serverSocket = new ServerSocket(NetworkConfiguration.SERVER_PORT)) {
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                new Thread(() -> processSerializableClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {
            GameState gameState = (GameState) ois.readObject();
            if (gameState.getRedLives() == null) {
                Platform.runLater(() -> {
                    controller.RefreshStepButton(gameState);
                });
            } else {
                Platform.runLater(() -> {
                    controller.RefreshGameBoard(gameState);
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}