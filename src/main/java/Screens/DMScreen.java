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
import javax.swing.JTextField;

import core.*;
import helpers.*;
public class DMScreen extends JFrame {
    private User user;
    public DMScreen(String title) {
        super(title);
        this.setSize(650,400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null); 
        
    }
    public void initFirebase() throws Exception {
        
    } 
    public static String getConversationId(String user1, String user2) {
        if(user1.compareTo(user2) < 0)
            return user1 + "_" + user2;
        else
            return user2 + "_" + user1;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
