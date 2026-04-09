package core;

import Screens.*;

public class Launcher {
    private static Screen currentScreen;
    public static void main(String[] args) throws Exception {
        //FireBaseInit.init();
        //python -m uvicorn server:app --host 0.0.0.0 --port 8000 --reload
        setScreen(new MenuScreen("Chatter-Menu"));
    }
    /**
     * setCurrentScreen is a method that sets the current screen of the application to the provided Screen object. It takes a Screen object as a parameter and assigns it to the static variable currentScreen, allowing the application to keep track of which screen is currently being displayed to the user.
     * @param screen the Screen object that represents the new screen to be set as the current screen of the application
     */
    public static void setCurrentScreen(Screen screen) {
        currentScreen = screen;
    }
    /**
     * setScreen is a method that changes the current screen of the application to a new screen. It first retrieves the current screen using the getCurrentScreen method, and if there is an existing screen, it hides and disposes of it. Then, it sets the new screen as the current screen using the setCurrentScreen method, initializes Firebase for the new screen, and finally makes the new screen visible to the user.
     * @param newScreen the Screen object that represents the new screen to be displayed in the application. This method handles the transition from the current screen to the new screen, ensuring that resources are properly managed and that the new screen is initialized before being shown to the user.
     */
    public static void setScreen(Screen newScreen) {

        Screen current = getCurrentScreen();

        if (current != null) {
            current.setVisible(false);
            current.dispose();
        }

        setCurrentScreen(newScreen);

        try {
            newScreen.initScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }

        newScreen.setVisible(true);
    }
    
    /**
     * getCurrentScreen is a method that retrieves the current screen of the application. It returns the Screen object that is currently set as the active screen, allowing other parts of the application to access and interact with the current screen as needed.
     * @return the Screen object that represents the current screen of the application, or null if no screen is currently set
     */
    public static Screen getCurrentScreen() {
        return currentScreen;
    }
}