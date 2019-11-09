package main;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private String userName;
    private String password;
    private String salt;
    private int id;

    public User()
    {

    }

    public void setUser(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
        //to je len zatial aby som nejaky salt mala
        String generateSalt = "blabvlabla456";
        //tu sa ma generovat salt asi
        this.salt = generateSalt;
    }

    public void hashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[16];
//        random.nextBytes(salt);
//
//        KeySpec spec = new PBEKeySpec(this.password.toCharArray(), salt, 65536, 128);
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//
//        byte[] hashedPassword = factory.generateSecret(spec).getEncoded();
//        this.password = hashedPassword.toString();
    }

    public boolean verify() throws SQLException, ClassNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException {
        DbHandler db = new DbHandler();
        ResultSet rs = db.st.executeQuery("select ID from users where users.name = '" + this. userName + "' and users.password = '" + this.password + "';");
        if(rs.next()) {
            this.setId(rs.getInt(1));
            return true;
        }
        return false;
    }

    public boolean checkUser(String userToRegister) throws SQLException, ClassNotFoundException {
        DbHandler db = new DbHandler();
        ResultSet rs = db.st.executeQuery("select Name from users where users.name = '" + userToRegister +"';");
        return rs.next();
    }

    public boolean checkPasswords(String password, String passwordToCheck){
        return password.equals(passwordToCheck);
    }

    public void registerUser() throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        DbHandler db = new DbHandler();
        hashPassword();
        db.st.executeUpdate("INSERT INTO users(Name,Password,Salt) values ('" + this.userName + "','"+ this.password + "','" + this.salt + "');");
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
}
