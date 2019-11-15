package main;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class PermissionHandler {

    public static String[] allCollumns = new String[]{"id_p", "to_id", "to_file_id", "granted"};

    /**
     * Spracuje poziadavku o pristup k suboru
     *
     * @param to
     * @param to_file
     * @return
     */
    public static boolean sendRequest(String to, String to_file){
        try {
            DbHandler db = new DbHandler();
            ArrayList<String> values = new ArrayList<>();
            values.add(to);
            values.add(to_file);
            // Oznacime ako nepotvrdeny permission
            values.add(String.valueOf(0));
            db.add("INSERT INTO permissions (to_id, to_file_id, granted) VALUES (?, ?, ?)", values);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean acceptRequest(String permission_id){
        try {
            DbHandler db = new DbHandler();
            ArrayList<String> values = new ArrayList<>();
            values.add(permission_id);
            db.add("UPDATE permissions SET granted = 1 WHERE id_p = ?", values);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static HashMap<String, String> getRequestStatus(String userId, String fileId) {
        try {
            DbHandler db = new DbHandler();
            ArrayList<String> values = new ArrayList<>();
            values.add(userId);
            values.add(fileId);
            return db.get(
            "SELECT * FROM permissions WHERE to_id = ? AND to_file_id = ?",
                values,
                PermissionHandler.allCollumns).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public static HashMap<String, String> getRequestStatus(String permission_id) {
        try {
            DbHandler db = new DbHandler();
            ArrayList<String> values = new ArrayList<>();
            values.add(permission_id);
            return db.get(
                    "SELECT * FROM permissions WHERE id_p = ?",
                    values,
                    PermissionHandler.allCollumns).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<HashMap<String, String>> getRequestsForFile(String fileId, boolean accepted){
        try {
            DbHandler db = new DbHandler();
            ArrayList<String> values = new ArrayList<>();
            values.add(fileId);
            values.add(String.valueOf(accepted ? 1 : 0));
            return db.get(
                    "SELECT * FROM permissions WHERE to_file_id = ? AND granted = ?",
                    values,
                    PermissionHandler.allCollumns
            );
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean canAccess(String userId, String fileId) throws SQLException, ClassNotFoundException, FileNotFoundException {
        // Nastavime filter aby hladal file podla ID
        FileFilter filter = new FileFilter(FileFilter.ALL_MY_FILES);
        filter.fileId = fileId;

        DbHandler db = new DbHandler();
        HashMap<String, String> file = db.getCompleteFileInfo(filter);

        if (file.get("owner_id").equals(userId)) {
            // Sme vlastnik suboru
            return true;
        }


        return getRequestStatus(userId, fileId) != null
                ? (getRequestStatus(userId, fileId).get("granted").equals("1") ? true : false)
                : false;
    }


}
