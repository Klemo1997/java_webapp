package main;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;


/**
 * @author adria
 *
 */
class RsaKeyGenerator {

	KeyPair getKeyPair() throws NoSuchAlgorithmException {
		
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
		return keyGen.generateKeyPair();
	}

	static byte[] encrypt(byte[] symmetricKey, PublicKey publicKey) throws Exception {
		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

		return encryptCipher.doFinal(symmetricKey);
	}
	 
	 static byte[] decrypt(byte[] cryptedKey, PrivateKey privateKey) throws Exception {
	        Cipher decriptCipher = Cipher.getInstance("RSA");
	        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

	        return decriptCipher.doFinal(cryptedKey);
	    }

	SecretKeySpec getSymmetricKey(){

        int length = 128/8;
        SecureRandom rnd = new SecureRandom();
        byte [] key = new byte [length];
        rnd.nextBytes(key);

		return new SecretKeySpec(key, "AES");
    }
	
}
