import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * @author adria
 *
 */
public class RsaKeyGenerator {
	
	private Cipher cipher;
	private PrivateKey privateKey;
    private PublicKey publicKey;

	public RsaKeyGenerator() throws NoSuchAlgorithmException, NoSuchPaddingException {
		// TODO Auto-generated constructor stub
		this.cipher = Cipher.getInstance("RSA");

	}
	public KeyPair getKeyPair(String filename) throws NoSuchAlgorithmException {
		
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
	
	 public static String encrypt(String key, PublicKey publicKey) throws Exception {
	        Cipher encryptCipher = Cipher.getInstance("RSA");
	        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

	        byte[] cipherText = encryptCipher.doFinal(key.getBytes(UTF_8));

	        return Base64.getEncoder().encodeToString(cipherText);
	    }
	 
	 public static String decrypt(String key, PrivateKey privateKey) throws Exception {
	        byte[] bytes = Base64.getDecoder().decode(key);

	        Cipher decriptCipher = Cipher.getInstance("RSA");
	        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

	        return new String(decriptCipher.doFinal(bytes), UTF_8);
	    }
	
}
