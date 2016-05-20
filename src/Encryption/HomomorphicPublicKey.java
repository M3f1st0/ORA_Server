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
public class HomomorphicPublicKey extends ObjectKey implements Serializable{
    private BigInteger n=null;
    private BigInteger g=null;
    BigInteger[] publicKey=new BigInteger[2];
    
   
    
    public HomomorphicPublicKey(BigInteger[] puK){
        super(puK);
        n = puK[0];
        g=puK[1];
    }

    /**
     * @return the n
     */
    public BigInteger getN() {
        return n;
    }

    /**
     * @return the g
     */
    public BigInteger getG() {
        return g;
    }
    
}
