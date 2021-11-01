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
 * Class to handle the Modify Appointment form
 */
public class ModifyAppointment implements Initializable {

    private final static Connection conn = Main.conn;
    Appointment modAppointment;

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
    private Label modifyLabel;
    @FXML
    private Button saveB;
    @FXML
    private Button cancelB;


    /**
     * Constructor to set appointment to be modified
     *
     * @param modAppointment
     */
    public ModifyAppointment(Appointment modAppointment) {
        this.modAppointment = modAppointment;
    }

    /**
     * Method populates the customer combo box.
     */
    @FXML
    private void populateCustomer(int selectedCustomer) {
        ObservableList<String> customerNameList = FXCollections.observableArrayList();
        customer.getItems().clear();
        for (Customer c : Main.getListCustomer()) {
            customerNameList.add(c.getName());
            if (c.getId() == selectedCustomer) {
                customer.getSelectionModel().select(c.getName());
            }
        }
        customer.setItems(customerNameList);
    }

    /**
     * Method populates the contact combo box.
     */
    @FXML
    private void populateContact(int contactID) {
        contact.getItems().clear();
        contact.setItems(Main.getListContact());
        try {
            contact.getSelectionModel().select(getContactName(contactID));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Method populates the user combo box.
     */
    @FXML
    private void populateUsers(int selectedUser) {
        ObservableList<String> userNameList = FXCollections.observableArrayList();
        user.getItems().clear();
        for (User u : Main.getListUser()) {
            userNameList.add(u.getUsername());
            if (u.getUserId() == selectedUser) {
                user.getSelectionModel().select(u.getUsername());
            }
        }
        user.setItems(userNameList);
    }

    /**
     * Method populates the hour and minute combo boxes.
     */
    @FXML
    private void populateTimeFields(LocalDateTime modStartLocalDate, LocalDateTime modEndLocalDate) {

        ZonedDateTime modStartDate = modStartLocalDate.atZone(Main.userZone);
        ZonedDateTime modEndDate = modEndLocalDate.atZone(Main.userZone);


        ComboBox[] hourFields = {startHour, endHour};
        for (ComboBox hourField : hourFields) {
            final ObservableList<String> options = hourField.getItems();

            for (int h = 0; h < 24; h++) {
                options.add(String.format("%02d", h));
            }
        }
        ComboBox[] minuteFields = {startMinute, endMinute};
        for (ComboBox minuteField : minuteFields) {
            final ObservableList<String> options = minuteField.getItems();
            for (int m = 0; m < 60; m++) {
                options.add(String.format("%02d", m));
            }

        }

        DatePicker[] dateFields = {startDate, endDate};
        for (DatePicker dateField : dateFields) {
            dateField.setValue(LocalDate.now());
        }

        startHour.setValue(String.format("%02d", modStartDate.getHour()));
        endHour.setValue(String.format("%02d", modEndDate.getHour()));
        startMinute.setValue(String.format("%02d", modStartDate.getMinute()));
        endMinute.setValue(String.format("%02d", modEndDate.getMinute()));
        startDate.setValue(modStartDate.toLocalDate());
        endDate.setValue(modEndDate.toLocalDate());
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
        } else if ((endTime.getHour() > 2 && endTime.getHour() < 12) || (endTime.getMinute() > 0) && endTime.getHour() == 2) {//If ENDDATE  <08:00  >22:00
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
            if(a.getId() != appointmentID){
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

        }

        String getCustomerIDQuery = "SELECT Customer_ID from customers WHERE Customer_Name = ?";
        String getContactIDQuery = "SELECT Contact_ID from contacts WHERE Contact_Name = ?";
        String getUserIDQuery = "SELECT User_ID from users WHERE User_Name = ?";
        String modAppointmentQuery = "UPDATE appointments " +
                "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = NOW(), Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";

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


            PreparedStatement stmt = conn.prepareStatement(modAppointmentQuery);

            stmt.setString(1, appointmentTitle);
            stmt.setString(2, appointmentDescription);
            stmt.setString(3, appointmentLocation);
            stmt.setString(4, appointmentType);
            stmt.setString(5, startTime.toString());
            stmt.setString(6, endTime.toString());
            stmt.setString(7, recordUser);
            stmt.setInt(8, customerID);
            stmt.setInt(9, appointmentUserID);
            stmt.setInt(10, contactID);
            stmt.setInt(11, appointmentID);

            stmt.executeUpdate();
            mainMenu(event);


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to get the name of contact from their ID.
     *
     * @param contactID ID to query contact table
     * @return contactName
     */
    private String getContactName(int contactID) throws SQLException {
        String getContactIDQuery = "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?";
        String contactName;

        PreparedStatement stmt = conn.prepareStatement(getContactIDQuery);
        stmt.setInt(1, contactID);
        ResultSet resultQuery = stmt.executeQuery();
        resultQuery.next();
        return contactName = resultQuery.getString("Contact_Name");
    }

    /**
     * Method to call after the mainMenu method after user confirms cancel action.
     * LAMBDA EXPRESSION 1: This lambda expression simplified handling the response of the user to the cancel action.
     * It would have taken a few extra lines of code to handle the cancel action without lambda.
     *
     * @param event the base class for FX events
     */
    @FXML
    private void cancelModifyAppointment(ActionEvent event) {
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
            System.out.println(e);
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
        this.id.setText(Integer.toString(modAppointment.getId()));
        this.title.setText(modAppointment.getTitle());
        this.description.setText(modAppointment.getDescription());
        this.location.setText(modAppointment.getLocation());
        this.type.setText(modAppointment.getType());

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
        modifyLabel.setText(Login.resourceBundle.getString("modify"));
        cancelB.setText(Login.resourceBundle.getString("cancel"));
        saveB.setText(Login.resourceBundle.getString("save"));

        populateTimeFields(modAppointment.getStart(), modAppointment.getEnd());
        populateUsers(modAppointment.getUserID());
        populateCustomer(modAppointment.getCustomerID());
        populateContact(modAppointment.getContactID());

    }
}
