package main;

import java.io.File;

/**
 *  Classa na directories, nech to nieje v kazdom subore
 */
public class DirectoryManager {

//    private String KEYDIR = "/usr/local/keys";
//    private String UPLOADDIRECTORY = "/usr/local/uploads";

    private static final String PROJECT_ROOT = "C:/Users/matus/IdeaProjects/java_webapp";
    private static final String UPLOAD_ROOT = "C:/Users/matus/IdeaProjects/java_webapp/uploads";
    private static final String KEYS_ROOT = "C:/Users/matus/IdeaProjects/java_webapp/keys";

    public static String getProjectRoot(){
        return PROJECT_ROOT;
    }

    public static String getUploadRoot(Object id){
        return UPLOAD_ROOT + File.separator + id.toString() + File.separator;
    }

    public static String getKeysRoot(Object id){
        return KEYS_ROOT + File.separator + id.toString() + File.separator;
    }
}
