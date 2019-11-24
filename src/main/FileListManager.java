package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileListManager {
    private String userId;

    public FileListManager(String userId){
        this.userId = userId;
    }

    /**
     * Get uploads by filter
     *
     * @param filter
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     */
    public  HashMap<String, Map<String, String>> getUploads(FileFilter filter)  {
        if (filter == null) {
            filter = new FileFilter(false, false, null, false);
        }
        //Ak vyberame vsetky fily, unsetneme ho vo filtri
        filter.setUserId(userId);
        try {
            return filter.doFiltrationV2();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get user key pairs
     *
     * @return
     */
    public  Map<String, String> getKeys(){
        Map<String, String> fileMap = new HashMap<>();

        List<String> uploads = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(Paths.get(DirectoryManager.getKeysRoot(this.userId)))) {

            uploads = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());


        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String item : uploads) {
            String[] pathSplit = item.replace('\\','/').split("/");
            String itemName = pathSplit[pathSplit.length - 1];
            if (!itemName.contains("tempKey")){
                fileMap.put(itemName, item.replace("\\", "/"));
            }
        }
        return fileMap;
    }

    /**
     * Ziskame kompletne info o subore
     * t.j. vsetky columny v tabulke
     * vracia HashMap<fileId, HashMap<fileDetails>>
     *
     * @param filter - moze urcovat aj ake parametre chceme
     * @return
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public HashMap<String, HashMap<String, String>> getCompleteInfo(FileFilter filter) throws FileNotFoundException, SQLException, ClassNotFoundException {

        HashMap<String, String> fileInfo;
        fileInfo = filter.doCompleteInfoFiltration();

        HashMap<String, HashMap<String, String>> fileMap = new HashMap<>();
        fileMap.put(filter.fileId, fileInfo);

        return fileMap;
    }

    /**
     * Funkcia vytiahne privKey setnuteho usera
     *
     * @return User private key file
     * @throws Exception
     */
    File getUserPrivKey() throws Exception {
        if (this.userId == null) {
            throw new Exception("user_not_set");
        }
        File userPrivkey = new File(DirectoryManager.getKeysRoot(this.userId) + "privKey");
        if (!userPrivkey.exists()) {
            throw new Exception("private_key_not_found");
        }
        return userPrivkey;
    }

    /**
     * Funkcia vytiahne pubKey setnuteho usera
     *
     * @return User public key file
     * @throws Exception
     */
    File getUserPubKey() throws Exception {
        if (this.userId == null) {
            throw new Exception("user_not_set");
        }
        File userPubKey = new File(DirectoryManager.getKeysRoot(this.userId) + "pubKey");
        if (!userPubKey.exists()) {
            throw new Exception("public_key_not_found");
        }
        return userPubKey;
    }

    /**
     * Funkcia vrati retazec s 10
     * nahodnymi znakmi, pouzivame
     * pri nazvoch temp fileov
     *
     * @return randomstring
     */
    String getAlphaNumericString()
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    /**
     * Set userId
     *
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Tu kontrolujeme ci uz subor s takym nazvom neexistuje, ak ano
     * pridame mu k nazvu koncovku "-new", napr test.txt -> test-new.txt
     *
     * Funguje aj "rekurzivne", ak uz existuje aj test-new-new.txt, najde nazov
     * text-new-new-new.txt
     */
    static String sanitizeFileName(String name, String userid) throws SQLException, ClassNotFoundException, FileNotFoundException {
        FileListManager flm = new FileListManager(userid);
        FileFilter filter = new FileFilter(FileFilter.ALL_MY_FILES);
        filter.setToFindFileName(name);

        // Extraktneme si nazvy fileov
        ArrayList<String> fileNames = new ArrayList<>();
        for (Map<String, String> file : flm.getUploads(filter).values()) {
            fileNames.add(file.get("filename"));
        }
        // Hladame fily s rovnakym menom
        while (fileNames.contains(name)) {
            String[] split = name.split("\\.");
            split[0] = split[0] + "-new";
            name = String.join(".", split);
        }
        return name;
    }
}
