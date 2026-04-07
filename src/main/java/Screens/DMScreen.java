package Screens;

import core.Launcher;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
 

import helpers.*;

public class DMScreen extends ChatScreen {
    
    private String user1;
    private String user2;
    
    
    public DMScreen(String title, String user1, String user2)  {
        super(title);    
        this.user1 = user1;
        this.user2 = user2;
    }

    public void initFirebase() throws Exception {
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(20,20,400,250);

        this.add(scrollPane);

        JTextField textField = new JTextField("", 100);
        textField.setBounds(400, 300, 150, 45);

        JButton backButton = new JButton("Back");
        backButton.setBounds(20, 300, 80, 45);
        JButton button = new JButton("Send");
        button.setBounds(550, 300, 80, 45);
        this.add(backButton);
        this.add(button);
        this.add(textField);
        JLabel MessagesLabel = new JLabel("Messages with " + user2 + ":");
        MessagesLabel.setBounds(30, 0, 200, 20);
        this.add(MessagesLabel);
        
        button.addActionListener(e -> {
            String text = textField.getText().trim();
            if(!text.isEmpty()) {
                try {
                     ServerAPI.sendDirectMessage(getUser().getUsername(), user2, text);
                     textField.setText("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        backButton.addActionListener(e -> {
            ChatScreen chatScreen = new ChatScreen("Chatter: " + this.getUser().getUsername()+ " -> All Users");
            chatScreen.setUser(this.getUser());
            Launcher.setScreen(chatScreen);
        });
        Timer messagesTimer = new Timer(1000, e -> {
            try {
                ServerAPI.listenForMessages(chatArea, getConversationId(user1, user2),this.getUser().getUsername());
                ServerAPI.setAllMessagesRead(getConversationId(user1, user2), this.getUser().getUsername());
                ServerAPI.heartbeat(getUser().getUsername());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        messagesTimer.start();

        
            
    } 
    

    public static String getConversationId(String user1, String user2) {
        if(user1.compareTo(user2) < 0)
            return user1 + "_" + user2;
        else
            return user2 + "_" + user1;
    }
    
}
