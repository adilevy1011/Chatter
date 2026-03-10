package Screens;

import javax.swing.JFrame;

public class Screen extends JFrame {
    public Screen(String title) {
        super(title);
        this.setSize(650,400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null); 
        
        try {
            initFirebase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initFirebase() throws Exception {
        
    }
    
}
