package Model;

/**
 * @author Gabriel Fernandez Patak
 */
public class Customer {


    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionID;

    /**
     * Constructor to create an Customer object
     *
     * @param id         the Customer id
     * @param name       the Customer name
     * @param address    the Customer address
     * @param postalCode the Customer postal code
     * @param phone      the Customer phone
     * @param divisionID the Customer division id
     */
    public Customer(int id, String name, String address, String postalCode, String phone, int divisionID) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionID = divisionID;
    }

    /**
     * @return customer ID
     */
    public int getId() {
        return id;
    }

    /**
     * @param id set customer ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return customer name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name set customer name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return customer address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address set customer address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return customer postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode set customer postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return customer phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone set customer phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return customer division
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * @param divisionID set customer division
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

}
