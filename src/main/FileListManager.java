package main;

import java.io.File;
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

    public  Map<String, String> getUploads(FileFilter filter) throws SQLException, ClassNotFoundException {
        if (filter == null) {
            filter = new FileFilter(false, false, null, false);
        }

        Map<String, String> fileMap = new HashMap<String, String>();

        List<String> uploads = new ArrayList<>();

        if (filter.isDisabled) {
            try (Stream<Path> walk = Files.walk(Paths.get(DirectoryManager.getUploadRoot(this.userId)))) {

                uploads = walk.filter(Files::isRegularFile)
                        .map(x -> x.toString()).collect(Collectors.toList());


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
           filter.setUserId(this.userId);
           uploads = filter.doFiltration();
        }

        for (String item : uploads) {
            String itemName = item.substring(item.lastIndexOf("\\") + 1);
            if (itemName.contains(".enc")) {
                fileMap.put(itemName, item.replace("\\", "/"));
            } else {
                File file = new File(itemName);
                file.delete();
            }

        }
        return fileMap;
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
            String itemName = item.substring(item.lastIndexOf("\\") + 1);
            if (!itemName.contains("tempKey")){
                fileMap.put(itemName, item.replace("\\", "/"));
            }
        }
        return fileMap;
    }

    public  Map<String, String> getAllUploads(FileFilter filter) throws SQLException, ClassNotFoundException {


        Map<String, String> fileMap = new HashMap<String, String>();

        List<String> uploads = new ArrayList<>();

        if (filter.isDisabled) {
            try (Stream<Path> walk = Files.walk(Paths.get(DirectoryManager.getAllUploadsRoot()))) {

                uploads = walk.filter(Files::isRegularFile)
                        .map(x -> x.toString()).collect(Collectors.toList());


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            uploads = filter.doFiltration();
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


}
