package main;

import java.io.File;

/**
 *  Classa na directories, nech to nieje v kazdom subore
 */
public class DirectoryManager {



    private static final String UPLOAD_ROOT = "uploads";
    private static final String KEYS_ROOT = "keys";


    public static String getUploadRoot(Object id){
        return UPLOAD_ROOT + File.separator + id.toString() + File.separator;
    }

    public static String getKeysRoot(Object id){
        return KEYS_ROOT + File.separator + id.toString() + File.separator;
    }

    public static String getAllUploadsRoot() {
        return UPLOAD_ROOT + File.separator;
    }
}
