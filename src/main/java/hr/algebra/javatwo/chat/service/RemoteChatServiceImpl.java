package hr.algebra.javatwo.chat.service;

import java.util.ArrayList;
import java.util.List;

public class RemoteChatServiceImpl implements RemoteChatService {

    private List<String> chatMessages;

    public RemoteChatServiceImpl() {
        chatMessages = new ArrayList<>();
    }

    @Override
    public void sendMessage(String sendMessages) throws RuntimeException {
        chatMessages.add(sendMessages);
        List<String> messagesList =  getAllChatMessages();
    }

    @Override
    public List<String> getAllChatMessages() {
        return chatMessages;
    }
}
