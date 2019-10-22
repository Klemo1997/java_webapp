package main;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CryptoUtils
{
    private static final String ALGORITHM="AES";
    private static final String TRANSFORMATION="AES";

    public static void encrypt(String key, File inputFile, File outputFile) throws Exception
    {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decrypt(String key, File inputFile, File outputFile) throws Exception
    {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws Exception
    {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int)inputFile.length()];

            // Output stream
            inputStream.read(inputBytes);

            if (cipherMode == Cipher.DECRYPT_MODE && !checkMac(inputFile, inputBytes, key)) {
                // Throw manipulated file exception
                throw new IOException("File manipulated");
            }

            if (cipherMode == Cipher.DECRYPT_MODE) {
                inputBytes = Arrays.copyOfRange(inputBytes, 0, inputBytes.length - 32);
            }
            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
//            outputStream.write(outputBytes);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(outputBytes);
            if (cipherMode == Cipher.ENCRYPT_MODE) {
                output.write(getMac(outputStream, outputBytes ,key));
            }

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

    public static byte[] getMac(FileOutputStream encrypted, byte[] outputBytes ,String key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        //Append last 16 bytes of mac
        byte[] authTag = new byte[32];

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec macKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        mac.init(macKey);
        mac.update(outputBytes);
        return Arrays.copyOfRange(mac.doFinal(), 0, 32);
     }

    public static boolean checkMac(File encrypted, byte[] inputBytes, String key) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        int n = 32;
        int inputBytesLength = inputBytes.length;
        byte[] authTag = new byte[n];
        byte[] inputFileAuthTag = new byte[n];
        inputFileAuthTag = Arrays.copyOfRange(inputBytes,inputBytes.length - n, inputBytes.length);
        inputBytes = Arrays.copyOfRange(inputBytes, 0, inputBytesLength - n);

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec macKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");

        mac.init(macKey);
        mac.update(inputBytes);
        authTag =  Arrays.copyOfRange(mac.doFinal(), 0, 32);

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

}
