package se.stromvap.vaadin.simple.slack;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatRoom {
    private List<String> messages = new CopyOnWriteArrayList<>();
    private List<MessageListener> messageListeners = new CopyOnWriteArrayList<>();

    public List<String> getMessages() {
        return messages;
    }

    public void addMessage(String message) {
        messages.add(message);
        new Thread(() -> messageListeners.forEach(l -> l.onMessage(message))).start();

    }

    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void removeMessageListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }

    public interface MessageListener {
        void onMessage(String message);
    }
}
