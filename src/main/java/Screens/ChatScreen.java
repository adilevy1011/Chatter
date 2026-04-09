package Screens;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import helpers.*;
import core.*;

public class ChatScreen extends Screen{
    private User user;
    public ChatScreen(String title)  {
        super(title);
        
    }
    public void initScreen() throws Exception{

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);

        JTextArea chatArea2 = new JTextArea();
        chatArea2.setEditable(false);

        JTextArea chatArea3 = new JTextArea();
        chatArea3.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(20,20,300,250);

        JScrollPane scrollPane2 = new JScrollPane(chatArea2);
        scrollPane2.setBounds(320,20,100,250);

        JScrollPane scrollPane3 = new JScrollPane(chatArea3);
        scrollPane3.setBounds(420,20,210,250);

        this.add(scrollPane);
        this.add(scrollPane2);
        this.add(scrollPane3);


        JTextField textField = new JTextField("", 100);
        textField.setBounds(400, 300, 150, 45);
        JTextField DMtextField = new JTextField("", 150);
        DMtextField.setBounds(100, 300, 290, 45);
        JButton button = new JButton("Send");
        button.setBounds(550, 300, 80, 45);
        JButton DMButton = new JButton("DM");
        DMButton.setBounds(20, 300, 80, 45);
                
        this.add(textField); 
        this.add(DMtextField);
        this.add(button);
        this.add(DMButton);

        
        JLabel onlineUsersLabel = new JLabel("Online Users");
        onlineUsersLabel.setBounds(320, 0, 100, 20);
        this.add(onlineUsersLabel);
        JLabel unreadMessagesLabel = new JLabel("Private Messages:");
        unreadMessagesLabel.setBounds(420, 0, 150, 20);
        this.add(unreadMessagesLabel);
        JLabel AllMessagesLabel = new JLabel("All Messages:");
        AllMessagesLabel.setBounds(30, 0, 150, 20);
        this.add(AllMessagesLabel);
        
        JLabel typeUserToDmLabel = new JLabel("Type the username of the user you want to message:");
        typeUserToDmLabel.setBounds(20, 280, 300, 20);
        this.add(typeUserToDmLabel);
        
        Timer OnlineUserstimer = new Timer(1000, e -> {
            try {
                ServerAPI.heartbeat(user.getUsername());
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

        OnlineUserstimer.start();
        
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
                ServerAPI.heartbeat(user.getUsername());
                ArrayList<String> existingUsers = ServerAPI.getExistingUsers();

                chatArea3.setText("");

                if (existingUsers != null) {
                    for(String username : existingUsers) {
                        if(!username.equals(user.getUsername())) {
                            int unreadCount = ServerAPI.getUnreadCount(DMScreen.getConversationId(user.getUsername(), username),this.getUser().getUsername());
                            chatArea3.append(username + " (" + unreadCount + " unread)\n");
                        }
                    }
                }

                chatArea3.setCaretPosition(chatArea3.getDocument().getLength());
                revalidate();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        messagesTimer.start();

        
        DMButton.addActionListener(e -> {
            if(DMtextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username to DM.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if(DMtextField.getText().equals(this.user.getUsername())) {
                DMtextField.setText("");
                return;
            } else {
                try{
                    if (ServerAPI.checkUserExists(DMtextField.getText())) {
                    DMScreen dmScreen = new DMScreen("Chatter: " + user.getUsername() + "->" + DMtextField.getText(), this.user.getUsername(), DMtextField.getText());
                    dmScreen.setUser(this.user);
                    Launcher.setScreen(dmScreen);
                    } else {
                        JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        DMtextField.setText("");
                    }
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
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
        
        // Register window listener to handle offline when closing
        if (user != null) {
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        ServerAPI.setUserOffline(user.getUsername());
                        Thread.sleep(500);
                        dispose();
                        System.exit(0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.exit(0);
                    }
                }
            });
        }
    }
    /**
     * setUser is a method that assigns the provided User object to the user field of the ChatScreen class, allowing the screen to have access to the user's information and perform actions based on that user context.
     * @param user the User object to be assigned to the user field
     */
    public void setUser(User user) {
        this.user = user;
    }
    /**
     * getUser is a method that returns the User object associated with the ChatScreen, allowing other parts of the application to access the user's information and perform actions based on that user context.
     * @return the User object associated with the ChatScreen
     */
    public User getUser() {
        return this.user;
    }
    
}
