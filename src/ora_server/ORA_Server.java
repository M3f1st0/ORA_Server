/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ora_server;

/**
 *
 * @author pibit
 */
public class ORA_Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerConnectionManager connectionManager = new ServerConnectionManager();
        connectionManager.connectToDB();
        connectionManager.processClient();
    }
}
