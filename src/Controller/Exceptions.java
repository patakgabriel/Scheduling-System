package Controller;
/**
 * @author Gabriel Fernandez Patak
 */

import javafx.scene.control.Alert;

/**
 * Class to handle the exceptions
 */
public class Exceptions {
    /**
     * Constructor for Exceptions with integer as input.
     *
     * @param exceptionCode
     */
    public Exceptions(int exceptionCode) {
        generateMessage(exceptionCode);
    }

    /**
     * Constructor for Exceptions with String as input.
     *
     * @param emptyField
     */
    public Exceptions(String emptyField) {
        generateTextMessage(emptyField);
    }

    /**
     * Method to handle exceptions and generate and error message.
     *
     * @param exceptionCode
     */
    private void generateMessage(int exceptionCode) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Login.resourceBundle.getString("error"));

        switch (exceptionCode) {
            case 1:
                alert.setHeaderText(Login.resourceBundle.getString("incorrect_login"));
                break;
            case 2:
                alert.setHeaderText(Login.resourceBundle.getString("blank_login"));
                break;
            case 3:
                alert.setHeaderText(Login.resourceBundle.getString("customer_error"));
                break;
            case 4:
                alert.setHeaderText(Login.resourceBundle.getString("appointment_error"));
                break;
            case 5:
                alert.setHeaderText(Login.resourceBundle.getString("before_larger_after"));
                break;
            case 6:
                alert.setHeaderText(Login.resourceBundle.getString("start_not_range"));
                break;
            case 7:
                alert.setHeaderText(Login.resourceBundle.getString("end_not_range"));
                break;
            case 8:
                alert.setHeaderText(Login.resourceBundle.getString("start_weekend"));
                break;
            case 9:
                alert.setHeaderText(Login.resourceBundle.getString("end_weekend"));
                break;
            case 10:
                alert.setHeaderText(Login.resourceBundle.getString("busy_schedule"));
                break;

            case 0:
                alert.setHeaderText(Login.resourceBundle.getString("unknown_error"));

        }
        alert.showAndWait();
    }

    /**
     * Method to handle exceptions when empty field is submitted
     *
     * @param emptyField
     */
    private void generateTextMessage(String emptyField) {
        Alert alertEmpty = new Alert(Alert.AlertType.ERROR);
        alertEmpty.setTitle(Login.resourceBundle.getString("error"));

        alertEmpty.setHeaderText(emptyField + " " + Login.resourceBundle.getString("is_empty"));

        alertEmpty.showAndWait();
    }

}
