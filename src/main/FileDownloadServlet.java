package main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FileDownloadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Dec or nah
        boolean decrypt = request.getParameter("decrypt") != null;

        //Vyparsujeme filename z urlky
        String[] parsedUrl = new String(request.getRequestURL()).split("/");

        // Posledny prvok bude nas parameter pre file
        String filename = parsedUrl[parsedUrl.length - 1].split("\\?")[0];

        if (decrypt) {
            decryptAndDownloadFile(filename, response);
        } else {
            downloadFile(filename, response);
        }
    }

    /**
     * Downloadneme zasifrofany subor
     *
     * @param filename
     * @param response
     * @throws IOException
     */
    private void downloadFile(String filename, HttpServletResponse response) throws IOException {
        File toDownload = getFileFromName(filename);

        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(toDownload);

        byte[] buffer = new byte[4096];
        int length;
        while((length = in.read(buffer)) > 0)
        {
            out.write(buffer, 0, length);
        }

        in.close();
        out.flush();
    }

    /**
     * Downloadneme desifrovany subor
     *
     * @param filename
     * @param response
     */
    private void decryptAndDownloadFile(String filename, HttpServletResponse response){
        //todo: decrypt

        //downloadFile(filename);

        //todo: delete temp
    }

    private File getFileFromName(String fileName) {
        FileListManager flm = new FileListManager();
        String path = flm.getUploads().get(fileName);
        return new File(path);
    }
}

