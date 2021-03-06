package query;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import java.sql.PreparedStatement;
import java.sql.*;

/**
 * Class that queries the customer table.
 */
public class CustomerQuery {

    private static Connection conn = JDBC.getConnection();;
    private static Statement mystmt = null;
    private static PreparedStatement ps = null;
    private static ResultSet result = null;

    /**
     *
     * @return List of all customers in the database.
     */
    public static ObservableList<Customer> getAllCustomers(){
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        try {
            mystmt = conn.createStatement();
            result = mystmt.executeQuery("select * from customers");
            while(result.next()){
                    Customer c = new Customer(result.getInt("Customer_ID"), result.getString("Customer_Name"), result.getString("Address"), result.getString("Postal_Code"), result.getString("Phone"), result.getInt("Division_ID"));
                    allCustomers.add(c);
                }
            return allCustomers;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param c customer desired to be deleted
     * @return true if the customer was deleted successfully.
     */
    public static boolean deleteCustomer(Customer c){
        int id = c.getId();
        String sqlDelete = "DELETE FROM customers WHERE Customer_ID = ?";
        try{
            ps = conn.prepareStatement(sqlDelete);
            ps.setInt(1, id);
            int confirmDeleted = ps.executeUpdate();
            if(confirmDeleted < 1){
                return false;
            }else{
                return true;
            }
        }catch (SQLException e){
            //
        }
        return false;
}

    /**
     * Inserts a new customer into the database.
     * @param name new customer name.
     * @param address new customer address.
     * @param postal new customer postal code.
     * @param phone new customer phone number.
     * @param divID new customer Division_ID.
     */
    public static void insertCustomer(String name, String address, String postal, String phone, int divID){

        String sqlInsert = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID)" +
                            "VALUES(?,?,?,?,?)         ";
        try{
            ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, postal);
            ps.setString(4, phone);
            ps.setInt(5,divID);
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     *
     *Updates the desired fields for a customer that is already in the database, does not allow the user to modify the customer's unique ID.
     */
    public static void updateCustomer(String name, String address, String postal, String phone, int divID, int customerID){

        String sqlUpdate =  "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? WHERE Customer_ID = ?";
        try{
            ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, postal);
            ps.setString(4, phone);
            ps.setInt(5,divID);
            ps.setInt(6,customerID);
            ps.executeUpdate();
            System.out.println("customer update ran");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @return Creates a customer object from the customer's ID.
     */
    public static Customer createCustomerbyID(int id){
        int customerID = 0;
        String customerName = null;
        String address = null;
        String postal = null;
        String phone = null;
        int divID = 0;
        try{
            ps = conn.prepareStatement("SELECT * FROM customers WHERE Customer_ID = ?");
            ps.setInt(1, id);
            result = ps.executeQuery();
            if(result.next()){
                customerID = result.getInt("Customer_ID");
                customerName = result.getString("Customer_Name");
                address = result.getString("Address");
                postal = result.getString("Postal_Code");
                address = result.getString("Phone");
                divID = result.getInt("Division_ID");
            }
            Customer customer = new Customer(customerID,customerName,address,postal,phone,divID);
            return customer;

        }catch (SQLException e){
            e.printStackTrace();
            return null;
        } }

    /**
     *
     * @return List that contains a count of how many customers are in each country.
     */
    public static ObservableList<String> customersPerCountry(){
        ObservableList<String> customersInCountry = FXCollections.observableArrayList();
        try {
            String sql = "SELECT COUNT(customers.Customer_ID), countries.Country FROM" +
                    " customers INNER JOIN first_level_divisions ON" +
                    " customers.Division_ID = first_level_divisions.Division_ID INNER JOIN"+
                    " countries ON first_level_divisions.Country_ID = countries.Country_ID"+
                    " GROUP BY countries.Country";
            ps = conn.prepareStatement(sql);
            result = ps.executeQuery();
            while(result.next()){
                String stat = result.getString(2) + " " +result.getString(1);
                customersInCountry.add(stat);
            }
            return customersInCountry;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


}
