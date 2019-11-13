package main;

import java.sql.SQLException;
import java.util.ArrayList;

public class FileFilter {
    boolean searchFileNames = true;
    boolean searchAuthorNames = false;
    public boolean allFiles = false;
    String searchQuery = null;
    boolean isDisabled = false;
    String userId = null;

    public FileFilter(boolean search_file_names, boolean search_author_names, String query, boolean allFiles){
        this.searchFileNames = search_file_names;
        this.searchAuthorNames = search_author_names;
        this.searchQuery = query;
        this.allFiles = allFiles;

        this.isDisabled = !search_file_names && !search_author_names && (query == null || query.equals(""));
    }

    public ArrayList<String> doFiltration() throws SQLException, ClassNotFoundException {
        DbHandler dbh = new DbHandler();
        return dbh.getFilteredFiles(this);
    }

    public void setUserId(String id){
        this.userId = id;
    }


}
