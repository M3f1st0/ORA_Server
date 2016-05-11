/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ora_server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Karolis
 */
public class DBHandlerTest {

    public DBHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of connect method, of class DBHandler.
     */
    @Test
    public void testConnect() throws SQLException {
        String URL = "jdbc:mysql://127.0.0.1:3306/mydb?user=root&password=root";
        Connection c = DriverManager.getConnection(URL);
        assertEquals(true, !c.isClosed());
        c.close();
    }
}
