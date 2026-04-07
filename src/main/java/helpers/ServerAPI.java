package helpers;

import com.google.gson.Gson;

import java.util.function.Consumer;

import com.google.gson.reflect.TypeToken;
import java.util.List;

import javax.swing.JTextArea;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;


import Screens.*;
public class ServerAPI {
    
    public static final String SERVER_URL = initServerUrl();

    private static String initServerUrl() {
        String url = System.getProperty("server.url");
        if (url == null || url.isEmpty()) {
            url = System.getenv("SERVER_URL");
        }
        if (url == null || url.isEmpty()) {
            url = "http://localhost:8000";
        }
        return url;
    }
    //Done
    public static void sendMessage(String username, String text) throws Exception {
        URI uri = new URI(SERVER_URL+"/sendMessage");
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String json = String.format("{\"username\":\"%s\",\"text\":\"%s\"}", username, text);

        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();
        conn.getInputStream();
        
    }
       
    //Done
    public static void sendDirectMessage(String sender, String recipient, String message) throws Exception {
        String conversationID = DMScreen.getConversationId(sender, recipient);
        URI uri = new URI(SERVER_URL+"/sendDirectMessage?conversationID=" + conversationID);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String json = String.format("{\"username\":\"%s\",\"text\":\"%s\"}", sender, message);

        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();
        conn.getInputStream();
        
    }
    //Done
    public static void listenForMessages(JTextArea chatArea) throws Exception {
        URI uri = new URI(SERVER_URL+"/messages");
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        Gson gson = new Gson();

        List<Message> messages = gson.fromJson(response.toString(), new TypeToken<List<Message>>(){}.getType());
        chatArea.setText("");
        for (Message msg : messages) {
            chatArea.append(formatTimestamp(msg.timestamp) + " " + msg.username + ": " + msg.text + "\n");
        }
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
        chatArea.revalidate();
        chatArea.repaint();
        
    }
    //Done
    public static void listenForMessages(JTextArea chatArea, String conversationId) throws Exception {

        URI uri = new URI(SERVER_URL+"/conversation?conversationID=" + conversationId);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        Gson gson = new Gson();

        List<Message> messages = gson.fromJson(response.toString(), new TypeToken<List<Message>>(){}.getType());
        chatArea.setText("");
        for (Message msg : messages) {
            chatArea.append(formatTimestamp(msg.timestamp) + " " + msg.username + ": " + msg.text + "\n");
        }
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
        chatArea.revalidate();
        chatArea.repaint();
        

    }

    private static String formatTimestamp(double timestamp) {
        long timeMillis = (long) timestamp;
        if (timeMillis < 1_000_000_000_000L) {
            timeMillis *= 1000;
        }
        Date date = new Date(timeMillis);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String timePart = timeFormat.format(date);
        String todayPart = dateFormat.format(new Date());
        String messageDay = dateFormat.format(date);
        if (todayPart.equals(messageDay)) {
            return "[" + timePart + "]";
        }
        return "[" + messageDay + " " + timePart + "]";
    }

    public static void isOnline(String username, Consumer<Boolean> callback) {

        new Thread(() -> {
            try {

                URI uri = new URI(SERVER_URL+"/onlineUsers");
                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                Gson gson = new Gson();
                List<String> users = gson.fromJson(
                    response.toString(),
                    new TypeToken<List<String>>(){}.getType()
                );

                callback.accept(users.contains(username));

            } catch (Exception e) {
                callback.accept(false);
            }

        }).start();
    }

    //Done
    public static ArrayList<String> getOnlineUsers() throws Exception {
        ArrayList<String> onlineUsers = new ArrayList<>();

        URI uri = new URI(SERVER_URL+"/onlineUsers");
        URL url = uri.toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        InputStream in = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String resp = response.toString().trim();
        if (resp.startsWith("[") && resp.endsWith("]")) {
            resp = resp.substring(1, resp.length() - 1); // remove [ and ]
            if (!resp.isEmpty()) {
                String[] users = resp.split(",");
                for (String user : users) {
                    onlineUsers.add(user.trim().replaceAll("\"", ""));
                }
            }
        }

        return onlineUsers;
    }

    public static void createNewUser(String username, String password) throws Exception {
        URI uri = new URI(SERVER_URL+"/newUser/username?=" + username + "password=" + password);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        conn.getInputStream();
        // DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserDetails").child(username);
        // User newUser = new User(username, password);
        // ref.setValueAsync(newUser);
    }

    public static String login(String username, String password) throws Exception {

        URI uri = new URI(SERVER_URL+"/login?username=" + username + "&password=" + password);
        
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        String response = reader.readLine();
        reader.close();

        return response;
    }
    public static void setUserOnline(String username) throws Exception{
        URI uri = new URI(SERVER_URL+"/setOnline?username=" + URLEncoder.encode(username, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.getInputStream().close();
    }

    public static void setUserOffline(String username) throws Exception {
        URI uri = new URI(SERVER_URL+"/setOffline?username=" + URLEncoder.encode(username, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.getInputStream().close();
    }

    public static void heartbeat(String username) throws Exception {
        URI uri = new URI(SERVER_URL+"/heartbeat?username=" + URLEncoder.encode(username, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.getInputStream().close();
    }
}
    
