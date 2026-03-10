package Screens;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.function.Consumer;
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
                
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages");
        DatabaseReference onlineUsersRef = FirebaseDatabase.getInstance().getReference("onlineUsers");
        onlineUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                chatArea2.setText(""); 
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if(!userSnapshot.getKey().equals(user.getUsername())) {
                        chatArea2.setText(chatArea2.getText() + userSnapshot.getKey() + "\n");
                    }
                }
                
                chatArea2.setCaretPosition(chatArea2.getDocument().getLength());
                revalidate();
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Failed to read online users: " + error.getMessage());
            }
        });
        button.addActionListener(e -> {
            Message newMsg = new Message(this.user.getUsername(), textField.getText());
            ref.push().setValueAsync(newMsg);
            textField.setText("");
            
        });

        DMButton.addActionListener(e -> {
            if(DMtextField.getText().isEmpty() || DMtextField.getText().equals("Type the username of the user you want to message...")) {
                return;
            } else if(DMtextField.getText().equals(this.user.getUsername())) {
                DMtextField.setText("");
                return;
            } else {
                isOnline(DMtextField.getText(), (isOnline) -> {
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
        
        ref.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot snapshot, String prevChildKey) {    
                Message newMsg = snapshot.getValue(Message.class);
                chatArea.append(newMsg.username + ": " + newMsg.text + "\n");
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
                revalidate();
                repaint();
            }
            public void onChildChanged(DataSnapshot snapshot, String prevChildKey) {}
            public void onChildRemoved(DataSnapshot snapshot) {}
            public void onChildMoved(DataSnapshot snapshot, String prevChildKey) {}
            public void onCancelled(DatabaseError error) {}
        });
            

    }
    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return this.user;
    }
    private void isOnline(String username, Consumer<Boolean> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
            .getReference("onlineUsers")
            .child(username);

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
}
