package Screens;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

        // JLabel emailLabel = new JLabel("Email:");
        // emailLabel.setBounds(10, 100, 200, 30);
        // JTextField emailField = new JTextField("", 100);
        // emailField.setBounds(100, 100, 200, 35);

        // JLabel passwordLabel = new JLabel("Password:");
        // passwordLabel.setBounds(10, 100, 200, 30);
        // JTextField passwordField = new JTextField("", 100);
        // passwordField.setBounds(100, 100, 200, 35);

        JButton button = new JButton("Continue");
        button.setBounds(400, 300, 180, 45);
        
        button.addActionListener(e ->{

                User newUser = new User(usernameField.getText());
                ChatScreen chatScreen = new ChatScreen("Chatter: " + newUser.getUsername()+ " -> All Users");
                Launcher.getCurrentScreen().setVisible(false);
                Launcher.getCurrentScreen().dispose();
                chatScreen.setUser(newUser);
                Launcher.setScreen(chatScreen);

        });

        this.add(welcomeLabel);
        this.add(usernameLabel);
        this.add(usernameField);

        // this.add(emailLabel);
        // this.add(emailField);
        // this.add(passwordLabel);
        // this.add(passwordField);
        
        this.add(button);
        
        this.setVisible(true);
        
    }
}
