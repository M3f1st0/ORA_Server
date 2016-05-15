/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ora_server;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
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
    private String password= null;
    private Random rand = new SecureRandom();
    private String salt;
    private byte[] challengeAnswer=null;
    private byte[] responseToChallenge=null;
    private int timesToRunHash=0;
    private final int HASH_BIT_LENGTH = 512;
    
    
    
    public boolean findVoter(String userName){
        
        return searchForUsername(userName, "electorate");
        
    }
    
    public boolean findAdmin(String userName){
        
        return searchForUsername(userName, "administrators");
        
    }
    
    public void calculateChallengeAnswer(){
        
        challengeAnswer = challengeFunction();
    }
    
    public boolean  compareResults(byte[] responseSentByClient){
        responseToChallenge = responseSentByClient;
        
        return Arrays.equals(responseToChallenge, challengeAnswer);
    }
    
    public String sendChallenge(){
        //in here we gather 
        //the random number (how many times the hash will be ececuted)
        //the salt
        StringBuilder challengeParameters =new StringBuilder();
        challengeParameters.append(salt);
        challengeParameters.append(" ");
        challengeParameters.append(Integer.toString(timesToRunHash));
        
        return challengeParameters.toString();
    }
     
    private boolean searchForUsername(String uName, String tableName){
        boolean found = false;
        //A call to DB Handler to find user
        if(DBHandler.findUser(uName, tableName)){
            password = retreivePasswordFromDatabase(uName,tableName);
            salt = retreiveSaltFromDatabase(uName,tableName);
            found = true;
        }
        
        return found;
    }
    
    private String retreivePasswordFromDatabase(String userName, String tableName){
        String pass="";
        pass = DBHandler.getPass(userName, tableName);
        return pass;
    }
    
    private String retreiveSaltFromDatabase(String userName, String tableName){
        String s="";
        
        s = DBHandler.getSalt(userName, tableName);
        return s;
        
    }
    
    private byte[] challengeFunction(){
        byte[] hashValue=null;
        timesToRunHash = rand.nextInt(2048);
        try {
            
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), timesToRunHash, HASH_BIT_LENGTH);
            hashValue = keyFactory.generateSecret(keySpec).getEncoded();
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hashValue;
    } 
    
}
