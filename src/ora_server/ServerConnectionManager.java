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

/**
 *
 * @author Karolis
 */
public class ServerConnectionManager {

    private final int intSSLport = 4443;

    public void processClient() {
        // Registering the JSSE provider
        Security.addProvider(new Provider());

        //Specifying the Keystore details
        System.setProperty("javax.net.ssl.keyStore", "src/resources/orakeystore.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "kspasswd");

        // Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
        System.setProperty("javax.net.debug","all");
        try {
            // Initialize the Server Socket
            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(intSSLport);
            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

            // Create Input / Output Streams for communication with the client
            while (true) {
                PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                sslSocket.getInputStream()));
                String inputLine, outputLine;

                while ((inputLine = in.readLine()) != null) {
                    out.println(inputLine);
                    System.out.println(inputLine);
                }

                // Close the streams and the socket
                out.close();
                in.close();
                sslSocket.close();
                sslServerSocket.close();

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
}
