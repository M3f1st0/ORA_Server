/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ora_server;

import Encryption.AdHoPuK;
import Encryption.HomomorphicPrivateKey;
import Encryption.HomomorphicPublicKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author pibit
 */
public class ORA_Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Path prKeyFile = Paths.get("src/resources/adhoPrk.key");
        Path puKeyFile = Paths.get("src/resources/adhoPuk.key");
        if(Files.notExists(prKeyFile) || Files.notExists(puKeyFile)){
            AdHoPuK keyGenerator = new AdHoPuK();
            keyGenerator.generateKeyPair();
        }
        ServerConnectionManager connectionManager = new ServerConnectionManager();
        connectionManager.connectToDB();
        connectionManager.processClient();
    }
}
