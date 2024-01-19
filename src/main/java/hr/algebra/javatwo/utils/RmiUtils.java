package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.chat.service.RemoteChatService;
import hr.algebra.javatwo.chat.service.RemoteChatServiceImpl;
import hr.algebra.javatwo.model.ConfigurationKey;
import hr.algebra.javatwo.model.ConfigurationReader;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiUtils {

    private static final Integer rmiPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.RMI_PORT);

    public static void StartRmiRemoteChatServer() {
        int randomPortHint = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.RANDOM_PORT_HINT);
        try {
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            RemoteChatService remoteService = new RemoteChatServiceImpl();
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(remoteService,
                    randomPortHint);
            registry.rebind(RemoteChatService.REMOTE_CHAT_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void StartRmiRemoteChatClient() {
        try {
            String host = ConfigurationReader.getInstance().readStringValueForKey(ConfigurationKey.HOST);
            Registry registry = LocateRegistry.getRegistry(host, rmiPort);
            RemoteChatService stub = (RemoteChatService) registry.lookup(RemoteChatService.REMOTE_CHAT_OBJECT_NAME);


        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

}
