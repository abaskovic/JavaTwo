package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.chat.service.RemoteChatService;
import hr.algebra.javatwo.chat.service.RemoteChetServiceImpl;
import hr.algebra.javatwo.model.NetworkConfiguration;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiUtils {

    public static void StartRmiRemoteChatServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(NetworkConfiguration.RMI_PORT);
            RemoteChatService remoteService = new RemoteChetServiceImpl();
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(remoteService,
                    NetworkConfiguration.RANDOM_PORT_HINT);
            registry.rebind(RemoteChatService.REMOTE_CHAT_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void StartRmiRemoteChatClient() {
        try {
            Registry registry = LocateRegistry.getRegistry(NetworkConfiguration.HOST, NetworkConfiguration.RMI_PORT);
            RemoteChatService stub = (RemoteChatService) registry.lookup(RemoteChatService.REMOTE_CHAT_OBJECT_NAME);


        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

}
