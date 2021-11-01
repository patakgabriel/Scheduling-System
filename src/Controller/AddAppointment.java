package Controller;
/**
 * @author Gabriel Fernandez Patak
 */

import Main.Main;
import Model.Appointment;
import Model.Customer;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;

/**
 * Class to handle the Add Appointment form
 */
public class AddAppointment implements Initializable {

    private final static Connection conn = Main.conn;

    @FXML
    private TextField id;
    @FXML
    private TextField title;
    @FXML
    private TextField description;
    @FXML
    private TextField location;
    @FXML
    private TextField type;
    @FXML
    private ComboBox contact;
    @FXML
    private ComboBox customer;
    @FXML
    private ComboBox user;
    @FXML
    private ComboBox startHour;
    @FXML
    private ComboBox startMinute;
    @FXML
    private ComboBox endHour;
    @FXML
    private ComboBox endMinute;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;

    @FXML
    private Label idLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label contactLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label startLabel;
    @FXML
    private Label endLabel;
    @FXML
    private Label customerLabel;
    @FXML
    private Label userLabel;
    @FXML
    private Label addLabel;
    @FXML
    private Button saveB;
    @FXML
    private Button cancelB;

    /**
     * Method populates the customer combo box.
     */
    @FXML
    private void populateCustomer() {
        ObservableList<String> customerNameList = FXCollections.observableArrayList();
        customer.getItems().clear();
        for (Customer c : Main.getListCustomer()) {
            customerNameList.add(c.getName());
        }
        customer.setItems(customerNameList);
    }

    /**
     * Method populates the contact combo box.
     */
    @FXML
    private void populateContact() {
        contact.getItems().clear();
        contact.setItems(Main.getListContact());
    }

    /**
     * Method populates the user combo box.
     */
    @FXML
    private void populateUsers() {
        ObservableList<String> userNameList = FXCollections.observableArrayList();
        user.getItems().clear();
        for (User u : Main.getListUser()) {
            userNameList.add(u.getUsername());
        }
        user.setItems(userNameList);
    }

    /**
     * Method populates the hour and minute combo boxes.
     */
    @FXML
    private void populateTimeFields() {
        ComboBox[] hourFields = {startHour, endHour};
        for (ComboBox hourField : hourFields) {
            final ObservableList<String> options = hourField.getItems();

            for (int h = 0; h < 24; h++) {
                options.add(String.format("%02d", h));
            }
            hourField.getSelectionModel().select("12");
        }
        ComboBox[] minuteFields = {startMinute, endMinute};
        for (ComboBox minuteField : minuteFields) {
            final ObservableList<String> options = minuteField.getItems();
            for (int m = 0; m < 60; m++) {
                options.add(String.format("%02d", m));
            }
            minuteField.getSelectionModel().select("00");
        }

        DatePicker[] dateFields = {startDate, endDate};
        for (DatePicker dateField : dateFields) {
            dateField.setValue(LocalDate.now());
        }
    }

    /**
     * Method to transform input information into a structured ZonedDateTime object
     *
     * @param dateField   the DatePicker object
     * @param hourField   the hour combo box
     * @param minuteField the minute combo box
     * @return LocalDateTime object
     */
    private LocalDateTime structureDateTime(DatePicker dateField,
                                            ComboBox<String> hourField,
                                            ComboBox<String> minuteField) {
        int hour = hourField.getSelectionModel().getSelectedIndex();

        final int minute = minuteField.getSelectionModel().getSelectedIndex();


        LocalDateTime utcTime = dateField.getValue().atTime(hour, minute);


        ZonedDateTime startLocalTime = ZonedDateTime
                .now(Main.userZone)
                .with(utcTime)
                .withZoneSameInstant(ZoneOffset.UTC);

        return startLocalTime.toLocalDateTime();
    }

    /**
     * Method to gather all data input, validate it, transform it to a query and save it to the database.
     *
     * @param event base class for FX events
     */
    @FXML
    private void saveAppointment(ActionEvent event) {

        int appointmentID = Integer.parseInt(id.getText());
        String appointmentTitle = title.getText();
        String appointmentDescription = description.getText();
        String appointmentLocation = location.getText();
        String appointmentType = type.getText();
        String recordUser = Main.currentUser.getUsername();
        final LocalDateTime startTime = structureDateTime(startDate, startHour, startMinute);
        final LocalDateTime endTime = structureDateTime(endDate, endHour, endMinute);

        if (appointmentTitle.isEmpty()) {
            new Exceptions(titleLabel.getText());
            return;
        } else if (appointmentDescription.isEmpty()) {
            new Exceptions(descriptionLabel.getText());
            return;
        } else if (appointmentLocation.isEmpty()) {
            new Exceptions(locationLabel.getText());
            return;
        } else if (contact.getSelectionModel().isEmpty()) {
            new Exceptions(contactLabel.getText());
            return;
        } else if (appointmentType.isEmpty()) {
            new Exceptions(typeLabel.getText());
            return;
        } else if (customer.getSelectionModel().isEmpty()) {
            new Exceptions(customerLabel.getText());
            return;
        } else if (user.getSelectionModel().isEmpty()) {
            new Exceptions(userLabel.getText());
            return;
        }

        if (!startTime.isBefore(endTime)) {
            new Exceptions(5);
            return;
        } else if (startTime.getHour() >= 2 && startTime.getHour() < 12) { //IF START >= 22:00 OR < 8:00
            new Exceptions(6);
            return;
        } else if ((endTime.getHour() > 2 && endTime.getHour() < 12) || (endTime.getMinute() > 0) && endTime.getHour() == 2) {//If END DATE  <08:00  >22:00
            new Exceptions(7);
            return;
        } else if ((startTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !(startTime.getHour() < 2)) || startTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            new Exceptions(8);
            return;
        } else if ((endTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !(endTime.getHour() <= 2)) || endTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            new Exceptions(9);
            return;
        }

        for (Appointment a : Main.getListAppointment()) {
            ZonedDateTime appStartTime = ZonedDateTime
                    .now(Main.userZone)
                    .with(a.getStart())
                    .withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime appEndTime = ZonedDateTime
                    .now(Main.userZone)
                    .with(a.getEnd())
                    .withZoneSameInstant(ZoneOffset.UTC);

            LocalDateTime appLocalStart = appStartTime.toLocalDateTime();
            LocalDateTime appLocalEnd = appEndTime.toLocalDateTime();

            if (((startTime.isAfter(appLocalStart) || startTime.isEqual(appLocalStart)) && (startTime.isBefore(appLocalEnd) || startTime.isEqual(appLocalEnd)))
                    || ((endTime.isAfter(appLocalStart) || endTime.isEqual(appLocalStart)) && (endTime.isBefore(appLocalEnd) || endTime.isEqual(appLocalEnd)))) {
                new Exceptions(10);
                return;
            }
        }

        String getCustomerIDQuery = "SELECT Customer_ID from customers WHERE Customer_Name = ?";
        String getContactIDQuery = "SELECT Contact_ID from contacts WHERE Contact_Name = ?";
        String getUserIDQuery = "SELECT User_ID from users WHERE User_Name = ?";
        String addAppointmentQuery = "INSERT INTO appointments VALUES (?,?,?,?,?,?,?,NOW(),?,NOW(),?,?,?,?)";

        try {
            String customerName = customer.getSelectionModel().getSelectedItem().toString();
            PreparedStatement stmtCustomer = conn.prepareStatement(getCustomerIDQuery);
            stmtCustomer.setString(1, customerName);
            ResultSet resultQueryCustomer = stmtCustomer.executeQuery();
            resultQueryCustomer.next();
            int customerID = resultQueryCustomer.getInt("Customer_ID");

            String contactName = contact.getSelectionModel().getSelectedItem().toString();
            PreparedStatement stmtContact = conn.prepareStatement(getContactIDQuery);
            stmtContact.setString(1, contactName);
            ResultSet resultQueryContact = stmtContact.executeQuery();
            resultQueryContact.next();
            int contactID = resultQueryContact.getInt("Contact_ID");

            String appointmentUser = user.getSelectionModel().getSelectedItem().toString();
            PreparedStatement stmtUser = conn.prepareStatement(getUserIDQuery);
            stmtUser.setString(1, appointmentUser);
            ResultSet resultQueryUser = stmtUser.executeQuery();
            resultQueryUser.next();
            int appointmentUserID = resultQueryUser.getInt("User_ID");


            PreparedStatement stmt = conn.prepareStatement(addAppointmentQuery);
            stmt.setInt(1, appointmentID);
            stmt.setString(2, appointmentTitle);
            stmt.setString(3, appointmentDescription);
            stmt.setString(4, appointmentLocation);
            stmt.setString(5, appointmentType);
            stmt.setString(6, startTime.toString());
            stmt.setString(7, endTime.toString());
            stmt.setString(8, recordUser);
            stmt.setString(9, recordUser);
            stmt.setInt(10, customerID);
            stmt.setInt(11, appointmentUserID);
            stmt.setInt(12, contactID);
            stmt.executeUpdate();
            mainMenu(event);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to generate an appointment ID based on the highest number available.
     */
    private void generateID() {
        String getMaxIDQuery = "SELECT MAX(Appointment_ID)+1 as Appointment_ID FROM appointments";
        int maxID;
        try {
            PreparedStatement stmt = conn.prepareStatement(getMaxIDQuery);
            ResultSet resultQuery = stmt.executeQuery();
            if (resultQuery.next()) {
                maxID = resultQuery.getInt("Appointment_ID");
                if (maxID == 0) {
                    maxID = 1;
                }
                id.setText(Integer.toString(maxID));
            }
        } catch (SQLException ex) {
            new Exceptions(0);
        }

    }

    /**
     * Method to call after the mainMenu method after user confirms cancel action.
     * LAMBDA EXPRESSION 1: This lambda expression simplified handling the response of the user to the cancel action.
     * It would have taken a few extra lines of code to handle the cancel action without lambda.
     *
     * @param event the base class for FX events
     */
    @FXML
    private void cancelAddAppointment(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(Login.resourceBundle.getString("confirm_title"));
        alert.setHeaderText(Login.resourceBundle.getString("confirm_title"));
        alert.setContentText(Login.resourceBundle.getString("confirm_text"));
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> mainMenu(event));
    }

    /**
     * Method to go back to the main menu
     *
     * @param event the base class for FX events
     */
    @FXML
    private void mainMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Menu.fxml"));
            Menu controller = new Menu();
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            new Exceptions(0);
        }
    }

    /**
     * Sets proper language and calls methods that populate fields
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idLabel.setText(Login.resourceBundle.getString("id"));
        titleLabel.setText(Login.resourceBundle.getString("title"));
        descriptionLabel.setText(Login.resourceBundle.getString("description"));
        locationLabel.setText(Login.resourceBundle.getString("location"));
        contactLabel.setText(Login.resourceBundle.getString("contact"));
        typeLabel.setText(Login.resourceBundle.getString("type"));
        startLabel.setText(Login.resourceBundle.getString("start_date"));
        endLabel.setText(Login.resourceBundle.getString("end_date"));
        customerLabel.setText(Login.resourceBundle.getString("customer"));
        userLabel.setText(Login.resourceBundle.getString("user"));
        addLabel.setText(Login.resourceBundle.getString("add_appointment"));
        cancelB.setText(Login.resourceBundle.getString("cancel"));
        saveB.setText(Login.resourceBundle.getString("save"));

        populateTimeFields();
        populateUsers();
        populateCustomer();
        populateContact();
        generateID();
    }
}
