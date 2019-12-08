package main;

import com.mysql.jdbc.exceptions.MySQLDataException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DbHandler {

    Connection connection;

    DbHandler() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubp_db","root","JumpUpAndDown");
    }

    void AddFile(File file, String uploadedBy) throws IOException, SQLException {
        if (!file.exists() || this.connection.isClosed()){
            return;
        }
        String name = file.getName().length() < FileFilter.FILENAME_LENGTH_LIMIT
                ? file.getName()
                : file.getName().substring(0, FileFilter.FILENAME_LENGTH_LIMIT);

        String[] name_split = name.split("\\.");
        String path = file.getPath().replace('\\', '/');
        String mimeType = Files.probeContentType(file.toPath()) != null && Files.probeContentType(file.toPath()).length() < 8
                ? Files.probeContentType(file.toPath())
                : name_split[name_split.length - 2];
        // Ak mame setnuty tento mimetype, crashovalo
        if (mimeType.equals("text/plain")) {
            mimeType = "txt";
        }

        PreparedStatement query = connection.prepareStatement("INSERT INTO files(filename, path, mime_type, owner_id) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        query.setString(1, name);
        query.setString(2, path);
        query.setString(3, mimeType);
        query.setInt(4, Integer.parseInt(uploadedBy));
        query.executeUpdate();
    }

    private String[] getUsersLike(String searchQuery) throws SQLException {
        PreparedStatement query = connection.prepareStatement("SELECT ID FROM users WHERE Name LIKE ?", Statement.RETURN_GENERATED_KEYS);
        query.setString(1, "%" + searchQuery + "%");
        ResultSet resultSet = query.executeQuery();
        int index = 1;
        int rowCount = getRowCount(resultSet);
        String[] users = new String[rowCount];
        while (resultSet.next()) {
            users[index - 1] = resultSet.getString("ID");
            index++;
        }
        return users;
    }

    HashMap<String, String> getCompleteFileInfo(FileFilter filter) throws SQLException, FileNotFoundException {

        HashMap<String, String> fileInfo = new HashMap<>();

        if (filter.fileId == null) {
            return fileInfo;
        }

        PreparedStatement query = connection.prepareStatement("SELECT * FROM files WHERE id_file = ?", Statement.RETURN_GENERATED_KEYS);
        query.setString(1, filter.fileId);

        File MySqlExceptions = new File("SQLerr.log");
        PrintStream ps = new PrintStream(MySqlExceptions);

        try {
            ResultSet resultSet = query.executeQuery();
            String[] wantedColumns = filter.wantedColumns != null
                    ? filter.wantedColumns
                    : new String[]{"id_file", "filename", "path", "mime_type", "key_deprecated", "owner_id"};

            while (resultSet.next()) {

                for (String column : wantedColumns) {
                    if (resultSet.getString(column) == null) {
                        //data corrupted
                        fileInfo = null;
                        throw new Exception("Invalid data for file");
                    }
                    fileInfo.put(column, resultSet.getString(column));
                }
            }

        } catch (MySQLSyntaxErrorException sqlExc) {
            // Nastane pri pokusoch o injekcie, alebo nebodaj XSS vo vyhladavacom vstupe, proste blbosti v inpute
            // na vyhladavanie fileov

            //Zapiseme do logov
            sqlExc.printStackTrace(ps);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ps.close();
        return fileInfo;
    }

    HashMap<String, Map<String, String>> getFilteredFilesV2(FileFilter filter) throws SQLException, FileNotFoundException {
        StringBuilder author_query = new StringBuilder();
        String name_query = "";
        String[] owners = null;


        if (!filter.isDisabled) {
            if (filter.searchAuthorNames) {
                author_query = new StringBuilder(" owner_id IN (");
                owners = getUsersLike(filter.searchQuery);
                for (int i = 0; i < owners.length; i++) {
                    if (i != owners.length - 1) {
                        author_query.append(" ?,");
                    } else {
                        author_query.append(" ? )");
                    }
                }
            }
            if (filter.searchFileNames) {
                name_query = " filename LIKE ? ";
            }
        }
        // Hladame konkretneho usera (mozme mu ale filtrovat fily podla mena)
        if (filter.userId != null) {
            author_query = new StringBuilder(" owner_id = ? ");
        }
        // Hladame konkretny file
        if (filter.fileId != null) {
            name_query = " id_file = ? ";
            author_query = new StringBuilder();
        }

        // Oddelime podmienky
        String condition_separator = !author_query.toString().equals("") && !name_query.equals("")
            ? filter.allFiles ? " OR " : " AND "
            : "";

        String queryString = filter.allFiles && filter.isDisabled
            ? "SELECT * FROM files"
            : "SELECT * FROM files WHERE " + author_query + condition_separator + name_query;

        PreparedStatement query = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
        int parameterIndex = 1;
        if (filter.searchAuthorNames && filter.userId == null) {
            assert owners != null;
            for (String owner : owners) {
                query.setString(parameterIndex, owner);
                parameterIndex++;
            }
        }

        if (filter.userId != null && !filter.allFiles) {
            query.setString(parameterIndex, filter.userId);
            parameterIndex++;
        }

        if (filter.searchFileNames && !filter.isDisabled) {
            query.setString(parameterIndex, "%" + filter.searchQuery + "%");
        }

        if (filter.fileId != null) {
            query.setString(parameterIndex, filter.fileId);
        }

        HashMap<String, Map<String, String>> fileMap = new HashMap<>();

        File MySqlExceptions = new File("SQLerr.log");
        PrintStream ps = new PrintStream(MySqlExceptions);

        try {
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                fileMap.put(
                        resultSet.getString("id_file"), //key
                        getFileDataFromResultSet(resultSet, filter.wantedColumns) //value
                );
            }

        } catch (MySQLSyntaxErrorException sqlExc) {
            // Nastane (mozno) pri pokusoch o injekcie, alebo nebodaj XSS vo vyhladavacom vstupe, proste blbosti v inpute
            // na vyhladavanie fileov

            //Zapiseme do logov
            sqlExc.printStackTrace(ps);
        }
        ps.close();
        return fileMap;
    }

    private HashMap<String, String> getFileDataFromResultSet(ResultSet rs, String[] wantedColumns) throws SQLException {
        HashMap<String, String> retMap = new HashMap<>();
        for (String column : wantedColumns) {
            if (rs.getString(column) == null) {
                return null;
            }
            retMap.put(column, rs.getString(column));
        }
        return retMap;
    }

    private int getRowCount(ResultSet resultSet) {
        if (resultSet == null) {
            return 0;
        }
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
        return 0;
    }

    void add(String queryString, ArrayList<String> values) throws SQLException {
        getQuery(queryString,values).executeUpdate();
    }

    public ArrayList<HashMap<String, String>> get(String queryString, ArrayList<String> values, String[] columns)
            throws Exception {

        ResultSet rs = getQuery(queryString, values).executeQuery();
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

    private void update(String queryString, ArrayList<String> values)
            throws Exception {
        getQuery(queryString, values).executeUpdate();
    }

    private PreparedStatement getQuery(String queryString, ArrayList<String> values) throws SQLException {
        PreparedStatement query = this.connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
        int paramCount = 1;
        for (String val : values) {
            query.setString(paramCount, val);
            paramCount++;
        }
        return query;
    }

    void setDeprecatedKey(String fileId) throws Exception {
        ArrayList<String> values = new ArrayList<>();
        values.add("1");
        values.add(fileId);
        this.update("UPDATE files SET key_deprecated = ? WHERE id_file = ?", values);
    }

    void deleteFile(String fileId) throws Exception {
        //Pouzijeme na delete funkciu update, zbytocne duplikovat nemusime
        ArrayList<String> fileIdVal = new ArrayList<>();
        fileIdVal.add(fileId);
        // Vymazeme z permissions
        this.update("DELETE FROM permissions WHERE to_file_id = ?", fileIdVal);
        // Vymazeme komentare
        this.update("DELETE FROM comments WHERE file_id = ?", fileIdVal);
        // Vymazeme file
        this.update("DELETE FROM files WHERE id_file = ?", fileIdVal);
    }
}
