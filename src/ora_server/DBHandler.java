package ora_server;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Karolis
 */
public abstract class DBHandler {

    private static Connection c;

    
    
    private static PreparedStatement listAdmins;

    public static void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            String URL = "jdbc:mysql://127.0.0.1:3306/mydb?user=root&password=root";
            c = DriverManager.getConnection(URL);
            if(!c.isClosed()){
                System.out.println("Connection established with Database");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {

        }
    }
    
    public static int login(String name, String pass) {
        int customerID = -1;
        try {

            PreparedStatement findUser = c.prepareStatement("SELECT * FROM admins WHERE adminUserName = ?");
            findUser.setString(1, name);
            ResultSet rs = findUser.executeQuery();

            while (rs.next()) {
                if (pass.equals(rs.getString("adminPassword")) && name.contentEquals(rs.getString("adminUserName"))) {
                    customerID = rs.getInt("adminID");
                } else {
                    throw new Exception();
                }
            }

        } catch (Exception ex) {

        }
        return customerID;
    }

    
}
