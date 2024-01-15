package hr.algebra.javatwo;

import hr.algebra.javatwo.model.GameState;
import hr.algebra.javatwo.model.NetworkConfiguration;
import hr.algebra.javatwo.model.RoleName;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GameApplication extends Application {
    public static  RoleName loggedInRoleName;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);
        stage.setTitle("Clank! - " + loggedInRoleName.name());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        String inputRoleName = args[0];
        loggedInRoleName =  RoleName.CLIENT;

        if (RoleName.SERVER.name().equals(loggedInRoleName.name())){
            new  Thread(GameApplication::startServer).start();

        }else {
            new  Thread(GameApplication::startClient).start();

        }

        new Thread(Application::launch).start(); 
        acceptRequest();
    }

    private static void startClient() {
    }


    private static void startServer() {
        acceptRequest();
    }

    private static void acceptRequest() {
        try (ServerSocket serverSocket = new ServerSocket(NetworkConfiguration.SERVER_PORT)) {
            while (true){
                Socket clinetSocket = serverSocket.accept();
                new Thread(()-> processSerializableClient(clinetSocket)).start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void processSerializableClient(Socket clinetSocket) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(clinetSocket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(clinetSocket.getOutputStream())) {

            GameState gameState = (GameState) objectInputStream.readObject();
            System.out.println("recived " + gameState);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}