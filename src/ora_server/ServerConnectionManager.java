/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ora_server;

import java.io.*;
import java.security.Security;
import java.security.PrivilegedActionException;

import javax.net.ssl.*;
import com.sun.net.ssl.*;
import com.sun.net.ssl.internal.ssl.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karolis
 */
public class ServerConnectionManager {

    private final int intSSLport = 4443;
    private boolean keepProcessing = true;

    public void processClient() {
        // Registering the JSSE provider
        Security.addProvider(new Provider());

        //Specifying the Keystore details
        System.setProperty("javax.net.ssl.keyStore", "src/resources/orakeystore.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "kspasswd");

        // Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
        System.setProperty("javax.net.debug", "all");
        try {
            // Initialize the Server Socket
            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(intSSLport);

            while (keepProcessing) {
                System.out.println("wait connection");
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                process(sslSocket);
            }

        } catch (Exception exp) {
            PrivilegedActionException priexp = new PrivilegedActionException(exp);
            System.out.println(" Priv exp --- " + priexp.getMessage());
            System.out.println(" Exception occurred .... " + exp);
            exp.printStackTrace();
        }
    }

    public void connectToDB() {
        DBHandler.connect();
    }

    public void process(SSLSocket socket) {
        String type = MessageUtils.receiveMessage(socket);
        if (type.contentEquals("Client")) {
            ProcessClient processClient = new ProcessClient(socket);
            Thread clientConnection = new Thread(processClient);
            clientConnection.start();
        } else if (type.contentEquals("Admin")) {
            ProcessAdmin processAdmin = new ProcessAdmin(socket);
            Thread clientConnection = new Thread(processAdmin);
            clientConnection.start();
        }
    }

    public void stopProcessing() {
        keepProcessing = false;
    }

}
