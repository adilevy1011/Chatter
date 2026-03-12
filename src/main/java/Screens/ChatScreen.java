package Screens;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.util.ArrayList;
import helpers.*;
import core.*;

public class ChatScreen extends Screen{
    private User user;
    public ChatScreen(String title)  {
        super(title);
        
    }
    public void initFirebase() throws Exception{

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);

        JTextArea chatArea2 = new JTextArea();
        chatArea2.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(20,20,400,250);

        JScrollPane scrollPane2 = new JScrollPane(chatArea2);
        scrollPane2.setBounds(420,20,200,250);

        this.add(scrollPane);
        this.add(scrollPane2);

        JTextField textField = new JTextField("", 100);
        textField.setBounds(400, 300, 150, 45);
        JTextField DMtextField = new JTextField("Type the username of the user you want to message...", 150);
        DMtextField.setBounds(100, 300, 290, 45);
        JButton button = new JButton("Send");
        button.setBounds(550, 300, 80, 45);
        JButton DMButton = new JButton("DM");
        DMButton.setBounds(20, 300, 80, 45);
                
        this.add(textField); 
        this.add(DMtextField);
        this.add(button);
        this.add(DMButton);

        
        JLabel onlineUsersLabel = new JLabel("Online Users:");
        onlineUsersLabel.setBounds(430, 0, 200, 20);
        this.add(onlineUsersLabel);
        JLabel AllMessagesLabel = new JLabel("All Messages:");
        AllMessagesLabel.setBounds(30, 0, 200, 20);
        this.add(AllMessagesLabel);
                
        
        Timer timer = new Timer(1000, e -> {
            try {
                ArrayList<String> onlineUsers = ServerAPI.getOnlineUsers();

                chatArea2.setText("");

                for(String username : onlineUsers) {
                    if(!username.equals(user.getUsername())) {
                        chatArea2.append(username + "\n");
                    }
                }

                chatArea2.setCaretPosition(chatArea2.getDocument().getLength());
                revalidate();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        timer.start();
        
        button.addActionListener(e -> {
            try {
                ServerAPI.sendMessage(this.user.getUsername(), textField.getText());
                textField.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            textField.setText("");
            
        });
        Timer messagesTimer = new Timer(1000, e -> {
            try {
                ServerAPI.listenForMessages(chatArea);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        messagesTimer.start();

        DMButton.addActionListener(e -> {
            if(DMtextField.getText().isEmpty() || DMtextField.getText().equals("Type the username of the user you want to message...")) {
                return;
            } else if(DMtextField.getText().equals(this.user.getUsername())) {
                DMtextField.setText("");
                return;
            } else {
                ServerAPI.isOnline(DMtextField.getText(), (isOnline) -> {
                    if (isOnline) {
                        DMScreen dmScreen = new DMScreen("Chatter: " + user.getUsername() + "->" + DMtextField.getText(), this.user.getUsername(), DMtextField.getText());
                        dmScreen.setUser(this.user);
                        Launcher.setScreen(dmScreen);
                    } else {
                        DMtextField.setText("User is not online.");
                    }
                });
            }
        });
        try
        {
            ServerAPI.listenForMessages(chatArea);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return this.user;
    }
    
}
