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
    /**
     * sendMessage is a method that sends a message to the server. It takes in the username of the sender and the text of the message, and it makes a POST request to the server with this information in JSON format.
     * @param username the username of the sender of the message
     * @param text the text of the message to be sent
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
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
       
    /**
     *  sendDirectMessage is a method that sends a direct message from one user to another. It constructs the conversation ID based on the sender and recipient, then makes a POST request to the server with the message content in JSON format.
     * @param sender the username of the sender of the direct message
     * @param recipient the username of the recipient of the direct message
     * @param message the text of the direct message to be sent
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static void sendDirectMessage(String sender, String recipient, String message) throws Exception {
        String conversationID = DMScreen.getConversationId(sender, recipient);
        URI uri = new URI(SERVER_URL 
            + "/sendDirectMessage?conversationID=" 
            + conversationID 
            + "&sender=" 
            + URLEncoder.encode(sender, "UTF-8")
        );
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
    /**
     * listenForMessages is a method that continuously listens for new messages from the server and updates the chat area in the user interface. It makes a GET request to the server to retrieve the latest messages, parses the JSON response, and updates the JTextArea with the formatted messages.
     * @param chatArea the JTextArea component in the user interface where the chat messages will be displayed
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
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
    /**
     * listenForMessages is a method that listens for messages in a specific conversation.
     * @param chatArea the JTextArea component in the user interface where the chat messages will be displayed
     * @param conversationId the ID of the conversation to listen for messages in
     * @param currentUser the username of the current user
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static void listenForMessages(JTextArea chatArea, String conversationId, String currentUser) throws Exception {
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
    /**
     * isOnline is a method that checks if a user is currently online by making a GET request to the server to retrieve the list of online users, then checking if the specified username is in that list. The result is returned asynchronously through a callback function.
     * @param username the username of the user to check for online status
     * @param callback a Consumer<Boolean> callback function that will be called with the result of the online status check (true if the user is online, false otherwise)
     */
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

    /**
     * getOnlineUsers is a method that retrieves the list of currently online users from the server by making a GET request to the appropriate endpoint. The response is expected to be a JSON array of usernames, which is parsed and returned as an ArrayList<String>.
      * @return an ArrayList<String> containing the usernames of all currently online users
      * @throws Exception if there is an error with the network request or if the server returns an error response  
    */
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
    /**
     * createNewUser is a method that creates a new user account on the server by making a POST request to the appropriate endpoint with the provided username and password. The server is expected to handle the creation of the user account and return an appropriate response.
     * @param username the desired username for the new user account
     * @param password the desired password for the new user account
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
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
    /**
     * setMessageRead is a method that marks a specific message in a conversation as read by making a PUT request to the server with the conversation ID, message ID, and sender's username as parameters. The server is expected to update the read status of the message accordingly.
     * @param conversationID the ID of the conversation that contains the message to be marked as read
     * @param messageID the ID of the message to be marked as read
     * @param sender the username of the sender of the message to be marked as read
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static void setMessageRead(String conversationID, String messageID, String sender) throws Exception {

        URI uri = new URI(SERVER_URL 
            + "/conversations/" 
            + URLEncoder.encode(conversationID, "UTF-8") 
            + "/messages/" 
            + URLEncoder.encode(messageID, "UTF-8") 
            + "/read?sender=" 
            + URLEncoder.encode(sender, "UTF-8")
        );
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // No body needed, but some servers expect a stream
        conn.getOutputStream().write(new byte[0]);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed to mark message as read. Code: " + responseCode);
        }

        conn.getInputStream().close();
    }
    /**
     * getUnreadCount is a method that retrieves the count of unread messages in a specific conversation for the current user by making a GET request to the server with the conversation ID and current user's username as parameters. The server is expected to return a JSON response containing the unread message count, which is parsed and returned as an integer.
     * @param conversationID the ID of the conversation for which to retrieve the unread message count
     * @param currentUser the username of the current user for whom to retrieve the unread message count
     * @return an integer representing the count of unread messages in the specified conversation for the current user
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static int getUnreadCount(String conversationID, String currentUser) throws Exception {

        URI uri = new URI(SERVER_URL 
            + "/conversations/" 
            + URLEncoder.encode(conversationID, "UTF-8") 
            + "/unreadCount?user=" + URLEncoder.encode(currentUser, "UTF-8")
        );

        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

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

        // Parse JSON into a simple map
        java.util.Map<String, Double> result = gson.fromJson(
            response.toString(),
            new TypeToken<java.util.Map<String, Double>>(){}.getType()
        );

        // Gson parses numbers as Double by default
        return result.get("unreadCount").intValue();
    }
    /**
     * getUnreadMessages is a method that retrieves the list of unread messages in a specific conversation for the current user by making a GET request to the server with the conversation ID and current user's username as parameters. The server is expected to return a JSON response containing the list of messages, which is parsed and returned as an ArrayList<Message>.
     * @param conversationID the ID of the conversation for which to retrieve the unread messages
     * @param currentUser the username of the current user for whom to retrieve the unread messages
     * @return an ArrayList<Message> containing the unread messages in the specified conversation for the current user
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static ArrayList<Message> getUnreadMessages(String conversationID, String currentUser) throws Exception {

        URI uri = new URI(SERVER_URL + "/conversation?conversationID=" + URLEncoder.encode(conversationID, "UTF-8"));
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

        List<Message> messages = gson.fromJson(
            response.toString(),
            new TypeToken<List<Message>>(){}.getType()
        );

        ArrayList<Message> unreadMessages = new ArrayList<>();

        String user1 = conversationID.split("_")[0];
        boolean isUser1 = currentUser.equals(user1);

        for (Message msg : messages) {

            if (msg.username.equals(currentUser)) continue;

            if ((isUser1 && !msg.readForUser1) || (!isUser1 && !msg.readForUser2)) {
                unreadMessages.add(msg);
            }
        }

        return unreadMessages;
    }
    /**
     * setAllMessagesRead is a method that marks all unread messages in a specific conversation as read for the current user by first retrieving the list of unread messages using the getUnreadMessages method, and then iterating through the list and calling the setMessageRead method for each unread message to update its read status on the server.
     * @param conversationID the ID of the conversation for which to mark all messages as read
     * @param currentUser the username of the current user for whom to mark all messages as read
     * @throws Exception if there is an error with the network request or if the server returns an error response during the retrieval of unread messages or while marking messages as read
     */
    public static void setAllMessagesRead(String conversationID, String currentUser) throws Exception {
        ArrayList<Message> unreadMessages = getUnreadMessages(conversationID, currentUser);

        for (Message msg : unreadMessages) {
            setMessageRead(conversationID, msg.messageID, currentUser);
        }
    }
    /**
     * login is a method that attempts to log in a user by making a POST request to the server with the provided username and password as parameters. The server is expected to validate the credentials and return an appropriate response indicating whether the login was successful or not.
     * @param username the username of the user attempting to log in
     * @param password the password of the user attempting to log in
     * @return a string indicating the result of the login attempt
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
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
    /**
     * setUserOnline is a method that sets a user's online status to true by making a POST request to the server with the provided username as a parameter. The server is expected to update the user's online status accordingly and return an appropriate response.
     * @param username the username of the user whose online status is to be set to true
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static void setUserOnline(String username) throws Exception{
        URI uri = new URI(SERVER_URL+"/setOnline?username=" + URLEncoder.encode(username, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.getInputStream().close();
    }
    /**
     * setUserOffline is a method that sets a user's online status to false by making a POST request to the server with the provided username as a parameter. The server is expected to update the user's online status accordingly and return an appropriate response.
     * @param username the username of the user whose online status is to be set to false
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static void setUserOffline(String username) throws Exception {
        URI uri = new URI(SERVER_URL+"/setOffline?username=" + URLEncoder.encode(username, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.getInputStream().close();
    }
    /**
     * checkUserExists is a method that checks if a user with the specified username exists on the server by making a GET request to retrieve the list of all users, then parsing the response and checking if the provided username is in that list. The method returns true if the user exists and false otherwise.
     * @param username the username of the user to check for existence on the server
     * @return a boolean value indicating whether the user with the specified username exists on the server (true if the user exists, false otherwise)
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static boolean checkUserExists(String username) throws Exception {
        
        // Build URI
        URI uri = new URI(SERVER_URL + "/UserDetails");
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        // Read response
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Clean and parse JSON array
        String resp = response.toString().trim();
        if (resp.startsWith("[") && resp.endsWith("]")) {
            resp = resp.substring(1, resp.length() - 1); // remove [ and ]
            if (!resp.isEmpty()) {
                String[] users = resp.split(",");
                for (String user : users) {
                    // Remove quotes and whitespace
                    if (user.trim().replaceAll("\"", "").equals(username)) {
                        return true;
                    }
                }
            }
        }
        return false;

    }
    /**
     * heartbeat is a method that sends a heartbeat signal to the server to indicate that the user is still active. It makes a POST request to the server with the provided username as a parameter, allowing the server to update the user's last active timestamp and maintain an accurate list of online users.
     * @param username the username of the user for whom to send the heartbeat signal to the server
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static void heartbeat(String username) throws Exception {
        URI uri = new URI(SERVER_URL+"/heartbeat?username=" + URLEncoder.encode(username, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.getInputStream().close();
    }
    /**
     * getExistingUsers is a method that retrieves the list of all existing users from the server by making a GET request to the appropriate endpoint. The response is expected to be a JSON array of usernames, which is parsed and returned as an ArrayList<String>.
     * @return an ArrayList<String> containing the usernames of all existing users on the server
     * @throws Exception if there is an error with the network request or if the server returns an error response
     */
    public static ArrayList<String> getExistingUsers() throws Exception {
        ArrayList<String> existingUsers = new ArrayList<>();

        try {
            URI uri = new URI(SERVER_URL+"/UserDetails");
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
                        existingUsers.add(user.trim().replaceAll("\"", ""));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching existing users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return existingUsers;
    }
}
    
