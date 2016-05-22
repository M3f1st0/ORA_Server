package ora_server;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
//            String URL = "jdbc:mysql://127.0.0.1:3306/mydb?user=root&password=root";
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

    public static int checkHasVoted(String username) {
        int hasVoted = 0;

        try {
            PreparedStatement voterStatus = c.prepareStatement("SELECT * FROM electorate WHERE username=?;");
            voterStatus.setString(1, username);
            ResultSet rs = voterStatus.executeQuery();

            while (rs.next()) {
                if (username.contentEquals(rs.getString("username"))) {
                    hasVoted = rs.getInt("hasSubmitedVote");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hasVoted;
    }

    public static void updateVoterStatus(String username) {
        try {
            PreparedStatement updateStatus = c.prepareStatement("UPDATE mydb.electorate SET hasSubmitedVote=? WHERE username=?");
            updateStatus.setInt(1, 1);
            updateStatus.setString(2, username);
            updateStatus.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updateVotes(String username, String vote) {
        try {
            PreparedStatement updateVotes = c.prepareStatement("UPDATE mydb.electorate SET Vote=? WHERE username=?");
            updateVotes.setString(1, vote);
            updateVotes.setString(2, username);
            updateVotes.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getSalt(String userName, String tableName) {
        String salt = "";
        try {
            PreparedStatement findUser = c.prepareStatement("SELECT saltVal FROM " + tableName + " WHERE username=?;");
            findUser.setString(1, userName);
            ResultSet rs = findUser.executeQuery();
            while (rs.next()) {
                salt = rs.getString("saltVal");
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return salt;
    }

    public static String getPass(String userName, String tableName) {
        String pass = "";
        try {
            PreparedStatement findUser = c.prepareStatement("SELECT password FROM " + tableName + " WHERE username=?");
            findUser.setString(1, userName);
            ResultSet rs = findUser.executeQuery();

            while (rs.next()) {
                pass = rs.getString("password");
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return pass;
    }

    public static BigInteger[] getVoteList() {
        BigInteger[] votes = new BigInteger[countTableRows("mydb.electorate")];
        int index = 0;
        try {

            PreparedStatement selectVotes = c.prepareCall("SELECT Vote from mydb.electorate WHERE hasSubmitedVote=1;");
            ResultSet rs = selectVotes.executeQuery();
            while (rs.next()) {
                if (!rs.getString("Vote").isEmpty()) {
                    votes[index] = new BigInteger(rs.getString("Vote"));
                    index++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return votes;
    }

    public static BigInteger getTotalVotes() {
        return BigInteger.valueOf(countTableRows("mydb.electorate"));
    }

    private static int countTableRows(String tableName) {
        int rows = 0;
        try {
            PreparedStatement countRows = c.prepareStatement("SELECT COUNT(*) FROM " + tableName + " WHERE hasSubmitedVote=1;");
            ResultSet rs = countRows.executeQuery();
            while (rs.next()) {
                rows = rs.getInt("COUNT(*)");
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return rows;
    }
}
