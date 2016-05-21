/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ora_server;

import Encryption.AdHoPuK;
import Encryption.HomomorphicPrivateKey;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Karolis
 */
public class ProcessAdmin implements Runnable {

    private final SSLSocket socket;
    private boolean keepProcessing = true;
    
    public ProcessAdmin(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            authenticate();
            while (keepProcessing) {
                System.out.println("Admin waiting for command...");
                String command = MessageUtils.receiveMessage(socket);
                if (command.contentEquals("set_question")) {
                    String question = MessageUtils.receiveMessage(socket);
                    setQuestion(question);
                    MessageUtils.sendMessage(socket, "ACK");
                } else if (command.contentEquals("logout")) {
                    System.out.println("Admin Logout");
                    keepProcessing = false;
                } else if (command.contentEquals("get_result")) {
                    MessageUtils.sendMessage(socket, getResult());
                }
            }
            terminateThread();
        } catch (Exception ex) {
            Logger.getLogger(ProcessClient.class.getName()).log(Level.SEVERE, null, ex);
            keepProcessing = false;
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

    public void authenticate() {
        Authenticator authenticator = new Authenticator();
        //sent me your username
        System.out.println("Server: GET USERNAME");
        MessageUtils.sendMessage(socket, "GET USERNAME");
        String uname = MessageUtils.receiveMessage(socket);
        System.out.println("Client: " + uname);
        if (authenticator.findAdmin(uname)) {
            System.out.println("Voter found");
            authenticator.calculateChallengeAnswer();
            String challengeParams = authenticator.sendChallenge();
            System.out.println("Server: " + challengeParams);
            MessageUtils.sendMessage(socket, challengeParams);
        } else {
            keepProcessing = false;
            MessageUtils.sendMessage(socket, "NotFound");
            return;
        }
        String challengeResult = MessageUtils.receiveMessage(socket);
        System.out.println("Client: " + challengeResult);
        if (authenticator.compareResults(challengeResult)) {
            //Access granted
            MessageUtils.sendMessage(socket, "OK");
            System.out.println("Server: OK");
        } else {
            MessageUtils.sendMessage(socket, "FAIL");
            System.out.println("FAIL");
            keepProcessing = false;
        }
    }

    public void setQuestion(String question) {
        try {
            File questionFile = new File("question.txt");
            if (questionFile.exists()) {
                questionFile.delete();
            }
            questionFile.createNewFile();
            PrintWriter out = new PrintWriter("question.txt");
            out.println(question);
            questionFile.setReadOnly();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ProcessAdmin.class.getName()).log(Level.SEVERE, null, ex);
            MessageUtils.sendMessage(socket, "NAK");
        }
    }

    private String getResult() {
        
        AdHoPuK decryptor = new AdHoPuK();
        HomomorphicPrivateKey key = decryptor.getPrivateKey();
        decryptor.init(AdHoPuK.Cipher.DECRYPT_MODE, key);
        BigInteger[] votesList = DBHandler.getVoteList();
        BigInteger yesVotes = decryptor.doFinal(votesList);
        BigInteger totalVotes = DBHandler.getTotalVotes();
        BigInteger noVotes = totalVotes.subtract(yesVotes);
        String result = yesVotes.toString()+":"+noVotes.toString();
        
        return result;
    }
    
    
}
