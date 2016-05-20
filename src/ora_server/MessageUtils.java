/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ora_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Karolis
 */
public class MessageUtils {

    public static void sendMessage(SSLSocket socket, String message) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (Exception exp) {
            Logger.getLogger(MessageUtils.class.getName()).log(Level.SEVERE, null, exp);
        }
    }

    public static String receiveMessage(SSLSocket socket) {
        String message = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            message = in.readLine();
        } catch (SocketException ex){
            Logger.getLogger(MessageUtils.class.getName()).log(Level.SEVERE, null, ex);
            return "logout";
        } catch (IOException ex) {
            Logger.getLogger(MessageUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }

}
