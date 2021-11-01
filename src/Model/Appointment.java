package Model;
/**
 * @author Gabriel Fernandez Patak
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Appointment {

    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private int customerID;
    private int contactID;
    private int userID;

    /**
     * Constructor to create an Appointment object
     * @param id the Appointment id
     * @param title the Appointment title
     * @param description the Appointment description
     * @param location the Appointment location
     * @param type the Appointment type
     * @param start the Appointment start datetime
     * @param end the Appointment end datetime
     * @param customerID the Appointment customer ID
     * @param contactID the Appointment contact ID
     * @param userID the Appointment user ID
     */
    public Appointment(int id, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, int customerID, int contactID, int userID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.contactID = contactID;
        this.userID = userID;
    }
    /**
     * @return appointment userID
     */
    public int getUserID() {
        return userID;
    }
    /**
     * @param userID the id to set appointment user
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return appointment id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set appointment
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return appointment title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title the appointment's title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return appointment description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the appointment's description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return appointment location
     */
    public String getLocation() {
        return location;
    }
    /**
     * @param location the appointment's location
     */
    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * @return appointment type
     */
    public String getType() {
        return type;
    }
    /**
     * @param type the appointment's location
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @return appointment start datetime
     */
    public LocalDateTime getStart() {
        return start;
    }
    /**
     * @param start the appointment's start datetime
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }
    /**
     * @return appointment end datetime
     */
    public LocalDateTime getEnd() {
        return end;
    }
    /**
     * @param end the appointment's end datetime
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
    /**
     * @return appointment customer ID
     */
    public int getCustomerID() {
        return customerID;
    }
    /**
     * @param customerID the appointment's customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    /**
     * @return appointment contact ID
     */
    public int getContactID() {
        return contactID;
    }
    /**
     * @param contactID the appointment's contact ID
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }
    /**
     * @return appointment start time as String
     */
    public String getStartString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.getDefault());
        return start.format(formatter);
    }
    /**
     * @return appointment end time as String
     */
    public String getEndString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.getDefault());
        return end.format(formatter);
    }



}
