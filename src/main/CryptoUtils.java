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
import java.util.HashMap;

class CryptoUtils
{
    private static final String ALGORITHM="AES";
    private static final String TRANSFORMATION="AES";
    private static final int FILE_SIZE_LIMIT = 536870912;

    static void encrypt(File pubKey, File inputFile, File outputFile) throws Exception
    {
        doCrypto(inputFile, outputFile, pubKey);
    }

    static void decrypt(File privKey, File inputFile, File outputFile) throws Exception
    {
        doDecrypt(inputFile, outputFile, privKey);
    }

    private static void doCrypto(File inputFile, File outputFile, File pubKey) throws Exception {
        try {
            if (inputFile.length() > FILE_SIZE_LIMIT) {
                throw new FileUploadBase.FileSizeLimitExceededException("File size limit exceeded", inputFile.length(), FILE_SIZE_LIMIT);
            }

            RsaKeyGenerator rsa= new RsaKeyGenerator();

            // AES key gen
            Key secretKey = rsa.getSymmetricKey();
            String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int)inputFile.length()];

            // Output stream
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);

            ByteArrayOutputStream output = new ByteArrayOutputStream();


            byte[] encryptedSymKey = RsaKeyGenerator.encrypt(secretKey.getEncoded(), getPublicKey(pubKey));

            // Na zaciatok dame kryptovany sym. kluc
            output.write(encryptedSymKey);
            // Na potom subor
            output.write(outputBytes);
            // Nakoniec Mac autentifinacny tag
            output.write(getMac(outputBytes , secretKeyString));

            // Zapiseme do streamu
            outputStream.write(output.toByteArray());

            if (!pubKey.getName().equals("inputKey")) {
                pubKey.delete();
            }

            inputStream.close();
            outputStream.close();
        } catch(Exception ex) {
            throw new Exception("Errorencrypting/decryptingfile"+ex.getMessage(),ex);
        }
    }

    private static void doDecrypt(File inputFile, File outputFile, File privKey) throws Exception {
        try {

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int)inputFile.length()];

            // Load file to inputbytes
            inputStream.read(inputBytes);
            //Get first 128 bytes from inputfile
            byte[] cryptedKey = Arrays.copyOfRange(inputBytes, 0, 128);

            //erase them from inputbytes
            // File without mac and crypted key
            inputBytes = Arrays.copyOfRange(inputBytes, 128, inputBytes.length);

            // get symmetric key from crypted data
            Key cryptedKeyFromFile =  new SecretKeySpec(RsaKeyGenerator.decrypt(cryptedKey, getPrivateKey(privKey)), ALGORITHM);

            // check mac
            if (!checkMac(inputBytes, Base64.getEncoder().encodeToString(cryptedKeyFromFile.getEncoded()))) {
                // Throw manipulated file exception
                throw new IOException("File manipulated");
            }

            // erase mac from file
            inputBytes = Arrays.copyOfRange(inputBytes, 0, inputBytes.length - 32);

            // Init cipher to decrypt file
            Cipher decryptCipher = Cipher.getInstance(TRANSFORMATION);
            decryptCipher.init(Cipher.DECRYPT_MODE, cryptedKeyFromFile);

            byte[] outputBytes = decryptCipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            if (privKey.getName().equals("inputKey")) {
                if (!privKey.delete()) {
                    throw new Exception("file_delete_error");
                }
            }

            inputStream.close();
            outputStream.close();
        } catch(Exception ex) {
            throw new Exception("Errorencrypting/decryptingfile"+ex.getMessage(),ex);
        }
    }

    String reEncrypt(HashMap<String, String> fileData, String receiverId) throws Exception {
        File toReEncrypt = new File(fileData.get("path"));

        if (!toReEncrypt.exists()) {
            throw new Exception("file_not_found");
        }

        FileListManager flm = new FileListManager(fileData.get("owner_id"));
        File decrypted = new File("temp/" + flm.getAlphaNumericString() + "." + fileData.get("mime_type"));
        File reEncrypted = new File("temp/" + flm.getAlphaNumericString() + "." + fileData.get("mime_type") + ".enc");

        if (!decrypted.createNewFile() || !reEncrypted.createNewFile()) {
            throw new Exception("creating_tempfiles_error");
        }

        // Vyberieme si private key usera od ktoreho berieme subor
        File privKey = flm.getUserPrivKey();
        flm.setUserId(receiverId);
        File myPubKey = flm.getUserPubKey();

        doDecrypt(toReEncrypt, decrypted, privKey);
        doCrypto(decrypted, reEncrypted, myPubKey);

        if (!decrypted.delete()) {
            throw new Exception("temp_delete_error");
        }

        return reEncrypted.getPath();
    }

    
    private static byte[] getMac(byte[] outputBytes, String key) throws GeneralSecurityException {

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec macKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        mac.init(macKey);
        mac.update(outputBytes);
        return Arrays.copyOfRange(mac.doFinal(), 0, 32);
     }

    private static boolean checkMac(byte[] inputBytes, String keyString)
            throws GeneralSecurityException {

        int n = 32;
        int inputBytesLength = inputBytes.length;
        byte[] authTag;
        byte[] inputFileAuthTag;

        inputFileAuthTag = Arrays.copyOfRange(inputBytes,inputBytesLength - n, inputBytesLength);
        authTag =  getMac(Arrays.copyOfRange(inputBytes, 0, inputBytes.length - 32 ), keyString);

        for(int i = 0; i < n; i++) {
            if (inputFileAuthTag[i] != authTag[i]) {
                // Unequal bytes
                return false;
            }
        }
        return true;
    }

    private static PublicKey getPublicKey(File pubKeyFile) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {

        byte[] keyBytes = Files.readAllBytes(pubKeyFile.toPath());

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private static PrivateKey getPrivateKey(File privKeyFile) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        byte[] keyBytes = Files.readAllBytes(privKeyFile.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
