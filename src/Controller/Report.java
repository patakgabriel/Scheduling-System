package Controller;
/**
 * @author Gabriel Fernandez Patak
 */

import Main.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

/**
 * Class to handle the Reports form
 */
public class Report implements Initializable {
    private final static Connection conn = Main.conn;
    public static ObservableList<ObservableList> data = FXCollections.observableArrayList();

    private int chosenReport = 0;

    @FXML
    private TableView reportTable;
    @FXML
    public Label reportLabel;
    @FXML
    public Button menuB;
    @FXML
    public Button reportOneB;
    @FXML
    public Button reportTwoB;
    @FXML
    public Button reportThreeB;

    /**
     * Method to query total appointments by month and type and call the setReport method.
     */
    @FXML
    private void buttonReportOne() {
        String getReportQuery = "SELECT COUNT(Appointment_ID) as 'Total Appointments', monthname(start) as Month, Type\n" +
                "from appointments\n" +
                "GROUP BY monthname(start), Type";
        chosenReport = 1;
        setReport(getReportQuery);
    }

    /**
     * Method to query total appointments by contact and call the setReport method.
     */
    @FXML
    private void buttonReportTwo() {
        String getReportQuery = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, " +
                "Customer_ID, User_ID, c.Contact_ID, c.Contact_Name, c.Email " +
                "FROM appointments a " +
                "JOIN contacts c ON c.Contact_ID = a.Contact_ID " +
                "ORDER BY Contact_ID, Start";
        chosenReport = 2;
        setReport(getReportQuery);
    }

    /**
     * Method to query appointments by country and call the setReport method.
     */
    @FXML
    private void buttonReportThree() {
        String getReportQuery = "SELECT a.Title, a.Appointment_ID, Customer_Name, Address, Postal_Code, Phone,  d.Division, s.Country\n" +
                "                FROM customers c \n" +
                "                JOIN first_level_divisions d ON d.Division_ID = c.Division_ID JOIN countries s ON d.COUNTRY_ID = s.Country_ID " +
                "                JOIN appointments a ON a.Customer_ID = c.Customer_ID\n" +
                "                ORDER BY  s.Country, Customer_Name";
        chosenReport = 3;
        setReport(getReportQuery);
    }

    /**
     * Method that receives a string to query and updates the TableView object with the result.
     * LAMBDA EXPRESSION 2: This lambda expression sets the header of the table columns.
     * It would have taken extra lines of code to handle the cancel action without lambda.
     * @param query
     */
    @FXML
    private void setReport(String query) {

        reportTable.getItems().clear();
        reportTable.getColumns().clear();
        data.clear();

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultQuery = stmt.executeQuery();


            for (int i = 0; i < resultQuery.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(resultQuery.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                        param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                reportTable.getColumns().addAll(col);
            }

            while (resultQuery.next()) {

                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= resultQuery.getMetaData().getColumnCount(); i++) {

                    if(chosenReport == 2 && i == 6){
                        LocalDateTime utcStartTime = resultQuery.getTimestamp("Start").toLocalDateTime();

                        ZonedDateTime startLocalTime = ZonedDateTime
                                .now(ZoneOffset.UTC)
                                .with(utcStartTime)
                                .withZoneSameInstant(Main.userZone);
                        row.add(startLocalTime.toLocalDate().toString() + " " + startLocalTime.toLocalTime().toString());
                    }else if(chosenReport == 2 && i == 7){
                        LocalDateTime utcEndTime = resultQuery.getTimestamp("End").toLocalDateTime();
                        ZonedDateTime endLocalTime = ZonedDateTime
                                .now(ZoneOffset.UTC)
                                .with(utcEndTime)
                                .withZoneSameInstant(Main.userZone);
                        row.add(endLocalTime.toLocalDate().toString() + " " + endLocalTime.toLocalTime().toString());
                    }else {
                        row.add(resultQuery.getString(i));
                    }
                }
                data.add(row);
            }

            reportTable.setItems(data);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
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
     * Sets proper language
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        reportOneB.setText(Login.resourceBundle.getString("report_one"));
        reportTwoB.setText(Login.resourceBundle.getString("report_two"));
        reportThreeB.setText(Login.resourceBundle.getString("report_three"));
        menuB.setText(Login.resourceBundle.getString("menu"));
        reportLabel.setText(Login.resourceBundle.getString("reports"));


    }
}
