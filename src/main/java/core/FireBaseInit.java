package core;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;


public class FireBaseInit {

    public static void init() throws Exception {

        FileInputStream serviceAccount = new FileInputStream("firebase/serviceAccountKey.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://chatter1-74374-default-rtdb.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);
    }
    
}
