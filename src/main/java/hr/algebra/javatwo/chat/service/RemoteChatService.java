package hr.algebra.javatwo.chat.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteChatService extends Remote {

    String REMOTE_CHAT_OBJECT_NAME="hr.algebra.chat.service";
    void sendMessage(String sendMessages) throws RemoteException;
    List<String> getAllChatMessages() throws RemoteException;


}
