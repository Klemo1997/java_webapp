package main;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {

    private String userName;
    private String password;
    private byte[] salt;
    private int id;
    private static final SecureRandom RANDOM = new SecureRandom();

    public User()
    {

    }

    public void setUser(String userName, String password) throws NoSuchAlgorithmException {
        this.userName = userName;
        this.password = password;
        getNextSalt();
    }

    public void getNextSalt() throws NoSuchAlgorithmException {
        this.salt = new byte[16];
        RANDOM.getInstance("SHA1PRNG");
        RANDOM.nextBytes(this.salt);
    }

    public boolean verify() throws SQLException, ClassNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException {
        DbHandler db = new DbHandler();
        PreparedStatement query = db.connection.prepareStatement("select ID,Password,Salt from users where users.name = ? ");
        query.setString(1, this.userName);
        ResultSet rs  = query.executeQuery();

        if(rs.next()) {
            this.setId(rs.getInt(1));
            String stored = rs.getString(2);
            String storedSALT = rs.getString(3);

            if(validatePassword(this.password, stored, storedSALT)){
                return true;
            }
        }
        return false;
    }

    public String hashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iter = 1000;
        byte[] salt = this.salt;
        char[] passwd = this.password.toCharArray();

        PBEKeySpec spec = new PBEKeySpec(passwd, salt, iter, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iter + ":" + toHex(hash);
    }
    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

    private static boolean validatePassword(String originalPassword, String storedPassword, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] hash = fromHex(parts[1]);
        byte[] storedSalt = fromHex(salt);
        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), storedSalt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }
    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public boolean checkUser(String userToRegister) throws SQLException, ClassNotFoundException {
        DbHandler db = new DbHandler();
        PreparedStatement query = db.connection.prepareStatement("select Name from users where users.name = ?");
        query.setString(1, userToRegister);
        ResultSet rs=query.executeQuery();
        return rs.next();
    }

    public boolean checkPasswords(String password, String passwordToCheck){
        return password.equals(passwordToCheck);
    }

    public void registerUser() throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        DbHandler db = new DbHandler();
        String hashPasswd = hashPassword();
        PreparedStatement query = db.connection.prepareStatement("INSERT INTO users(Name,Password,Salt) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
        query.setString(1, this.userName);
        query.setString(2, hashPasswd);
        query.setString(3, toHex(this.salt));
        query.executeUpdate();

        ResultSet rs = query.getGeneratedKeys();
        if (rs.next()){
            this.setId(rs.getInt(1));
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean setDirectories () {
        return makeUserDir(DirectoryManager.getUploadRoot(this.id)) && makeUserDir(DirectoryManager.getKeysRoot(this.id));
    }

    private boolean makeUserDir(String directoryRoot) {
        File dirToMake = new File(directoryRoot);
        if (!dirToMake.exists()) {
            dirToMake.mkdirs();
        } else {
            // Directory uz existuje, nejaka picovina
            return false;
        }
        return dirToMake.exists();
    }


    public boolean isPasswordSecure() throws IOException, Exception {
        final String MIN_LENGHT="8";
        final String MAX_LENGHT="20";

        try (BufferedReader br = Files.newBufferedReader(Paths.get("xato-net-10-million-passwords.txt"), StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null;) {
                if (this.password.equals(line)){
                    throw new Exception("password_in_dictionary");
                }
            }
        }

        // Regex, ktory zisti ci password obsahuje aspon 1 velke pismeno, aspon 1 male pismeno, cislo a ma 8-40 znakov
        final String PASSWORD_SECURITY_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,40})";
        // Taktiez nesmie obsahovat medzery
        return this.password.matches(PASSWORD_SECURITY_PATTERN)
                && !this.password.contains(" ");
    }
}
