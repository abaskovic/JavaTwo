package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.GameApplication;
import hr.algebra.javatwo.chat.service.RemoteChatService;
import hr.algebra.javatwo.chat.service.RemoteChatServiceImpl;
import hr.algebra.javatwo.model.ConfigurationKey;
import hr.algebra.javatwo.model.ConfigurationReader;
import hr.algebra.javatwo.model.RoleName;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatUtils {

    private static RemoteChatService remoteChatService;
    private static final int rmiPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.RMI_PORT);

    public static void StartRmiRemoteChatClient() {


        String host = ConfigurationReader.getInstance().readStringValueForKey(ConfigurationKey.HOST);
        try {
            Registry registry = LocateRegistry.getRegistry(host, rmiPort);
            remoteChatService = (RemoteChatService) registry.lookup(RemoteChatService.REMOTE_CHAT_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
    public static void StartRmiRemoteChatServer() {
        int randomPortHint = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.RANDOM_PORT_HINT);
        try {
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            remoteChatService = new RemoteChatServiceImpl();
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(remoteChatService,randomPortHint);
            registry.rebind(RemoteChatService.REMOTE_CHAT_OBJECT_NAME, skeleton);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public static Timeline CheckMessages(TextFlow chatMessagesTextFlow) {
        final Timeline timelineChat = new Timeline(new KeyFrame(
                Duration.millis(500), actionEvent -> {

            try {
                List<String> chatMessages = remoteChatService.getAllChatMessages();
                chatMessagesTextFlow.getChildren().clear();
                for (String msg : chatMessages.reversed()) {
                    Text chatMessage = new Text(msg +  "\n");
                    if ( msg.contains(RoleName.BLUE.name())) {
                        chatMessage.setFill(Color.BLUE);
                    } else {
                        chatMessage.setFill(Color.RED);
                    }
                    chatMessagesTextFlow.getChildren().add(chatMessage);

                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }));
        timelineChat.setCycleCount(Animation.INDEFINITE);
        return timelineChat;
    }

    public static void sendMessage(String message) {
        try {
            if (!message.isEmpty()) {
                LocalDateTime timestamp = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String formattedTimestamp = timestamp.format(formatter);
                String fullMessage = formattedTimestamp + " - " + GameApplication.loggedInRoleName + ": " + message;
                remoteChatService.sendMessage(fullMessage);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
