package Screens;

import com.google.api.client.util.Data;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import helpers.*;
import core.*;
public class ChatScreen extends JFrame{
    private User user;
    public ChatScreen(String title)  {
        super(title);
        this.setSize(650,400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null); 
        
        
    }
    public void initFirebase() throws Exception{
        JTextField textField = new JTextField("", 100);
        textField.setBounds(300, 300, 250, 45);
        JButton button = new JButton("Send");
        button.setBounds(560, 300, 80, 45);
                
        this.add(textField); 
        this.add(button);
                
        this.setVisible(true);
                
        FireBaseInit.init();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages");
        
        //================================================================
        // DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // DatabaseReference onlineRef = database.child("onlineUsers").child(this.user.getUsername());
        // DatabaseReference connectedRef = database.child(".info/connected");

        // connectedRef.addValueEventListener(new ValueEventListener() {
        //     @Override
        //     public void onDataChange(DataSnapshot snapshot) {
        //         Boolean connected = snapshot.getValue(Boolean.class);

        //         if (connected != null && connected) {
        //             onlineRef.setValue(true);

        //             // remove user automatically when they disconnect
        //             onlineRef.onDisconnect().removeValue();
        //         }
        //     }

        //     @Override
        //     public void onCancelled(DatabaseError error) {}
        // });
        //================================================================
        button.addActionListener(e -> {
            Message newMsg = new Message(this.user.getUsername(), textField.getText());
            ref.push().setValueAsync(newMsg);
            textField.setText("");
        });

        ref.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot snapshot, String prevChildKey) {    
                Message newMsg = snapshot.getValue(Message.class);
                JLabel label = new JLabel(newMsg.username + ": " + newMsg.text);
                label.setBounds(10, -30 + 30 * getContentPane().getComponentCount(), 600, 30);
                add(label);
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
    
}
