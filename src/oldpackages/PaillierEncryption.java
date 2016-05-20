/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oldpackages;

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
    //Our weak spot because the maximum bit length is 12 bits
    // after that it gets versy slow
    private final int BIT_LENGTH = 8;

    public void initialize() {

        boolean isGeneratedproperly = false;
        do {
            isGeneratedproperly = generatePrimeNumbers();
        } while (!isGeneratedproperly);
        System.out.println("p = " + p);
        System.out.println("q = " + q);
        this.n = calculateN(p, q);
        System.out.println("n = " + n);
        this.lamda = calculateSmallLamda(p, q);
        System.out.println("lamda = " + lamda);
        this.g = calculateSmallG(n);
        while (!verifyModularMultiplicativeInverse()) {
            this.g = calculateSmallG(n);
        }
        System.out.println("g = " + g);
        this.m = calculateMi();
        System.out.println("m = " + m);

    }

    public BigInteger encrypt(BigInteger msg) {
        BigInteger r = createRandomBigInteger();
        //System.out.println("r = " + r);
        BigInteger gPowmsg = g.pow(msg.intValueExact());
        BigInteger rPown = pow(r,n);
//        BigInteger rPown = r.modPow(n, gPowmsg);
        BigInteger nsqrd = n.pow(2);
        BigInteger cipher = (gPowmsg.multiply((rPown)).mod(nsqrd));

        return cipher;
    }

    public BigInteger decrypt(BigInteger c) {

        BigInteger nominator = LofU((c.pow(lamda.intValueExact())).mod(n.pow(2)));

        BigInteger pt = (nominator.multiply(m)).mod(n);

        return pt;
    }
    
    public BigInteger homomorphicAddition(BigInteger[] arrayWithEncryptedVotes){
        //The product of arrayWithEncryptedVotes results in the addition of the plaintexts
        //Magic of math :)
        
        BigInteger cipherProduct = new BigInteger("1");
        //multiply all the encrypted votes
        for(int i=0; i<arrayWithEncryptedVotes.length; i++){
            cipherProduct = cipherProduct.multiply(arrayWithEncryptedVotes[i]);
        }
        //do modulation and feed it to the decryption method
        return decrypt(cipherProduct.mod(n.pow(2)));
    }

    private BigInteger pow(BigInteger base, BigInteger exponent) {
        BigInteger result = BigInteger.ONE;
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) {
                result = result.multiply(base);
            }
            base = base.multiply(base);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    private BigInteger LofU(BigInteger u) {
        return (u.subtract(BigInteger.ONE)).divide(n);
    }

    public BigInteger[] createPublicKey() {
        BigInteger[] publicKey = new BigInteger[2];
        publicKey[0] = n;
        publicKey[1] = g;
        return publicKey;
    }

    public BigInteger[] createPrivateKey() {
        BigInteger[] privateKey = new BigInteger[2];
        privateKey[0] = lamda;
        privateKey[1] = m;
        return privateKey;
    }

    private boolean generatePrimeNumbers() {
        createP(System.currentTimeMillis());
        createQ(System.currentTimeMillis());

        //p and q must be of equal bit length
//        while (p.bitLength() != p.bitLength()) {
//            createP(System.currentTimeMillis());
//            createQ(System.currentTimeMillis());
//        }
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
//        smallLamda = calculatePhi();
        return smallLamda;
    }

    private BigInteger calculateSmallG(BigInteger n) {
        BigInteger g = n.add(BigInteger.ONE);
        return g;
    }

//    private BigInteger calculatePhi() {
//        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
//        return phi;
//    }

    private BigInteger calculateMi() {
        //BigInteger mi = lamda.modInverse(n);
        BigInteger mi = LofU((g.pow(lamda.intValueExact())).mod(n.pow(2)));
        mi = mi.modInverse(n);
        return mi;
    }

    /*
     *Ensure n divides the order of g by checking the existence of the following 
     *modular multiplicative inverse: m = (L(g^lambda mod n^2))^-1 \mod n
     *where function L is defined as L(u) = u-1/n . 
     */
    private boolean verifyModularMultiplicativeInverse() {
        return gcd((LofU(g.pow(lamda.intValueExact()))), n).equals(BigInteger.ONE);
    }

    private BigInteger createRandomBigInteger() {
        BigInteger y = new BigInteger(BIT_LENGTH, new Random(System.currentTimeMillis()));
        //System.out.println("y " + y);
//        int i = y.compareTo(n);
//        if (i == 1) {
//            y = y.subtract(n);
//        }
        return y;
    }

    private void createP(long randomSeed) {
        p = createPrimeNumber(randomSeed);

    }

    private void createQ(long randomSeed) {
        q = createPrimeNumber(randomSeed);

    }

    private BigInteger createPrimeNumber(long randomSeed) {
        BigInteger prime;
        prime = java.math.BigInteger.probablePrime(BIT_LENGTH, new Random(randomSeed));

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
