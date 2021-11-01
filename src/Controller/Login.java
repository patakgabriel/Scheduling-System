package Controller;
/**
 * @author Gabriel Fernandez Patak
 */

import Main.Main;
import Model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class to handle login form
 */
public class Login implements Initializable {

    private final File loginAttempts = new File("src\\login_activity.txt");
    public static Locale userLocale;
    public static ZoneId userZone;
    public static ResourceBundle resourceBundle;
    private Main main;
    private final static Connection conn = Main.conn;

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button loginB;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private Label region;


    /**
     * Method that sets login stage to main class stage.
     *
     * @param stage
     */
    public void setMain(Main stage) {
        this.main = stage;
    }

    /**
     * Method that validates user credentials and writes attempt to text file.
     */
    @FXML
    private void handleLogin() throws IOException {


        String loginUsername = username.getText();
        String loginPassword = password.getText();

        ZonedDateTime attemptTime = ZonedDateTime
                .now(Main.userZone)
                .with(LocalDateTime.now())
                .withZoneSameInstant(ZoneOffset.UTC);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = attemptTime.format(formatter);
        BufferedWriter myWriter = new BufferedWriter(new FileWriter(loginAttempts, true));

        User loginUser = new User();
        loginUser.setUsername(loginUsername);
        loginUser.setPassword(loginPassword);

        if (!loginUser.valid()) {
            new Exceptions(2);
            return;
        }

        String getUserQuery = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(getUserQuery);
            stmt.setString(1, loginUsername);
            stmt.setString(2, loginPassword);
            ResultSet resultQuery = stmt.executeQuery();

            if (resultQuery.next()) {
                loginUser.setUsername(resultQuery.getString("User_Name"));
                loginUser.setPassword(resultQuery.getString("Password"));
                loginUser.setUserId(resultQuery.getInt("User_ID"));
                this.main.setUser(loginUser);
                myWriter.write(loginUser.getUsername() + "\t" + formatDateTime + " UTC\tATTEMPT SUCCESSFUL\n");
            } else {
                new Exceptions(1);

                myWriter.write(loginUser.getUsername() + "\t" + formatDateTime + " UTC\tATTEMPT FAILED\n");


            }
            myWriter.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }


    }

    /**
     * Sets proper language
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        userLocale = Locale.getDefault();
        userZone = ZoneId.systemDefault();

        this.region.setText(userZone.toString());

        Login.resourceBundle = ResourceBundle.getBundle("myResource", userLocale);

        usernameLabel.setText(Login.resourceBundle.getString("username"));
        passwordLabel.setText(Login.resourceBundle.getString("password"));
        loginB.setText(Login.resourceBundle.getString("login"));
        regionLabel.setText(Login.resourceBundle.getString("region") + ":");

    }
}
