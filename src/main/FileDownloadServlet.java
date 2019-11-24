package main;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FileDownloadServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String userId;
        if (session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        else {
            userId = session.getAttribute("userId").toString();
        }
        Map<String, String> params = parseUrlParams(request.getRequestURL().toString());

        //download/recrypt/3
        assert params != null;
        if (params.get("param").equals("recrypt")) {
            String fileId = params.get("param1");
            FileFilter  filter = new FileFilter(fileId);
            FileListManager flm = new FileListManager(userId);
            HashMap<String, String> fileData = null;

            try {
                fileData = flm.getCompleteInfo(filter).get(fileId);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {

                CryptoUtils crUtils = new CryptoUtils();
                assert fileData != null;
                File reencrypted = new File(crUtils.reEncrypt(fileData, userId));

                byte[] buffer = new byte[4096];
                int length;
                OutputStream out = response.getOutputStream();
                FileInputStream in = new FileInputStream(reencrypted);

                response.setContentType("application/x-msdownload");
                response.setHeader("Content-Disposition", "attachment; filename=" +  fileData.get("filename"));

                while((length = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, length);
                }
                reencrypted.delete();
                request.setAttribute("message","File Reencrypted Successfully");
                out.flush();
            } catch (Exception e) {
                if (e.getMessage().equals("deprecated_private_key")) {
                    // User ma neaktualny privateKey, ulozime to do tabulky, informujeme ho o tom vo flashi, subor si moze stiahnut znova priamo
                    try {
                        DbHandler db = new DbHandler();
                        db.setDeprecatedKey(fileId);
                    } catch (Exception ex) {
                        response.sendRedirect("/java_webapp_war/view.jsp?id=" + fileId + "&error=true");
                    }

                    response.sendRedirect("/java_webapp_war/view.jsp?id=" + fileId + "&error=deprecatedprivatekey");
                } else {
                    response.sendRedirect("/java_webapp_war/view.jsp?id=" + fileId + "&error=true");
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Map<String, String> params = parseUrlParams(request.getRequestURL().toString());

        try {
            assert params != null;
            Integer.parseInt(params.get("param"));
            // Nastavime filter aby hladal file podla ID
            FileFilter filter = new FileFilter(FileFilter.ALL_MY_FILES);
            filter.fileId = params.get("param");
            DbHandler db = new DbHandler();

            HashMap<String, String> fileToDownload = db.getCompleteFileInfo(filter);

            // Pozrieme ci nan mame permissions
            if (!PermissionHandler.canAccess(session.getAttribute("userId").toString(), params.get("param"))) {
                throw new Exception("unauthorized");
            }

            downloadFileV2(fileToDownload.get("path"), response);
            return;

        } catch (Exception e) {
            if (e.getMessage().equals("unauthorized")) {
                // Sahame na subor na ktory nemame prava
                response.sendRedirect("/java_webapp_war/files.jsp?error=unauthorized");
                return;
            }
            response.sendRedirect("/java_webapp_war/files.jsp?error=true");
        }
        // Pokracujeme len ak sme dostali exception inu ako unauthorized, pravdepodobne
        // volame cez staru metodu downloads + nazov

        //Vyparsujeme filename z urlky
        String[] parsedUrl = new String(request.getRequestURL()).split("/");

        // Posledny prvok bude nas parameter pre file
        String filename = parsedUrl[parsedUrl.length - 1].split("\\?")[0];

        downloadFile(filename, response, (String) session.getAttribute("userId"));
    }

    /**
     * Downloadneme zasifrovany subor
     * @param filename
     * @param response
     * @param userId
     * @throws IOException
     */
    private void downloadFile(String filename, HttpServletResponse response, String userId) throws IOException {
        File toDownload;

        toDownload = new File(DirectoryManager.getUploadRoot(userId) + filename);
        if (!toDownload.exists()) {
            toDownload = new File(DirectoryManager.getKeysRoot(userId) + filename);
        }

        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(toDownload);

        byte[] buffer = new byte[4096];
        int length;
        while((length = in.read(buffer)) > 0)
        {
            out.write(buffer, 0, length);
        }

        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        in.close();
        out.flush();
        out.close();
    }

    /**
     * Downloadneme file podla ID
     *
     * @param filePath
     * @param response
     * @throws Exception
     */
    private void downloadFileV2(String filePath, HttpServletResponse response) throws Exception {
        File toDownload = new File(filePath);

        if (!toDownload.exists()) {
            throw new Exception("not_found");
        }

        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(toDownload);

        byte[] buffer = new byte[4096];
        int length;
        while((length = in.read(buffer)) > 0)
        {
            out.write(buffer, 0, length);
        }
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment; filename=" +  toDownload.getName());

        in.close();
        out.flush();
        out.close();
    }

    private HashMap<String, String> parseUrlParams(String url) {
        String[] splitUrl = url.split("/");
        boolean startParams = false;
        int addedParams = 0;


        HashMap<String, String> params = new HashMap<>();
        for (String s : splitUrl) {

            if (startParams && addedParams >= 0) {
                // dalej zapisujeme dalsie parametre ktore pridu v url
                params.put("param" + (addedParams == 0 ? "" : String.valueOf(addedParams)), s);
                addedParams++;
            }

            if (s.equals("download")) {
                params.put("method", "download");
                startParams = true;
            }
        }
        if (params.get("method") == null || params.get("param") == null) {
            return null;
        }
        return params;
    }
}

