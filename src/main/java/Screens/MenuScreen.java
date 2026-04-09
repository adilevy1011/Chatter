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
    public void initScreen() throws Exception{
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
                        if(result.contains("\"status\":\"created\"")) {
                            JOptionPane.showMessageDialog(null, "Congratulations! Your account has been created.");
                        }

                        ChatScreen chatScreen = new ChatScreen("Chatter: " + newUser.getUsername()+ " | Main Page");
                        chatScreen.setUser(newUser);
                        Launcher.setScreen(chatScreen);          
                        ServerAPI.setUserOnline(newUser.getUsername());
                    }
                } catch(Exception ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to connect to server. Please ensure the FastAPI server is running on " + ServerAPI.SERVER_URL + ".");
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
        
        // Add window listener for proper close handling
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
    }
    
}
