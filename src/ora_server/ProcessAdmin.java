/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ora_server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Karolis
 */
public class ProcessAdmin implements Runnable {

    private final SSLSocket socket;

    public ProcessAdmin(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Authenticator authenticator = new Authenticator();
            //sent me your username
            MessageUtils.sendMessage(socket, "GET USERNAME");
            String uname = MessageUtils.receiveMessage(socket);
            if(authenticator.findAdmin(uname)){
                authenticator.calculateChallengeAnswer();
                MessageUtils.sendMessage(socket, authenticator.sendChallenge());
            }
            String result = MessageUtils.receiveMessage(socket);
            if(authenticator.compareResults(result.getBytes())){
                //Access granted
                MessageUtils.sendMessage(socket, "ACCESS GRANTED");
            }
            Thread.sleep(10000);
            System.out.println("Admin Thread Started");
            Thread.sleep(5000);
            terminateThread();
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void terminateThread() {
        System.out.println("Admin Thread terminating");
        closeSocket();
        Thread.currentThread().interrupt();
    }

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ProcessClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
