package helpers;

public class Message {
    public String username;
    public String text;
    public long timestamp;


    public Message(String username, String text) {
        this.username = username;
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }
    public Message() {
    }
  
}
