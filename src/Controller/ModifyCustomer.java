package Controller;
/**
 * @author Gabriel Fernandez Patak
 */

import Main.Main;
import Model.Customer;
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
 * Class to Modify Customer form
 */
public class ModifyCustomer implements Initializable {

    private final static Connection conn = Main.conn;
    Customer modCustomer;

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
    private Label modifyLabel;
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
     * Constructor to set customer to be modified
     *
     * @param modCustomer
     */
    public ModifyCustomer(Customer modCustomer) {
        this.modCustomer = modCustomer;
    }

    /**
     * Method to get the country of a division
     *
     * @param divisionID ID to query division table
     * @return country
     */
    private String getCountry(int divisionID) throws SQLException {
        String getCountryQuery = "select c.Country \n" +
                "from first_level_divisions d join countries c on d.COUNTRY_ID = c.Country_ID\n" +
                "WHERE Division_ID = ?";
        PreparedStatement stmt = conn.prepareStatement(getCountryQuery);
        stmt.setInt(1, divisionID);
        ResultSet resultQuery = stmt.executeQuery();
        resultQuery.next();
        return resultQuery.getString("Country");
    }

    /**
     * Method to get the division nam from a division id
     *
     * @param divisionID ID to query division table
     * @return division name
     */
    private String getDivision(int divisionID) throws SQLException {
        String getDivisionQuery = "SELECT Division from first_level_divisions WHERE Division_ID =?";
        PreparedStatement stmt = conn.prepareStatement(getDivisionQuery);
        stmt.setInt(1, divisionID);
        ResultSet resultQuery = stmt.executeQuery();
        resultQuery.next();
        return resultQuery.getString("Division");
    }

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
    private void saveCustomer(ActionEvent event) throws IOException {

        int customerID = modCustomer.getId();
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
        String addCustomerQuery = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?,Last_Update = NOW(), Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";

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

            stmt.setString(1, customerName);
            stmt.setString(2, customerAddress);
            stmt.setString(3, customerPostalCode);
            stmt.setString(4, customerPhone);
            stmt.setString(5, customerUser);
            stmt.setInt(6, divisionID);
            stmt.setInt(7, customerID);
            stmt.executeUpdate();
            mainMenu(event);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
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
    private void cancelModifyCustomer(ActionEvent event) {
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
        this.id.setText(Integer.toString(modCustomer.getId()));
        this.name.setText(modCustomer.getName());
        this.address.setText(modCustomer.getAddress());
        this.postalCode.setText(modCustomer.getPostalCode());
        this.phone.setText(modCustomer.getPhone());
        try {
            this.country.getSelectionModel().select(getCountry(modCustomer.getDivisionID()));
            this.division.getSelectionModel().select(getDivision(modCustomer.getDivisionID()));
        } catch (SQLException e) {
            e.printStackTrace();
        }


        idLabel.setText(Login.resourceBundle.getString("id"));
        nameLabel.setText(Login.resourceBundle.getString("name"));
        addressLabel.setText(Login.resourceBundle.getString("address"));
        postalLabel.setText(Login.resourceBundle.getString("postal_code"));
        phoneLabel.setText(Login.resourceBundle.getString("phone"));
        countryLabel.setText(Login.resourceBundle.getString("country"));
        divisionLabel.setText(Login.resourceBundle.getString("division"));
        modifyLabel.setText(Login.resourceBundle.getString("modify_customer"));
        cancelB.setText(Login.resourceBundle.getString("cancel"));
        saveB.setText(Login.resourceBundle.getString("save"));

        populateCountries();
        populateDivision();
    }
}
