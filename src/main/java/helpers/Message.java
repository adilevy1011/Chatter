package helpers;

public class Message {
    public String username;
    public String text;
    public double timestamp;
    public String messageID;
    public boolean readForUser1;
    public boolean readForUser2;

    public Message(String username, String text) {
        this.username = username;
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }
    public Message() {
    }
  
}
