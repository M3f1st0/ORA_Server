/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ora_server;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author panos
 */
public class Authenticator {

    private String username = "";
    private String password = "";
    private Random rand = new SecureRandom();
    private String salt;
    private String challengeAnswer = "";
    private String responseToChallenge = "";
    private int timesToRunHash = 0;
    private final int HASH_BIT_LENGTH = 256;

    public boolean findVoter(String userName) {
        System.out.println("Search voter in database...");
        return searchForUsername(userName, "electorate");

    }

    public boolean findAdmin(String userName) {
        System.out.println("search admin in database...");
        return searchForUsername(userName, "administrators");

    }

    public void calculateChallengeAnswer() {
        System.out.println("calculating challenge answer...");
        challengeAnswer = challengeFunction();

    }

    public boolean compareResults(String responseSentByClient) {
        System.out.println("comparing answers...");
        responseToChallenge = responseSentByClient;

        return challengeAnswer.equalsIgnoreCase(responseToChallenge);
    }

    public String sendChallenge() {
        //in here we gather 
        //the random number (how many times the hash will be ececuted)
        //the salt
        printAll();
        StringBuilder challengeParameters = new StringBuilder();
        challengeParameters.append(salt);
        challengeParameters.append(" ");
        challengeParameters.append(Integer.toString(timesToRunHash));

        return challengeParameters.toString();
    }

    private boolean searchForUsername(String uName, String tableName) {
        boolean found = false;
        //A call to DB Handler to find user
        if (DBHandler.findUser(uName, tableName)) {
            password = retreivePasswordFromDatabase(uName, tableName);
            salt = retreiveSaltFromDatabase(uName, tableName);
            found = true;
        }

        return found;
    }

    private String retreivePasswordFromDatabase(String userName, String tableName) {
        String pass = "";
        pass = DBHandler.getPass(userName, tableName);
        return pass;
    }

    private String retreiveSaltFromDatabase(String userName, String tableName) {
        String s = "";

        s = DBHandler.getSalt(userName, tableName);
        return s;

    }

    private String challengeFunction() {
        byte[] hashValue = null;
        String hashString="";
        timesToRunHash = rand.nextInt(2048);
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), timesToRunHash, HASH_BIT_LENGTH);
            hashValue = keyFactory.generateSecret(keySpec).getEncoded();
            for (int i = 0; i < hashValue.length; i++) {
                hashString = hashString + Integer.toHexString(0xFF & hashValue[i]);
            }

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hashString;
    }

    private void printAll() {
        System.out.println("------Server Data------");
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        System.out.println("salt: " + salt);
        System.out.print("challengeAnswer: " + challengeAnswer);
        System.out.println("");
        System.out.println("timesToRunHash: " + timesToRunHash);
        System.out.println("Hash bit length: " + HASH_BIT_LENGTH);
        System.out.println("------ENd Server Data--------");
    }

}
