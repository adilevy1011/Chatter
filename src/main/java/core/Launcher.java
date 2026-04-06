package core;

import Screens.*;

public class Launcher {
    private static Screen currentScreen;
    public static void main(String[] args) throws Exception {
        //FireBaseInit.init();
        //python -m uvicorn server:app --host 0.0.0.0 --port 8000 --reload
        setScreen(new MenuScreen("Chatter-Menu"));
    }

    public static void setCurrentScreen(Screen screen) {
        currentScreen = screen;
    }
    public static void setScreen(Screen newScreen) {

        Screen current = getCurrentScreen();

        if (current != null) {
            current.setVisible(false);
            current.dispose();
        }

        setCurrentScreen(newScreen);

        try {
            newScreen.initFirebase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        newScreen.setVisible(true);
    }
    
    
    public static Screen getCurrentScreen() {
        return currentScreen;
    }
}