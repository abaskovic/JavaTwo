package hr.algebra.javatwo.chat.service;

import java.util.ArrayList;
import java.util.List;

public class RemoteChetServiceImpl implements RemoteChatService {

    private List<String> chatMessages;

    public RemoteChetServiceImpl() {
        chatMessages = new ArrayList<>();
    }

    @Override
    public void sendMessage(String sendMessages) throws RuntimeException {
        chatMessages.add(sendMessages);
    }

    @Override
    public List<String> getAllChatMessages() {
        return chatMessages;
    }
}
