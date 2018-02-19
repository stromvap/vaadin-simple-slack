package se.stromvap.vaadin.simple.slack;

import com.vaadin.annotations.Push;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
@Push
public class VaadinSimpleSlackUI extends UI implements ChatRoom.MessageListener {

    private VerticalLayout messages;
    private Panel messagesPanel;

    @Autowired
    private ChatRoom chatRoom;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        setContent(root);

        createMessagePanel(root);
        createUserInputPanel(root);

        chatRoom.addMessageListener(this);
        addDetachListener(l -> chatRoom.removeMessageListener(VaadinSimpleSlackUI.this));
    }

    private void createMessagePanel(VerticalLayout root) {
        messages = new VerticalLayout();
        messages.setSpacing(false);
        for (String message : chatRoom.getMessages()) {
            messages.addComponent(new Label(message));
        }

        messagesPanel = new Panel();
        messagesPanel.setContent(messages);
        messagesPanel.setSizeFull();
        root.addComponent(messagesPanel);
        root.setExpandRatio(messagesPanel, 2);
    }

    private void createUserInputPanel(VerticalLayout root) {
        HorizontalLayout userInput = new HorizontalLayout();
        userInput.setWidth(100, Unit.PERCENTAGE);
        root.addComponent(userInput);

        TextField messageInput = new TextField();
        messageInput.setWidth(100, Unit.PERCENTAGE);
        userInput.addComponent(messageInput);
        userInput.setExpandRatio(messageInput, 2);

        Button send = new Button("Send");
        send.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        send.addClickListener((Button.ClickListener) clickEvent -> {
            chatRoom.addMessage(messageInput.getValue());
            messageInput.clear();
        });
        userInput.addComponent(send);
    }

    @Override
    public void onMessage(String message) {
        access(() -> {
            messages.addComponent(new Label(message));
            messagesPanel.setScrollTop(Integer.MAX_VALUE);
        });
    }
}
