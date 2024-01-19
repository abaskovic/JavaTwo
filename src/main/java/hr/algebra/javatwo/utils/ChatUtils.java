package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.GameApplication;
import hr.algebra.javatwo.chat.service.RemoteChatService;
import hr.algebra.javatwo.chat.service.RemoteChatServiceImpl;
import hr.algebra.javatwo.model.NetworkConfiguration;
import hr.algebra.javatwo.model.RoleName;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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

    public static void StartRmiRemoteChatClient() {

        try {
            Registry registry = LocateRegistry.getRegistry(NetworkConfiguration.HOST, NetworkConfiguration.RMI_PORT);
            remoteChatService = (RemoteChatService) registry.lookup(RemoteChatService.REMOTE_CHAT_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public static void StartRmiRemoteChatServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(NetworkConfiguration.RMI_PORT);
            remoteChatService = new RemoteChatServiceImpl();
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(remoteChatService,
                    NetworkConfiguration.RANDOM_PORT_HINT);
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
                    if ( msg.contains(RoleName.SERVER.name())) {
                        chatMessage.setFill(Color.RED);
                    } else {
                        chatMessage.setFill(Color.BLUE);
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
