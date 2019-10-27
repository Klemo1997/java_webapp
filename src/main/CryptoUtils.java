package main;

import org.apache.commons.fileupload.FileUploadBase;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtils
{
    private static final String ALGORITHM="AES";
    private static final String TRANSFORMATION="AES";
    private static final String KEYSDIRECTORY = "C:/Users/matus/IdeaProjects/java_webapp/keys";
    private static final int FILE_SIZE_LIMIT = 536870912;

    public static void encrypt(File pubKey, File inputFile, File outputFile) throws Exception
    {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile, pubKey);
    }

    public static void decrypt(File privKey, File inputFile, File outputFile) throws Exception
    {
        doDecrypt(Cipher.DECRYPT_MODE, inputFile, outputFile, privKey);
    }

    private static void doCrypto(int cipherMode, File inputFile, File outputFile, File pubKey) throws Exception, FileUploadBase.FileSizeLimitExceededException
    {
        try {
            if (inputFile.length() > FILE_SIZE_LIMIT) {
                throw new FileUploadBase.FileSizeLimitExceededException("File size limit exceeded", inputFile.length(), FILE_SIZE_LIMIT);
            }

            RsaKeyGenerator rsa= new RsaKeyGenerator();

            // AES key gen
            Key secretKey = rsa.getSymmetricKey();
            String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int)inputFile.length()];

            // Output stream
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);

            ByteArrayOutputStream output = new ByteArrayOutputStream();


            byte[] encryptedSymKey = rsa.encrypt(secretKey.getEncoded(), getPublicKey(pubKey));

            // Na zaciatok dame kryptovany sym. kluc
            output.write(encryptedSymKey);
            // Na potom subor
            output.write(outputBytes);
            // Nakoniec Mac autentifinacny tag
            output.write(getMac(outputStream, outputBytes , secretKeyString));

            // Zapiseme do streamu
            outputStream.write(output.toByteArray());
          
            inputStream.close();
            outputStream.close();
        } catch(
            NoSuchPaddingException
            | NoSuchAlgorithmException
            | InvalidKeyException
            |BadPaddingException
            | IllegalBlockSizeException
            |IOException ex
        ) {
            throw new Exception("Errorencrypting/decryptingfile"+ex.getMessage(),ex);
        }
    }

    public static void doDecrypt(int cipherMode, File inputFile, File outputFile, File privKey) throws Exception {
        try {
            RsaKeyGenerator rsa= new RsaKeyGenerator();

            PrivateKey myPrivate = getPrivateKey(privKey);

            //Todo: get first 128 bytes from inputfile
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int)inputFile.length()];

            // Load file to inputbytes
            inputStream.read(inputBytes);

            byte[] cryptedKey = Arrays.copyOfRange(inputBytes, 0, 128);

            //erase them from inputbytes
            // File without mac and crypted key
            inputBytes = Arrays.copyOfRange(inputBytes, 128, inputBytes.length);

            // get symmetric key from crypted data
            Key cryptedKeyFromFile =  new SecretKeySpec(rsa.decrypt(cryptedKey, getPrivateKey(privKey)), "AES");

            // check mac
            if (!checkMac(inputFile, inputBytes, Base64.getEncoder().encodeToString(cryptedKeyFromFile.getEncoded()))) {
                // Throw manipulated file exception
                throw new IOException("File manipulated");
            }

            // erase mac from file
            inputBytes = Arrays.copyOfRange(inputBytes, 0, inputBytes.length - 32);

            // Init cipher to decrypt file
            Cipher decryptCipher = Cipher.getInstance(TRANSFORMATION);
            decryptCipher.init(cipherMode, cryptedKeyFromFile);

            byte[] outputBytes = decryptCipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();
        } catch(
                NoSuchPaddingException
                        | NoSuchAlgorithmException
                        | InvalidKeyException
                        |BadPaddingException
                        | IllegalBlockSizeException
                        |IOException ex
        ) {
            throw new Exception("Errorencrypting/decryptingfile"+ex.getMessage(),ex);
        }
    }

    
    public static byte[] getMac(FileOutputStream encrypted, byte[] outputBytes ,String key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        //Append last 16 bytes of mac
        byte[] authTag = new byte[32];

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec macKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        mac.init(macKey);
        mac.update(outputBytes);
        return Arrays.copyOfRange(mac.doFinal(), 0, 32);
     }

    public static boolean checkMac(File encrypted, byte[] inputBytes, String keyString) throws IOException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        int n = 32;
        int inputBytesLength = inputBytes.length;
        byte[] authTag = new byte[n];
        byte[] inputFileAuthTag = new byte[n];

        inputFileAuthTag = Arrays.copyOfRange(inputBytes,inputBytesLength - n, inputBytesLength);
        authTag =  getMac(null, Arrays.copyOfRange(inputBytes, 0, inputBytes.length - 32 ), keyString);

        inputBytes = Arrays.copyOfRange(inputBytes, 0, inputBytesLength - n);
        // Update array length
        inputBytesLength = inputBytes.length;

        for(int i = 0; i < n; i++) {
            if (inputFileAuthTag[i] != authTag[i]) {
                // Unequal bytes
                return false;
            }
        }
        return true;
    }

    public static PublicKey getPublicKey(File pubKeyFile) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {

        byte[] keyBytes = Files.readAllBytes(pubKeyFile.toPath());

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static PrivateKey getPrivateKey(File privKeyFile) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        byte[] keyBytes = Files.readAllBytes(privKeyFile.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
