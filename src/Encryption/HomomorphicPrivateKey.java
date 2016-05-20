/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Encryption;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * @author Panagiotis Bitharis
 */
public class HomomorphicPrivateKey extends ObjectKey implements Serializable{
    
    private BigInteger lamda = null;
    private BigInteger m = null;
    
    public HomomorphicPrivateKey(BigInteger[] prk){
        super(prk);
        lamda = prk[0];
        m = prk[1];      
    }

    /**
     * @return the lamda
     */
    public BigInteger getLamda() {
        return lamda;
    }

    /**
     * @return the m
     */
    public BigInteger getM() {
        return m;
    }

    
    
}
