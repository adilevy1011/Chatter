package core;

import javax.swing.JFrame;
import Screens.*;
public class Launcher {
    private static JFrame currentScreen = new MenuScreen("Chatter-Menu");
    public static void main(String[] args) throws Exception {
        setScreen(currentScreen);
    }
    public static void setScreen(JFrame frame) {
        
        currentScreen = frame;
    }
    
    
    public static JFrame getCurrentScreen() {
        return currentScreen;
    }
}