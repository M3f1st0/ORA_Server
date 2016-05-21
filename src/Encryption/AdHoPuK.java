/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Encryption;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Panagiotis Bitharis
 */
public class AdHoPuK {

    private BigInteger p, q;
    private BigInteger n;
    private BigInteger g;
    private BigInteger lamda;
    private BigInteger m;
    private HomomorphicPrivateKey privK = null;
    private HomomorphicPublicKey pubK = null;
    //Our weak spot because the maximum bit length is 12 bits
    // after that it gets versy slow
    private final int BIT_LENGTH = 8;
    private final Path privateKeyPath = Paths.get("src/resources/adhoPrk.key");
    private final Path publicKeyPath = Paths.get("src/resources/adhoPuk.key");
    private boolean encryption = false;
    private boolean decryption = false;
    public enum Cipher {
        ENCRYPT_MODE, DECRYPT_MODE
    };

    public void init(Cipher mode, ObjectKey key) {
        if (mode == Cipher.ENCRYPT_MODE) {
            encryption = true;
            pubK = (HomomorphicPublicKey) key;
        } else if (mode == Cipher.DECRYPT_MODE) {
            decryption = true;
            privK = (HomomorphicPrivateKey) key;
        }
    }
    
    
    public BigInteger doFinal(BigInteger... params){
        BigInteger result = null;
        if(encryption && !decryption){
            //encrypt the message
            result = encrypt(params[0],pubK);
        }else if(decryption && !encryption){
            //decrypt the message
            result = additiveDecryption(privK,params);
        }else{
            throw new IllegalStateException("The object is both in Encryption and Decryption mode");
        }
        return result;
    }
    
    private BigInteger encrypt(BigInteger plainText, HomomorphicPublicKey puk){
        BigInteger r = createRandomBigInteger();
        BigInteger g = puk.getG();
        BigInteger n = puk.getN();
        //System.out.println("r = " + r);
        BigInteger gPowmsg = g.pow(plainText.intValueExact());
        BigInteger rPown = pow(r, n);
//        BigInteger rPown = r.modPow(n, gPowmsg);
        BigInteger nsqrd = n.pow(2);
        BigInteger cipher = (gPowmsg.multiply((rPown)).mod(nsqrd));

        return cipher;
    }
    
    private BigInteger additiveDecryption(HomomorphicPrivateKey prK, BigInteger... votes){
        //The product of arrayWithEncryptedVotes results in the addition of the plaintexts
        //Magic of math :)
        BigInteger m =prK.getM();
        BigInteger lamda =prK.getLamda();
        BigInteger cipherProduct = new BigInteger("1");
        //multiply all the encrypted votes
        for (int i = 0; i < votes.length; i++) {
            cipherProduct = cipherProduct.multiply(votes[i]);
        }
        //do modulation and feed it to the decryption method
        return decrypt(cipherProduct.mod(n.pow(2)));
    }
    
    private BigInteger decrypt(BigInteger c) {

        BigInteger nominator = LofU((c.pow(lamda.intValueExact())).mod(n.pow(2)));

        BigInteger pt = (nominator.multiply(m)).mod(n);

        return pt;
    }

    /*
    This method is execute only once to generate the Public-Private key pairs
    if they are not located in the server's parent directory of the application
     */
    public void generateKeyPair() {

        do {
            //We start by generating two prime numbers according to the standards
            //of the algorithm
            boolean isGeneratedproperly = false;
            do {
                isGeneratedproperly = generatePrimeNumbers();
            } while (!isGeneratedproperly);

            //we calculate the n
            this.n = calculateN(p, q);
            System.out.println("n = " + n);

            //we calculate the lamda value
            this.lamda = calculateSmallLamda(p, q);
            System.out.println("lamda = " + lamda);

            //we calculate the g value
            this.g = calculateSmallG(n);
            while (!verifyModularMultiplicativeInverse()) {
                this.g = calculateSmallG(n);
            }
            System.out.println("g = " + g);

            //we calculate the m value
            this.m = calculateMi();
            System.out.println("m = " + m);
        } while (!encryptionDecryptionTest());
        createPublicKey();
        createPrivateKey();

    }


    /**
     * @return the private key from file
     */
    public HomomorphicPrivateKey getPrivateKey() {
        ObjectInputStream input = null;
        HomomorphicPrivateKey privateKey=null;
        try {
            input = new ObjectInputStream(Files.newInputStream(privateKeyPath));
            privateKey = (HomomorphicPrivateKey)input.readObject();
            
        } catch (IOException ex) {
            Logger.getLogger(AdHoPuK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AdHoPuK.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
                Logger.getLogger(AdHoPuK.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return privateKey;
    }

    /**
     * @return the public key from file
     */
    public HomomorphicPublicKey getPublicKey() {
        ObjectInputStream input = null;
        HomomorphicPublicKey publicKey=null;
        try {
            input = new ObjectInputStream(Files.newInputStream(publicKeyPath));
            publicKey = (HomomorphicPublicKey)input.readObject();
            
        } catch (IOException ex) {
            Logger.getLogger(AdHoPuK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AdHoPuK.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
                Logger.getLogger(AdHoPuK.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return publicKey;
    }
    
    private boolean generatePrimeNumbers() {
        createP(System.currentTimeMillis());
        createQ(System.currentTimeMillis());

        //verify p,q property gcd(pq, (p-1)(q-1))=1
        if (gcd(p.multiply(q), (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)))).equals(BigInteger.ONE)) {
            return true;
        } else {
            return false;
        }

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

        return y;
    }

    private void createPublicKey() {
        ObjectOutputStream output = null;
        try {
            BigInteger[] publicKey = new BigInteger[2];
            publicKey[0] = n;
            publicKey[1] = g;
            HomomorphicPublicKey pubK = new HomomorphicPublicKey(publicKey);
            output = new ObjectOutputStream(Files.newOutputStream(publicKeyPath));
            //Write the public key to a file
            output.writeObject(pubK);
        } catch (IOException ex) {
            Logger.getLogger(AdHoPuK.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(AdHoPuK.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void createPrivateKey() {
        ObjectOutputStream output = null;
        try {
            BigInteger[] privateKey = new BigInteger[2];
            privateKey[0] = lamda;
            privateKey[1] = m;
            HomomorphicPrivateKey privK = new HomomorphicPrivateKey(privateKey);
            output = new ObjectOutputStream(Files.newOutputStream(privateKeyPath));
            //Write the private key to a file
            output.writeObject(privK);
        } catch (IOException ex) {
            Logger.getLogger(AdHoPuK.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /*
    a small test that verifies that the encryption will work 
    with these prime numbers and the derived keys.
    All methods containing the Test suffix belong to the encryption test
    mechanisme and should not be used during the official
    encryption or decryption algorithm
     */
    private boolean encryptionDecryptionTest() {
        boolean result = false;
        BigInteger sum = BigInteger.ZERO;

        //we create 15 random values and add them
        BigInteger[] votes = new BigInteger[15];

        //we create 15 random values and add them
        for (int i = 0; i < 15; i++) {
            //a 3-bit length random BigInteger value and assign it to the "votes[]" array
            votes[i] = new BigInteger(3, new SecureRandom());

            //we add each value to the previous summ
            sum = sum.add(votes[i]);

            //We encryptTest the value array and replace it inside the "votes[]" array
            votes[i] = encryptTest(votes[i]);
        }
        //false test
        //sum.add(BigInteger.ONE);

        if (sum.equals(homomorphicAdditionTest(votes))) {
            result = true;
            System.out.println("sum= " + sum);
        } else {
            System.out.println("False");
        }

        return result;
    }

    private BigInteger encryptTest(BigInteger msg) {
        BigInteger r = createRandomBigInteger();
        //System.out.println("r = " + r);
        BigInteger gPowmsg = g.pow(msg.intValueExact());
        BigInteger rPown = pow(r, n);
//        BigInteger rPown = r.modPow(n, gPowmsg);
        BigInteger nsqrd = n.pow(2);
        BigInteger cipher = (gPowmsg.multiply((rPown)).mod(nsqrd));

        return cipher;
    }

    private BigInteger decryptTest(BigInteger c) {

        BigInteger nominator = LofU((c.pow(lamda.intValueExact())).mod(n.pow(2)));

        BigInteger pt = (nominator.multiply(m)).mod(n);

        return pt;
    }
    
    public BigInteger homomorphicAdditionTest(BigInteger[] arrayWithEncryptedVotes){
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

    /*
            Helper methods for complementing functionalities
            that are not supported by Java BigInteger library
    
     */
 /*
    method for caculating the gcd of two BigIntegers
     */
    private BigInteger gcd(BigInteger p, BigInteger q) {
        if ((p.mod(q)).equals(BigInteger.ZERO)) {

            return q;
        }

        return gcd(q, p.mod(q));
    }

    /*
    Method for calculating the lcm of two BigIntegerrs
     */
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

    /*
    method for calculating the pow of two BigIntegers
     */
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

}
