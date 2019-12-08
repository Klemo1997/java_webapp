package main;

import com.mysql.jdbc.exceptions.MySQLDataException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class CommentsHandler {

    public static final int COMMENT_LENGTH_LIMIT = 256;

    private static void add(String queryString, ArrayList<String> values) throws SQLException, ClassNotFoundException {
        DbHandler db = new DbHandler();

        PreparedStatement query = db.connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
        int paramCount = 1;
        for (String val : values) {
            query.setString(paramCount, val);
            paramCount++;
        }
        query.executeUpdate();
    }

    private static ArrayList<HashMap<String, String>> get(String queryString, ArrayList<String> values, String[] columns)
            throws Exception {
        DbHandler db = new DbHandler();

        PreparedStatement query = db.connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
        int paramCount = 1;
        for (String val : values) {
            query.setString(paramCount, val);
            paramCount++;
        }
        ResultSet rs = query.executeQuery();
        ArrayList<HashMap<String, String>> records = new ArrayList<>();

        while (rs.next()) {
            HashMap<String, String> record = new HashMap<>();

            for (String col : columns) {
                if (rs.getString(col) == null) {
                    throw new MySQLDataException("Corrupted data");
                }
                record.put(col, rs.getString(col));
            }
            records.add(record);
        }
        return records;
    }

    static boolean addComment(String commentBody, String userId, String fileId){
        // Komentar je dlhsi ako 256 znakov
        if (commentBody.length() > COMMENT_LENGTH_LIMIT) {
            return false;
        }

        ArrayList<String> values = new ArrayList<>();
        values.add(commentBody);
        values.add(userId);
        values.add(fileId);
        try {
            add("INSERT INTO comments (body,author_id,file_id) VALUES (?, ?, ?)", values);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static ArrayList<HashMap<String, String>> getCommentsByFile(String fileId){

        ArrayList<String> param = new ArrayList<>();
        param.add(fileId);
        try {
            return get("SELECT * FROM comments WHERE file_id = ?", param, new String[]{"id_c", "body", "author_id", "file_id"});
        } catch (Exception e) {
            return null;
        }
    }
}
