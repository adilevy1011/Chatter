package Screens;

import javax.swing.JFrame;

public abstract class Screen extends JFrame {
    public Screen(String title) {
        super(title);
        this.setSize(650,400);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(null); 
        
        
    }
    /**
     * initScreen is a method that initializes the screen by setting up the necessary components and layout for the user interface. This method can be overridden by subclasses to provide specific implementations for different screens in the application.
     * @throws Exception if there is an error during the initialization of the screen, such as issues with loading resources or setting up components
     */
    public abstract void initScreen() throws Exception;
    
}
