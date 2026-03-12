package helpers;
public class User {
    private String username;
    //private String email;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    // public String getEmail() {
    //     return email;
    // }

    public String getPassword() {
        return password;
    }    
}
