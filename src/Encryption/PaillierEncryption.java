/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Encryption;

import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author Panagiotis Bitharis
 */
public class PaillierEncryption {

    private BigInteger p, q;
    private BigInteger n;
    private BigInteger g;
    private BigInteger lamda;
    private BigInteger m;
    private final int NUMBER_OF_BITS = 5;
    
    public void initialize(){
        generatePrimeNumbers();
        this.n = calculateN(p, q);
        this.lamda = calculateSmallLamda(p, q);
        this.g = calculateSmallG(n);
        verifyModularMultiplicativeInverse();
    }
    
    public BigInteger encrypt(BigInteger p){
        BigInteger r = createRandomBigInteger();
        //NOT WORKING
        BigInteger cipher = (g.pow(p.intValueExact())).multiply((r.pow(n.intValueExact()))).mod(n.pow(2));
        System.out.println(cipher);
        
        return cipher;
    }
    
    public BigInteger decrypt(byte[] c){
        BigInteger sum=null;
        
        return sum;
    }

    public BigInteger[] createPublicKey() {
        BigInteger[] publicKey = new BigInteger[2];
        publicKey[0] = n;
        publicKey[1] = g;
        return publicKey;
    }
    
    public BigInteger[] createPrivateKey(){
        BigInteger[] privateKey = new BigInteger[2];
        privateKey[0] = lamda;
        privateKey[1] = m;
        return privateKey;
    }

    private boolean generatePrimeNumbers() {
        createP(System.currentTimeMillis());
        createQ(System.currentTimeMillis());

        while (p.bitLength() != p.bitLength()) {
            createP(System.currentTimeMillis());
            createQ(System.currentTimeMillis());
        }
        //verify p,q property gcd(pq, (p-1)(q-1))=1
        if (gcd(p.multiply(q), (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)))).equals(BigInteger.ONE)) {
            return true;
        } else {
            return false;
        }

    }

    private BigInteger calculateN(BigInteger p, BigInteger q) {
        return p.multiply(q);
    }

    private BigInteger calculateSmallLamda(BigInteger p, BigInteger q) {
        // lambda=lcm}(p-1,q-1)
        BigInteger smallLamda;
        smallLamda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
        System.out.println("lamda "+smallLamda);
        return smallLamda;
    }

    private BigInteger calculateSmallG(BigInteger n) {
        BigInteger g = createRandomBigInteger();

        if ((g.compareTo(this.g)) == -1) {
            g.add(n.multiply(n));
        }
        return g;
    }

    /*
    *Ensure n divides the order of g by checking the existence of the following 
    *modular multiplicative inverse: m = (L(g^lambda mod n^2))^-1 \mod n
    *where function L is defined as L(u) = u-1/n . 
     */
    private boolean verifyModularMultiplicativeInverse() {
        if(gcd(g,n).equals(BigInteger.ONE)){
            System.out.println("Multiplicative inverse of g mod n exists");
        } 
        System.out.println("gcd(g,n)= "+gcd(g,n));
        m = g.modInverse(n);
        BigInteger x = g.multiply(m);
        
        //checking if x is g*x is congruent to 1 mod n
        System.out.println("x ="+x);
        System.out.println("x mod n ="+x.mod(n));
        System.out.println("1 mod n = "+BigInteger.ONE.mod(n));
        System.out.println("m ="+m);

        return true;
    }
    
    private BigInteger createRandomBigInteger(){
        BigInteger x = new BigInteger(NUMBER_OF_BITS, new Random());

        if ((x.compareTo(g)) == -1) {
            x.add(n.multiply(n));
        }
        return x;
    }

    private void createP(long randomSeed) {
        p = createPrimeNumber(randomSeed);

    }

    private void createQ(long randomSeed) {
        q = createPrimeNumber(randomSeed);

    }

    private BigInteger createPrimeNumber(long randomSeed) {
        BigInteger prime;
        prime = java.math.BigInteger.probablePrime(NUMBER_OF_BITS, new Random(randomSeed));

        return prime;
    }

    private BigInteger gcd(BigInteger p, BigInteger q) {
        if ((p.mod(q)).equals(BigInteger.ZERO)) {

            return q;
        }

        return gcd(q, p.mod(q));
    }

    private BigInteger lcm(BigInteger pminus, BigInteger qminus1) {
        return pminus.multiply(qminus1.divide(gcd(pminus, qminus1)));
    }

    private BigInteger lcm(BigInteger[] input) {
        BigInteger result = input[0];
        for (int i = 1; i < input.length; i++) {
            result = lcm(result, input[i]);
        }
        return result;
    }

    /**
     * @return the p
     */
    public BigInteger getP() {
        return p;
    }

    /**
     * @return the q
     */
    public BigInteger getQ() {
        return q;
    }
}
