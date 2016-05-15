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
public class ProcessClient implements Runnable {

    private final SSLSocket socket;

    public ProcessClient(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10000);
            System.out.println("CLient Thread Started");
            Thread.sleep(5000);
            terminateThread();
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void terminateThread() {
        System.out.println("Client Thread terminating");
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
