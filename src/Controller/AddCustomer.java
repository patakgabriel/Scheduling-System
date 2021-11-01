package Controller;
/**
 * @author Gabriel Fernandez Patak
 */

import Main.Main;
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
import java.util.ResourceBundle;

/**
 * Class to handle the Add Customer form
 */
public class AddCustomer implements Initializable {

    private final static Connection conn = Main.conn;

    @FXML
    private TextField id;
    @FXML
    private TextField name;
    @FXML
    private TextField address;
    @FXML
    private TextField postalCode;
    @FXML
    private TextField phone;
    @FXML
    private ComboBox country;
    @FXML
    private ComboBox division;

    @FXML
    private Label addLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label postalLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Label divisionLabel;
    @FXML
    private Button saveB;
    @FXML
    private Button cancelB;

    /**
     * Method populates the country combo box.
     */
    @FXML
    private void populateCountries() {
        country.getItems().clear();
        country.setItems(Main.getListCountry());
    }

    /**
     * Method populates the division combo box.
     */
    @FXML
    private void populateDivision() {
        division.getItems().clear();
        division.setItems(Main.getListDivision());
    }

    /**
     * Method to update division combo box based on selected country
     */
    @FXML
    private void filterDivision() {
        String countrySelected = country.getSelectionModel().getSelectedItem().toString();
        String getCountryCodeQuery = "select Country_ID from countries WHERE Country = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(getCountryCodeQuery);
            stmt.setString(1, countrySelected);

            ResultSet resultQuery = stmt.executeQuery();

            if (resultQuery.next()) {
                division.getItems().clear();
                division.setItems(Main.getListDivisionByCountry(resultQuery.getString("Country_ID")));

            } else {
                System.out.println("Error");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    /**
     * Method to gather all data input, validate it, transform it to a query and save it to the database.
     *
     * @param event base class for FX events
     */
    @FXML
    private void saveCustomer(ActionEvent event) {

        int customerID = Integer.parseInt(id.getText());
        String customerName = name.getText();
        String customerAddress = address.getText();
        String customerPostalCode = postalCode.getText();
        String customerPhone = phone.getText();
        String customerUser = Main.currentUser.getUsername();

        if (customerName.isEmpty()) {
            new Exceptions(nameLabel.getText());
            return;
        } else if (customerAddress.isEmpty()) {
            new Exceptions(addressLabel.getText());
            return;
        } else if (customerPostalCode.isEmpty()) {
            new Exceptions(postalLabel.getText());
            return;
        } else if (customerPhone.isEmpty()) {
            new Exceptions(phoneLabel.getText());
            return;
        } else if (country.getSelectionModel().isEmpty()) {
            new Exceptions(countryLabel.getText());
            return;
        } else if (division.getSelectionModel().isEmpty()) {
            new Exceptions(divisionLabel.getText());
            return;
        }

        int divisionID = 1;

        String getDivisionIDQuery = "SELECT Division_ID from first_level_divisions WHERE Division = ?";
        String addCustomerQuery = "INSERT INTO customers VALUES (?,?,?,?,?,NOW(),?,NOW(),?,?)";

        try {
            if (!division.getSelectionModel().isEmpty()) {
                String customerDivision = division.getSelectionModel().getSelectedItem().toString();
                PreparedStatement stmtDivision = conn.prepareStatement(getDivisionIDQuery);
                stmtDivision.setString(1, customerDivision);
                ResultSet resultQueryDivision = stmtDivision.executeQuery();
                if (resultQueryDivision.next()) {
                    divisionID = resultQueryDivision.getInt("Division_ID");
                }
            }


            PreparedStatement stmt = conn.prepareStatement(addCustomerQuery);
            stmt.setInt(1, customerID);
            stmt.setString(2, customerName);
            stmt.setString(3, customerAddress);
            stmt.setString(4, customerPostalCode);
            stmt.setString(5, customerPhone);
            stmt.setString(6, customerUser);
            stmt.setString(7, customerUser);
            stmt.setInt(8, divisionID);
            stmt.executeUpdate();
            mainMenu(event);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to generate a customer ID based on the highest number available.
     */
    private void generateID() {
        String getMaxIDQuery = "SELECT MAX(Customer_ID)+1 as Customer_ID FROM customers";
        String maxID;
        try {
            PreparedStatement stmt = conn.prepareStatement(getMaxIDQuery);
            ResultSet resultQuery = stmt.executeQuery();
            if (resultQuery.next()) {
                maxID = resultQuery.getString("Customer_ID");
                id.setText(maxID);
            } else {
                System.out.println("Error running MAX(Customer_ID) query");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error happened");
                alert.showAndWait();
                return;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error happened");
            alert.showAndWait();
            return;
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
    private void cancelAddCustomer(ActionEvent event) {
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

        idLabel.setText(Login.resourceBundle.getString("id"));
        nameLabel.setText(Login.resourceBundle.getString("name"));
        addressLabel.setText(Login.resourceBundle.getString("address"));
        postalLabel.setText(Login.resourceBundle.getString("postal_code"));
        phoneLabel.setText(Login.resourceBundle.getString("phone"));
        countryLabel.setText(Login.resourceBundle.getString("country"));
        divisionLabel.setText(Login.resourceBundle.getString("division"));
        addLabel.setText(Login.resourceBundle.getString("add_customer"));
        cancelB.setText(Login.resourceBundle.getString("cancel"));
        saveB.setText(Login.resourceBundle.getString("save"));

        populateCountries();
        populateDivision();
        generateID();
    }
}
