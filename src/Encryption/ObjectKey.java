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
public abstract class ObjectKey implements Serializable{
    private BigInteger[] key = new BigInteger[2];
    
    public ObjectKey(BigInteger[] key){
        this.key = key;
    }

    /**
     * @return the key
     */
    public BigInteger[] getKey() {
        return key;
    }
    
}
