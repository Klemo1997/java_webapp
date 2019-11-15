package main;

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

    public  HashMap<String, Map<String, String>> getUploads(FileFilter filter) throws SQLException, ClassNotFoundException, FileNotFoundException {
        if (filter == null) {
            filter = new FileFilter(false, false, null, false);
        }
        //Ak vyberame vsetky fily, unsetneme ho vo filtri
        filter.setUserId(userId);
        return filter.doFiltrationV2();
    }

    public  Map<String, String> getKeys(){
        Map<String, String> fileMap = new HashMap<String, String>();

        List<String> uploads = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(Paths.get(DirectoryManager.getKeysRoot(this.userId)))) {

            uploads = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());


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

    public HashMap<String, HashMap<String, String>> getCompleteInfo(FileFilter filter) throws FileNotFoundException, SQLException, ClassNotFoundException {

        HashMap<String, String> fileInfo = new HashMap<>();
        fileInfo = filter.doCompleteInfoFiltration();

        HashMap<String, HashMap<String, String>> fileMap = new HashMap<>();
        fileMap.put(filter.fileId, fileInfo);

        return fileMap;
    }
}
