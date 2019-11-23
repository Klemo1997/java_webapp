package main;

import java.io.File;

/**
 *  Classa na directories, nech to nieje v kazdom subore
 */
public class DirectoryManager {
    private static final String UPLOAD_ROOT = "uploads";
    private static final String KEYS_ROOT = "keys";

    static String getUploadRoot(Object id){
        return UPLOAD_ROOT + File.separator + id.toString() + File.separator;
    }

    static String getKeysRoot(Object id){
        return KEYS_ROOT + File.separator + id.toString() + File.separator;
    }

}
