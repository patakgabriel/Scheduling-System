package Main;
/**
 * @author Gabriel Fernandez Patak
 */

import Controller.Login;
import Controller.Menu;
import Model.Appointment;
import Model.Customer;
import Model.User;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

/**
 * Class to handle the main functions of the program
 */
public class Main extends Application {

    private Stage stage;
    public static User currentUser;
    public static ObservableList<String> listCountry = FXCollections.observableArrayList();
    public static ObservableList<String> listDivision = FXCollections.observableArrayList();
    public static ObservableList<String> listContact = FXCollections.observableArrayList();
    public static ObservableList<User> listUser = FXCollections.observableArrayList();

    public static ObservableList<Appointment> listAppointment = FXCollections.observableArrayList();
    public static ObservableList<Customer> listCustomer = FXCollections.observableArrayList();
    public static Locale userLocale;
    public static ZoneId userZone;
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com/WJ08Hlf";
    private static final String jdbcURL = protocol + vendorName + ipAddress;
    private static final String MYSQLJDBCDriver = "com.mysql.cj.jdbc.Driver";
    public static Connection conn = null;
    private static final String username = "U08Hlf";

    /**
     * Method to initiate connection to database.
     */
    public static void startConnection() {
        try {
            Class.forName(MYSQLJDBCDriver);
            String password = "53689288789";
            conn = DriverManager.getConnection(jdbcURL, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method to set main user after logging and call menu screen and 15 min notification.
     *
     * @param user
     */
    public void setUser(User user) throws IOException, SQLException {
        currentUser = user;
        enableMenuScreen();

    }

    /**
     * Method to set the list of countries
     */
    private void setListCountry() {
        String getCountriesQuery = "SELECT Country from countries";
        try {
            PreparedStatement stmt = conn.prepareStatement(getCountriesQuery);

            ResultSet resultQuery = stmt.executeQuery();
            while (resultQuery.next()) {
                listCountry.add(resultQuery.getString("Country"));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to set the list of divisions
     */
    private void setListDivision() {
        String getDivisionsQuery = "SELECT Division from first_level_divisions";
        try {
            PreparedStatement stmt = conn.prepareStatement(getDivisionsQuery);

            ResultSet resultQuery = stmt.executeQuery();
            while (resultQuery.next()) {
                listDivision.add(resultQuery.getString("Division"));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to set the list of contacts
     */
    private static void setListContact() {
        String getContactsQuery = "SELECT Contact_Name from contacts";
        try {
            PreparedStatement stmt = conn.prepareStatement(getContactsQuery);

            ResultSet resultQuery = stmt.executeQuery();
            while (resultQuery.next()) {
                listContact.add(resultQuery.getString("Contact_Name"));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to set the list of users
     */
    private static void setListUser() {
        String getUserQuery = "SELECT * from users";
        try {
            PreparedStatement stmt = conn.prepareStatement(getUserQuery);

            ResultSet resultQuery = stmt.executeQuery();
            while (resultQuery.next()) {
                User foundUser = new User();
                foundUser.setUserId(resultQuery.getInt("User_ID"));
                foundUser.setUsername(resultQuery.getString("User_Name"));
                foundUser.setPassword(resultQuery.getString("Password"));
                listUser.add(foundUser);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to set the list of customers
     */
    private static void setListCustomer() {
        String getCustomersQuery = "SELECT * from customers";
        try {
            PreparedStatement stmt = conn.prepareStatement(getCustomersQuery);

            ResultSet resultQuery = stmt.executeQuery();
            while (resultQuery.next()) {
                Customer foundCustomer = new Customer(
                        resultQuery.getInt("Customer_ID"),
                        resultQuery.getString("Customer_Name"),
                        resultQuery.getString("Address"),
                        resultQuery.getString("Postal_Code"),
                        resultQuery.getString("Phone"),
                        resultQuery.getInt("Division_ID"));
                listCustomer.add(foundCustomer);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to set the list of appointments
     */
    private static void setListAppointment() {
        String getCustomersQuery = "SELECT * from appointments";
        try {
            PreparedStatement stmt = conn.prepareStatement(getCustomersQuery);

            ResultSet resultQuery = stmt.executeQuery();
            while (resultQuery.next()) {
                LocalDateTime utcStartTime = resultQuery.getTimestamp("Start").toLocalDateTime();
                LocalDateTime utcEndTime = resultQuery.getTimestamp("End").toLocalDateTime();
                ZonedDateTime startLocalTime = ZonedDateTime
                        .now(ZoneOffset.UTC)
                        .with(utcStartTime)
                        .withZoneSameInstant(userZone);
                ZonedDateTime endLocalTime = ZonedDateTime
                        .now(ZoneOffset.UTC)
                        .with(utcEndTime)
                        .withZoneSameInstant(userZone);


                Appointment foundAppointment = new Appointment(
                        resultQuery.getInt("Appointment_ID"),
                        resultQuery.getString("Title"),
                        resultQuery.getString("Description"),
                        resultQuery.getString("Location"),
                        resultQuery.getString("Type"),
                        startLocalTime.toLocalDateTime(),
                        endLocalTime.toLocalDateTime(),
                        resultQuery.getInt("Customer_ID"),
                        resultQuery.getInt("Contact_ID"),
                        resultQuery.getInt("User_ID"));
                listAppointment.add(foundAppointment);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to return the list of divisions with country as argument.
     *
     * @param countryID
     * @return list of countries
     */
    public static ObservableList<String> getListDivisionByCountry(String countryID) throws SQLException {
        String getCountriesQuery = "SELECT Division from first_level_divisions where COUNTRY_ID = ?";
        ObservableList<String> listDivisionByCountry = FXCollections.observableArrayList();

        PreparedStatement stmt = conn.prepareStatement(getCountriesQuery);
        stmt.setString(1, countryID);
        ResultSet resultQuery = stmt.executeQuery();

        while (resultQuery.next()) {
            listDivisionByCountry.add(resultQuery.getString("Division"));
        }


        return listDivisionByCountry;
    }

    /**
     * Method to return the list of countries.
     *
     * @return list of countries
     */
    public static ObservableList<String> getListCountry() {
        return listCountry;
    }

    /**
     * Method to return the list of divisions.
     *
     * @return list of divisions
     */
    public static ObservableList<String> getListDivision() {
        return listDivision;
    }

    /**
     * Method to return the list of contacts.
     *
     * @return list of contacts
     */
    public static ObservableList<String> getListContact() {
        listContact.clear();
        setListContact();
        return listContact;
    }

    /**
     * Method to return the list of users.
     *
     * @return list of users
     */
    public static ObservableList<User> getListUser() {
        listUser.clear();
        setListUser();
        return listUser;
    }

    /**
     * Method to return the list of customers.
     *
     * @return list of customers
     */
    public static ObservableList<Customer> getListCustomer() {
        listCustomer.clear();
        setListCustomer();
        return listCustomer;
    }

    /**
     * Method to return the list of appointments.
     *
     * @return list of appointments
     */
    public static ObservableList<Appointment> getListAppointment() {
        listAppointment.clear();
        setListAppointment();
        return listAppointment;
    }

    /**
     * Method to delete selected customer from database
     *
     * @param selectedCustomer
     */
    public static void deleteCustomer(Customer selectedCustomer) throws SQLException {
        String deleteCustomerQuery = "DELETE FROM customers WHERE Customer_ID = ?";

        PreparedStatement stmt = conn.prepareStatement(deleteCustomerQuery);
        stmt.setInt(1, selectedCustomer.getId());
        stmt.executeUpdate();

    }

    /**
     * Method to delete selected appointment from database
     *
     * @param selectedAppointment
     */
    public static void deleteAppointment(Appointment selectedAppointment) throws SQLException {
        String deleteAppointmentQuery = "DELETE FROM appointments WHERE Appointment_ID = ?";

        PreparedStatement stmt = conn.prepareStatement(deleteAppointmentQuery);
        stmt.setInt(1, selectedAppointment.getId());
        stmt.executeUpdate();

    }

    /**
     * Method to delete appointments from selected customer
     *
     * @param selectedCustomer
     */
    public static void deleteAppointment(Customer selectedCustomer) throws SQLException {
        String deleteAppointmentQuery = "DELETE FROM appointments WHERE Customer_ID = ?";

        PreparedStatement stmt = conn.prepareStatement(deleteAppointmentQuery);
        stmt.setInt(1, selectedCustomer.getId());
        stmt.executeUpdate();


    }

    /**
     * Method to display message of appointments within 15 minutes of user logging.
     */
    public static void notificationAppointment() {
        String messageTotalApp = Login.resourceBundle.getString("appointment") + "\t" + Login.resourceBundle.getString("start_date")
                + "\t" + Login.resourceBundle.getString("time") + "\n";

        ObservableList<Appointment> soonAppointments = FXCollections.observableArrayList();
        LocalDateTime loginLocalTime = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime loginLocalTime15 = loginLocalTime.plusMinutes(15);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Login.resourceBundle.getString("appointment"));


        for(Appointment a : listAppointment){
            if(a.getStart().isAfter(loginLocalTime) && a.getStart().isBefore(loginLocalTime15)
            ||(a.getStart().isEqual(loginLocalTime) || a.getStart().isEqual(loginLocalTime15))){
                soonAppointments.add(a);
            }
        }

        if (!soonAppointments.isEmpty()) {

            for (Appointment a : soonAppointments) {
                messageTotalApp = messageTotalApp + a.getId() + "\t\t\t" + a.getStart().toLocalDate() + "\t" + a.getStart().toLocalTime() + "\n";
            }
            alert.setHeaderText(Login.resourceBundle.getString("appointment_soon"));
            alert.setContentText(messageTotalApp);

        } else {
            alert.setHeaderText(Login.resourceBundle.getString("appointment_not_soon"));
        }
        alert.showAndWait();
    }

    /**
     * Method to call Login form
     */
    public void enableLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Login.fxml"));
        Login controller = new Login();
        controller.setMain(this);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("C195 - Scheduler");
        stage.show();
    }

    /**
     * Method to call Menu form
     */
    private void enableMenuScreen() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Menu.fxml"));
        Menu controller = new Menu();
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    /**
     * Calls startConnections method to connect to database.
     * Javadoc folder is located in "..\C195-GFP\javadoc".
     *
     * @param args Java's main method requires this string.
     */
    public static void main(String[] args) {
        startConnection();

        launch(args);
    }

    /**
     * Initiates JavaFX and loads a primary stage
     *
     * @param stage primary stage object
     * @throws Exception if method can't start throws exception.
     */
    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;
        setListCountry();
        setListDivision();
        enableLoginScreen();
        userLocale = Login.userLocale;
        userZone = Login.userZone;
    }
}
