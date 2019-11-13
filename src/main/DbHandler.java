package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;

public class DbHandler {

    public Connection connection;

    public DbHandler() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubp_db","root","");//JumpUpAndDown
    }

    public boolean AddFile (File file, String uploadedBy) throws IOException, SQLException {
        if (!file.exists() || this.connection.isClosed()){
            return false;
        }
        String name = file.getName();
        String[] name_split = name.split("\\.");
        String path = file.getPath().replace('\\', '/');
        String mimeType = Files.probeContentType(file.toPath()) != null
                ? Files.probeContentType(file.toPath())
                : name_split[name_split.length - 2];
        String user_id = uploadedBy;

        // todo: pridaj do databazy
        PreparedStatement query = connection.prepareStatement("INSERT INTO files(filename, path, mime_type, owner_id) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        query.setString(1, name);
        query.setString(2, path);
        query.setString(3, mimeType);
        query.setInt(4, Integer.valueOf(user_id));
        query.executeUpdate();
        return true;
    }

    public ArrayList<String> getFilteredFiles(FileFilter filter) throws SQLException {
        String author_query = "";
        String name_query = "";
        String[] owners = null;


        if (filter.searchAuthorNames) {
            author_query = " owner_id IN (";
            owners = getUsersLike(filter.searchQuery);
            for (int i = 0; i < owners.length; i++) {
                if (i != owners.length - 1) {
                    author_query += " ?,";
                } else {
                    author_query += " ? )";
                }
            }

        }
        if (filter.searchFileNames) {
            name_query = " filename LIKE ? ";
        }
        if (filter.userId != null) {
            author_query = " owner_id = ? ";
        }
        // Oddelime podmienky ANDom
        String condition_separator = !author_query.equals("") && !name_query.equals("") ? " OR " : "";

        PreparedStatement query = connection.prepareStatement("SELECT path FROM files WHERE " + author_query + condition_separator + name_query, Statement.RETURN_GENERATED_KEYS);
        int parameterIndex = 1;
        if (filter.searchAuthorNames && filter.userId == null) {
            for (String owner : owners) {
                query.setString(parameterIndex, owner);
                parameterIndex++;
            }
        }

        if (filter.userId != null) {
            query.setString(parameterIndex, filter.userId);
            parameterIndex++;
        }

        if (filter.searchFileNames) {
            query.setString(parameterIndex, "%" + filter.searchQuery + "%");
        }

        ResultSet resultSet = query.executeQuery();


        ArrayList<String> results = new ArrayList<>();

        while (resultSet.next()) {
            results.add(resultSet.getString("path"));
        }
        return results;
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

}
