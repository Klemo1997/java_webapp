package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileListManager {
    private final static String UPLOADDIRECTORY = "C:/Users/matus/IdeaProjects/java_webapp/uploads";

    public  Map<String, String> getUploads(){
        Map<String, String> fileMap = new HashMap<String, String>();

        List<String> uploads = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(Paths.get(UPLOADDIRECTORY))) {

            uploads = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());


        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String item : uploads) {
            String itemName = item.substring(item.lastIndexOf("\\") + 1);
            fileMap.put(itemName, item.replace("\\", "/"));
        }
        return fileMap;
    }


}
