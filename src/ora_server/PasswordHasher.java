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
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author pibit
 */
public class PasswordHasher {

    public void generatePassowrd() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String password = "myPass";
        Random rand = new SecureRandom();
        byte[] saltBytes = new byte[16];
        rand.nextBytes(saltBytes);
        String salt = "";


        System.out.println("Salt in Hex");
        for (int i = 0; i < saltBytes.length; i++) {
            System.out.print(Integer.toHexString(0xFF & saltBytes[i]));
            salt = salt + (Integer.toHexString(0xFF & saltBytes[i]));

        }
        
        System.out.println("");

        
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 256);
        byte[] hashValue = keyFactory.generateSecret(keySpec).getEncoded();

        for (int i = 0; i < hashValue.length; i++) {
            System.out.print(Integer.toHexString(0xFF & hashValue[i]));
        }
        System.out.println("");
    }

}
