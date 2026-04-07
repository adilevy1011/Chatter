package Screens;

import javax.swing.JFrame;

public class Screen extends JFrame {
    public Screen(String title) {
        super(title);
        this.setSize(650,400);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(null); 
        
        
    }

    public void initFirebase() throws Exception {
        
    }
    
}
