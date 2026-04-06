package Screens;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import helpers.*;
import core.*;

public class MenuScreen extends Screen {

    
    public MenuScreen(String title) {
        super(title);
    }    
    public void initFirebase() throws Exception{
        JLabel welcomeLabel = new JLabel("Welcome to Chatter!");
        welcomeLabel.setBounds(10, 10, 200, 30);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 60, 200, 30);
        JTextField usernameField = new JTextField("", 100);
        usernameField.setBounds(100, 60, 200, 35);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 160, 200, 30);
        JTextField emailField = new JTextField("", 100);
        emailField.setBounds(100, 160, 200, 35);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 100, 200, 30);
        JTextField passwordField = new JTextField("", 100);
        passwordField.setBounds(100, 100, 200, 35);

        JButton button = new JButton("Continue");
        button.setBounds(400, 300, 180, 45);
        
        button.addActionListener(e ->{
                    
                User newUser = new User(usernameField.getText(),passwordField.getText());
                    //================================================================
                    // Check if the user exists in the database
                try{
                    String result = ServerAPI.login(newUser.getUsername(), newUser.getPassword());

                    if(result.contains("wrong_password")) {
                        JOptionPane.showMessageDialog(null, "Incorrect password, please try again.");
                    }
                    else {
                        ChatScreen chatScreen = new ChatScreen("Chatter: " + newUser.getUsername()+ " -> All Users");
                        chatScreen.setUser(newUser);
                        Launcher.setScreen(chatScreen);          
                        ServerAPI.setUserOnline(newUser.getUsername());       
                        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                            try {
                                ServerAPI.setUserOffline(newUser.getUsername());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }));       
                    }
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Failed to connect to server. Please ensure the FastAPI server is running on " + ServerAPI.SERVER_URL + ".");
                    ex.printStackTrace();
                }
                
                           
                    //================================================================
                    
                    //================================================================
                

        });

        this.add(welcomeLabel);
        this.add(usernameLabel);
        this.add(usernameField);
        
      
        this.add(passwordLabel);
        this.add(passwordField);
        
        this.add(button);
    }
    
}
