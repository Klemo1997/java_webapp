package main;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * @author adria
 *
 */
public class RsaKeyGenerator {
	
	private Cipher cipher;
	private PrivateKey privateKey;
    private PublicKey publicKey;
    private SecretKeySpec secretKey;

	public RsaKeyGenerator() throws NoSuchAlgorithmException, NoSuchPaddingException {
		// TODO Auto-generated constructor stub
		this.cipher = Cipher.getInstance("RSA");
	}
	public KeyPair getKeyPair() throws NoSuchAlgorithmException {
		
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
        return pair;
		
	}
	public PrivateKey getPrivate(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	// https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
	public PublicKey getPublic(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
	
	 public static byte[] encrypt(byte[] symmetricKey, PublicKey publicKey) throws Exception {
	        Cipher encryptCipher = Cipher.getInstance("RSA");
	        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

	        byte[] encryptedSymKey = encryptCipher.doFinal(symmetricKey);

	        return encryptedSymKey;
	    }
	 
	 public static byte[] decrypt(byte[] cryptedKey, PrivateKey privateKey) throws Exception {
	        Cipher decriptCipher = Cipher.getInstance("RSA");
	        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

	        return decriptCipher.doFinal(cryptedKey);
	    }

	public SecretKeySpec getSymmetricKey(){

        int length = 128/8;
        SecureRandom rnd = new SecureRandom();
        byte [] key = new byte [length];
        rnd.nextBytes(key);
        this.secretKey = new SecretKeySpec(key, "AES");

        return this.secretKey;
    }
	
}
