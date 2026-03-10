package Screens;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import helpers.*;
import core.*;

public class MenuScreen extends Screen {

    private ArrayList<String> onlineUsers = new ArrayList<>();
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
                    chatScreen.setUser(newUser);
                    Launcher.setScreen(chatScreen);
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
        
        // this.add(emailLabel);
        // this.add(emailField);
        // this.add(passwordLabel);
        // this.add(passwordField);
        
        this.add(button);
        
        //this.setVisible(true);
        
    }
    // private boolean usernameExists(String username) {
    //     ArrayList<String> LocalOnlineUsers = new ArrayList<>();
    //     DatabaseReference onlineUsersRef = FirebaseDatabase.getInstance().getReference("onlineUsers");
    //     onlineUsersRef.addValueEventListener(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot snapshot) {
    //             for (DataSnapshot userSnapshot : snapshot.getChildren()) {
    //                 if(userSnapshot.getKey().equals(username)) {
    //                     LocalOnlineUsers.add(userSnapshot.getKey());
    //                 }
    //             }
    //         }
            
    //         @Override
    //         public void onCancelled(DatabaseError error) {
    //             System.out.println("Failed to read online users: " + error.getMessage());
    //         }
    //     });
    //     if(LocalOnlineUsers.size() > 0) {
    //         return true;
    //     }
    //     return false;
    // }
    // private ArrayList<String> getOnlineUsers() {
    //     ArrayList<String> LocalOnlineUsers = new ArrayList<>();
    //     DatabaseReference onlineUsersRef = FirebaseDatabase.getInstance().getReference("onlineUsers");
    //     onlineUsersRef.addValueEventListener(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot snapshot) {
    //             for (DataSnapshot userSnapshot : snapshot.getChildren()) {
    //                 LocalOnlineUsers.add(userSnapshot.getKey());
    //             }
    //         }
            
    //         @Override
    //         public void onCancelled(DatabaseError error) {
    //             System.out.println("Failed to read online users: " + error.getMessage());
    //         }
    //     });
    //     return LocalOnlineUsers;
    // }
}
