package ora_server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Panagiotis Bitharis
 */
public abstract class DBHandler {

    private static Connection c;

    private static PreparedStatement listAdmins;

    public static void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            String URL = "jdbc:mysql://127.0.0.1:3306/mydb?user=root&password=toor";
            //String URL = "jdbc:mysql://127.0.0.1:3306/mydb?user=root&password=root";
            c = DriverManager.getConnection(URL);
            if (!c.isClosed()) {
                System.out.println("Connection established with Database");
            } else {
                System.out.println("Not established");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {

        }
    }

    public static boolean findUser(String userName, String tableName) {
        boolean found = false;
        try {
            PreparedStatement findUser = c.prepareStatement("SELECT * FROM " + tableName + " WHERE username=?;");
            findUser.setString(1, userName);
            ResultSet rs = findUser.executeQuery();

            while (rs.next()) {
                if (userName.contentEquals(rs.getString("username"))) {
                    found = true;
                } else {
                    throw new Exception();
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return found;
    }
    
    public static String getSalt(String userName, String tableName){
        String salt="";
        try {
            System.out.println("Username:"+userName);
            System.out.println("Tablename: "+tableName);
            PreparedStatement findUser = c.prepareStatement("SELECT saltVal FROM " + tableName + " WHERE username=?;");
            findUser.setString(1, userName);
            ResultSet rs = findUser.executeQuery();
            while(rs.next()){
                salt = rs.getString("saltVal");
            }
            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return salt;
    }
    
    public static String getPass(String userName, String tableName){
        String pass="";
        try {
            PreparedStatement findUser = c.prepareStatement("SELECT password FROM " + tableName + " WHERE username=?");
            findUser.setString(1, userName);
            ResultSet rs = findUser.executeQuery();
            
            while(rs.next()){
                pass = rs.getString("password");
            }
            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return pass;
    }
}
