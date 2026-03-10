package Screens;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.checkerframework.checker.units.qual.m;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.api.client.util.Data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.function.Consumer;

import helpers.*;
import core.*;

public class MenuScreen extends Screen {

    
    public MenuScreen(String title) {
        super(title);
    }    
    public void initFirebase() throws Exception{
        JLabel welcomeLabel = new JLabel("Welcome to Chatter!");
        welcomeLabel.setBounds(10, 10, 200, 30);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserDetails");

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 60, 200, 30);
        JTextField usernameField = new JTextField("", 100);
        usernameField.setBounds(100, 60, 200, 35);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 100, 200, 30);
        JTextField passwordField = new JTextField("", 100);
        passwordField.setBounds(100, 100, 200, 35);

        JButton button = new JButton("Continue");
        button.setBounds(400, 300, 180, 45);
        
        button.addActionListener(e ->{
            
                    User newUser = new User(usernameField.getText(), passwordField.getText());    
                    //================================================================
                    // Check if the user exists in the database
                    userExists(usernameField.getText(), (exists) -> {
                        System.out.println("User exists: " + exists);
                        if (exists) {
                            passwordMatches(usernameField.getText(), passwordField.getText(), (matches) -> {
                                System.out.println("Password matches: " + matches);
                                if (matches) {                
                                    ChatScreen chatScreen = new ChatScreen("Chatter: " + newUser.getUsername()+ " -> All Users");
                                    chatScreen.setUser(newUser);
                                    Launcher.setScreen(chatScreen);
                                    
                                } else{
                                    JOptionPane.showMessageDialog(null, "Incorrect password, please try again.");
                                }
                        });} else {
                            ref.child(usernameField.getText()).setValueAsync(newUser);
                            ChatScreen chatScreen = new ChatScreen("Chatter: " + newUser.getUsername()+ " -> All Users");
                            chatScreen.setUser(newUser);
                            Launcher.setScreen(chatScreen);
                        }
                    });
                    
                    //================================================================
                    DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference("onlineUsers").child(newUser.getUsername());

                    // Mark the user as online
                    onlineRef.setValueAsync(true);

                    // Ensure this user is removed when they disconnect
                    onlineRef.onDisconnect().removeValueAsync();

                    //================================================================
                

        });

        this.add(welcomeLabel);
        this.add(usernameLabel);
        this.add(usernameField);
        
      
        this.add(passwordLabel);
        this.add(passwordField);
        
        this.add(button);
    }
    
    public void userExists(String username, Consumer<Boolean> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserDetails").child(username);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                callback.accept(snapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.accept(false);
            }
        });
    }

    public void passwordMatches(String username, String password, Consumer<Boolean> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserDetails").child(username);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String storedPassword = snapshot.child("password").getValue(String.class);
                    callback.accept(password.equals(storedPassword));
                } else {
                    callback.accept(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.accept(false);
            }
        });
    }       
}
