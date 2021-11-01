package Controller;
/**
 * @author Gabriel Fernandez Patak
 */

import Main.Main;
import Model.Appointment;
import Model.Customer;
import javafx.application.Platform;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class to handle menu form
 */
public class Menu implements Initializable {


    public TableView<Customer> customerTable;
    public TableColumn customerID;
    public TableColumn customerName;
    public TableColumn customerAddress;
    public TableColumn customerPostal;
    public TableColumn customerPhone;
    public TableColumn customerDivision;

    public TableView<Appointment> appointmentTable;
    public TableColumn appointmentID;
    public TableColumn appointmentTitle;
    public TableColumn appointmentDescription;
    public TableColumn appointmentLocation;
    public TableColumn appointmentContact;
    public TableColumn appointmentType;
    public TableColumn appointmentStart;
    public TableColumn appointmentEnd;
    public TableColumn appointmentCustomer;

    @FXML
    ToggleGroup DateFilter;

    @FXML
    public Button reportB;
    @FXML
    public Button exitB;
    @FXML
    public Button addAppB;
    @FXML
    public Button modifyAppB;
    @FXML
    public Button deleteAppB;
    @FXML
    public Button addCustomerB;
    @FXML
    public Button modifyCustomerB;
    @FXML
    public Button deleteCustomerB;
    @FXML
    public RadioButton allRadio;
    @FXML
    public RadioButton monthRadio;
    @FXML
    public RadioButton weekRadio;


    private static int activateNotice = 0;
    //private final static Connection conn = Main.conn;
    
    /**
     * Method to handle exit button
     *
     * @param event
     */
    @FXML
    private void exitButton(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Method to handle report button
     *
     * @param event
     */
    @FXML
    private void viewReportButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Report.fxml"));
            Report controller = new Report();
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Method to handle add customer button
     *
     * @param event
     */
    @FXML
    private void addCustomerButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Add Customer.fxml"));
            AddCustomer controller = new AddCustomer();
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Method to handle add appointment button
     *
     * @param event
     */
    @FXML
    private void addAppointmentButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Add Appointment.fxml"));
            AddAppointment controller = new AddAppointment();
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Method to handle modify appointment button
     *
     * @param event
     */
    @FXML
    private void modifyAppointmentButton(ActionEvent event) throws IOException {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null || Main.listAppointment.isEmpty()) {
            new Exceptions(4);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Modify Appointment.fxml"));
            ModifyAppointment controller = new ModifyAppointment(selectedAppointment);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }

    }

    /**
     * Method to handle modify customer button
     *
     * @param event
     */
    @FXML
    private void modifyCustomerButton(ActionEvent event) throws IOException {

        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null || Main.listCustomer.isEmpty()) {
            new Exceptions(3);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Modify Customer.fxml"));
            ModifyCustomer controller = new ModifyCustomer(selectedCustomer);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }

    }

    /**
     * Method to handle delete customer button
     *
     * @param event
     */
    @FXML
    private void deleteCustomerButton(ActionEvent event) throws SQLException {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null || Main.listCustomer.isEmpty()) {
            new Exceptions(3);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(Login.resourceBundle.getString("delete"));
            alert.setHeaderText(Login.resourceBundle.getString("delete_customer"));
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                try {
                    Main.deleteCustomer(selectedCustomer);

                } catch (SQLException e) {
                    Alert alertCustomer = new Alert(Alert.AlertType.CONFIRMATION);
                    alertCustomer.setTitle(Login.resourceBundle.getString("delete"));
                    alertCustomer.setHeaderText(Login.resourceBundle.getString("customer_has_appointment"));
                    Optional<ButtonType> resultCustomer = alertCustomer.showAndWait();
                    if (resultCustomer.get() == ButtonType.OK) {
                        Main.deleteAppointment(selectedCustomer);
                        Main.deleteCustomer(selectedCustomer);
                    }
                }
                customerTable.setItems(Main.getListCustomer());
                appointmentTable.setItems(Main.getListAppointment());
                customerTable.refresh();
                appointmentTable.refresh();

            }
        }
    }

    /**
     * Method to handle delete customer button
     *
     * @param event
     */
    @FXML
    private void deleteAppointmentButton(ActionEvent event) throws SQLException {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null || Main.listAppointment.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No appointment selected");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(Login.resourceBundle.getString("delete"));
            alert.setHeaderText(Login.resourceBundle.getString("delete_appointment"));
            alert.setContentText(Login.resourceBundle.getString("appointment") + ": " + Integer.toString(selectedAppointment.getId()) + "\n"
                    + Login.resourceBundle.getString("type") + ": " + selectedAppointment.getType());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Main.deleteAppointment(selectedAppointment);
                Alert postAlert = new Alert(Alert.AlertType.INFORMATION);
                postAlert.setTitle(Login.resourceBundle.getString("appointment_deleted"));
                postAlert.setHeaderText(Login.resourceBundle.getString("appointment_deleted"));
                postAlert.setContentText(Login.resourceBundle.getString("appointment") + ": " + Integer.toString(selectedAppointment.getId()) + " "
                        + Login.resourceBundle.getString("type") + ": " + selectedAppointment.getType() + " " + Login.resourceBundle.getString("has_been_deleted"));
                postAlert.showAndWait();
                appointmentTable.setItems(Main.getListAppointment());
                appointmentTable.refresh();
            }
        }
    }

    /**
     * Method to filter appointments based on radio button by all, month, or week
     */
    public void filterAppointmentTable() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        ObservableList<Appointment> filteredAppointmentList = FXCollections.observableArrayList();
        RadioButton selectedRadioButton = (RadioButton) DateFilter.getSelectedToggle();
        String toggleGroupValue = selectedRadioButton.getId();


        if (toggleGroupValue.equals("allRadio")) {
            filteredAppointmentList = Main.getListAppointment();

        } else if (toggleGroupValue.equals("monthRadio")) {
            int currentMonth = currentDateTime.getMonthValue();
            for (Appointment a : Main.getListAppointment()) {
                if (a.getStart().getMonthValue() == currentMonth) {
                    filteredAppointmentList.add(a);
                }
            }
        } else if (toggleGroupValue.equals("weekRadio")) {
            Locale userLocale = Locale.getDefault();
            WeekFields weekNumbering = WeekFields.of(userLocale);
            LocalDate date = LocalDate.now();
            int currentWeek = date.get(weekNumbering.weekOfWeekBasedYear());
            int year = date.get(weekNumbering.weekBasedYear());
            for (Appointment a : Main.getListAppointment()) {
                if (a.getStart().get(weekNumbering.weekOfWeekBasedYear()) == currentWeek && a.getStart().get(weekNumbering.weekBasedYear()) == year) {
                    filteredAppointmentList.add(a);
                }
            }

        }


        appointmentTable.setItems(filteredAppointmentList);
        appointmentTable.refresh();
    }

    /**
     * Sets proper language and tables
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerTable.setItems(Main.getListCustomer());
        customerTable.refresh();
        appointmentTable.setItems(Main.getListAppointment());
        appointmentTable.refresh();
        if(activateNotice == 0) {
            Main.notificationAppointment();
            activateNotice = 1;
        }
        reportB.setText(Login.resourceBundle.getString("view_report"));
        exitB.setText(Login.resourceBundle.getString("exit"));
        addAppB.setText(Login.resourceBundle.getString("add"));
        modifyAppB.setText(Login.resourceBundle.getString("modify"));
        deleteAppB.setText(Login.resourceBundle.getString("delete"));
        addCustomerB.setText(Login.resourceBundle.getString("add"));
        modifyCustomerB.setText(Login.resourceBundle.getString("modify"));
        deleteCustomerB.setText(Login.resourceBundle.getString("delete"));
        allRadio.setText(Login.resourceBundle.getString("view_all"));
        monthRadio.setText(Login.resourceBundle.getString("view_month"));
        weekRadio.setText(Login.resourceBundle.getString("view_week"));

        customerID.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostal.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerDivision.setCellValueFactory(new PropertyValueFactory<>("divisionID"));

        appointmentID.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentContact.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStart.setCellValueFactory(new PropertyValueFactory<>("startString"));
        appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endString"));
        appointmentCustomer.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }
}
