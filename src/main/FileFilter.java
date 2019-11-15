package main;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FileFilter {
    boolean searchFileNames = true;
    boolean searchAuthorNames = false;
    public boolean allFiles = false;
    String searchQuery = null;
    boolean isDisabled = false;
    String userId = null;
    String fileId = null;
    String[]  wantedColumns;

    //Zrychlenie vytvarania filtrov, hodit do konstruktora

    // Iba moje subory
    public static final int ALL_MY_FILES = 1;
    // Vsetky subory
    public static final int ALL_FILES = 2;


    public FileFilter(boolean search_file_names, boolean search_author_names, String query, boolean allFiles){
        this.searchFileNames = search_file_names;
        this.searchAuthorNames = search_author_names;
        this.searchQuery = query;
        this.allFiles = allFiles;
        this.isDisabled = !search_file_names && !search_author_names && (query == null || query.equals(""));
    }
    // Vyfiltrujeme konkretny file
    public FileFilter (String fileId) {
        this.searchFileNames = false;
        this.searchAuthorNames = false;
        this.allFiles = true;
        this.searchQuery = null;
        this.userId = null;

        try {
            Integer.parseInt(fileId);
            this.fileId = fileId;
        } catch (Exception e) {
            // neciselny string
        }
    }

    public FileFilter(int constructForFind){
        this.searchFileNames = false;
        this.searchAuthorNames = false;
        this.searchQuery = null;
        this.isDisabled = true;

        if (constructForFind == this.ALL_MY_FILES) {
            this.allFiles = false;
        } else if (constructForFind == this.ALL_FILES) {
            this.allFiles = true;
        }
    }

    /**
     * Chceme kompletne informacie o file z databazky
     * @return
     */
    public HashMap<String, String> doCompleteInfoFiltration(String[] columns) throws SQLException, ClassNotFoundException, FileNotFoundException {
        DbHandler dbh = new DbHandler();
        this.wantedColumns = columns;
        return dbh.getCompleteFileInfo(this);
    }

    /**
     * Chceme kompletne informacie o file z databazky
     * VSETKY COLLUMNS !
     * @return
     */
    public HashMap<String, String> doCompleteInfoFiltration() throws SQLException, ClassNotFoundException, FileNotFoundException {
        DbHandler dbh = new DbHandler();
        this.wantedColumns = new String[] {"filename", "path", "mime_type", "owner_id"};
        return dbh.getCompleteFileInfo(this);
    }

    public HashMap<String, Map<String, String>> doFiltrationV2() throws SQLException, ClassNotFoundException, FileNotFoundException {
        DbHandler dbh = new DbHandler();
        this.wantedColumns = new String[]{"id_file", "filename", "path", "mime_type", "owner_id"};
        if (this.searchQuery == null || this.searchQuery.equals("")) {
            this.isDisabled = true;
        }
        if (this.allFiles) {
            //Chceme vsetky fily,
            //userId preto musime unsetnut
            this.userId = null;
        }
        return dbh.getFilteredFilesV2(this);
    }

    /**
    *  Setneme ked hladame fily podla owner_id
    * */
    public void setUserId(String id){
        this.userId = id;
    }

    public void setWantedColumns(String[] wantedColumns){
        this.wantedColumns = wantedColumns;
    }

    public void setToFindFileName(String searchQuery){
        this.isDisabled = false;
        this.searchFileNames = true;
        this.searchQuery = searchQuery;
    }

}
