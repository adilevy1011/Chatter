package Screens;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.function.Consumer;

import core.*;
import helpers.*;
public class DMScreen extends ChatScreen {
    private DatabaseReference ref;
    public DMScreen(String title, String user1, String user2)  {
        super(title);    
        ref = FirebaseDatabase.getInstance().getReference("conversations").child(getConversationId(user1, user2));
        
    }

    public void initFirebase() throws Exception {
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(20,20,400,250);

        this.add(scrollPane);

        JTextField textField = new JTextField("", 100);
        textField.setBounds(400, 300, 150, 45);

        JButton button = new JButton("Send");
        button.setBounds(550, 300, 80, 45);
        this.add(button);
        this.add(textField);
        JLabel MessagesLabel = new JLabel("Messages with " + this.getTitle().split("->")[1] + ":");
        MessagesLabel.setBounds(30, 0, 200, 20);
        this.add(MessagesLabel);

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
        button.addActionListener(e -> {
            String text = textField.getText().trim();
            if(!text.isEmpty()) {
                Message newMsg = new Message(getUser().getUsername(), text);
                ref.push().setValueAsync(newMsg);
                textField.setText("");
            }
        });
            
    } 
    

    public static String getConversationId(String user1, String user2) {
        if(user1.compareTo(user2) < 0)
            return user1 + "_" + user2;
        else
            return user2 + "_" + user1;
    }
    
}
