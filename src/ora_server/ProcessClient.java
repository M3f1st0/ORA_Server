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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author Karolis
 */
public class ProcessClient implements Runnable {

    private final SSLSocket socket;
    private PrintWriter out = null;
    private BufferedReader in = null;

    public ProcessClient(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10000);
            send();
            terminateThread();
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void closeSocket(){
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ProcessClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void terminateThread(){
        Thread.currentThread().interrupt();
        System.out.println("Thread terminated");
    }
    
    public void send(){
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput = "Thread Testing";
            out.println(userInput);

            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + in.readLine());
            }
            out.println(userInput);

            // Closing the Streams and the Socket
            out.close();
            in.close();
            stdIn.close();
            socket.close();
        } catch (Exception exp) {
            System.out.println(" Exception occurred .... " + exp);
            exp.printStackTrace();
        }
    }

}
