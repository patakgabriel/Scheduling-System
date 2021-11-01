package Model;
/**
 * @author Gabriel Fernandez Patak
 */
public class User{

    private int id;
    private String username;
    private String password;

    /**
     * Constructor to create a user object
     */
    public User(){
        setUser();
    }
    /**
     * @return user password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }
    /**
     * @param username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * @return userID of the user
     */
    public int getUserId() {
        return id;
    }
    /**
     * @param userId of the user
     */
    public void setUserId(int userId) {
        this.id = userId;
    }
    /**
     * Method to set user variables to blank
     */
    private void setUser(){
        setUsername("");
        setPassword("");
    }
    /**
     * Method to check that username and password are not empty
     */
    public boolean valid(){
        if(this.username.equals("") || this.password.equals("")){
            return false;
        }else {
            return true;
        }
    }




}
