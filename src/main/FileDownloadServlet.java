package main;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;

public class FileDownloadServlet extends HttpServlet {

    private final String KEYDIR = "/usr/local/keys";
    private final String UPLOADDIRECTORY = "/usr/local/uploads";

    public FileDownloadServlet() throws MalformedURLException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

         File temp = null;
         boolean checkBoxVal = false;

        if(ServletFileUpload.isMultipartContent(req)) {

            List<FileItem> multiparts = null;
            try {
                multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);


                for(FileItem item : multiparts)
                {
                    if (item.getFieldName().equals("keyFile")) {
                        String name = new File(item.getName()).getName();
                        temp = new File(KEYDIR + File.separator + "tempKey");
                        item.write(temp);
                    } else {
                        if(item.isFormField()) {
                            checkBoxVal = item.getString().equals("on");
                        }

                    }
                }

                if (!checkBoxVal) {
                    doGet(req, resp);
                }

                //Vyparsujeme filename z urlky
                String[] parsedUrl = new String(req.getRequestURL()).split("/");

                // Posledny prvok bude nas parameter pre file
                String filename = parsedUrl[parsedUrl.length - 1].split("\\?")[0];



                decryptAndDownloadFile(filename, temp, resp);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }

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

        } else {
            downloadFile(filename, null, response);
        }
    }

    /**
     * Downloadneme zasifrofany subor
     *
     * @param filename
     * @param response
     * @throws IOException
     */
    private void downloadFile(String filename, String desiredName , HttpServletResponse response) throws IOException {

        File toDownload = null;

        toDownload = new File(UPLOADDIRECTORY + File.separator + filename);
        if (!toDownload.exists()) {
            toDownload = new File(KEYDIR + File.separator + filename);
        }

        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(toDownload);

        byte[] buffer = new byte[4096];
        int length;
        while((length = in.read(buffer)) > 0)
        {
            out.write(buffer, 0, length);
        }



        desiredName = desiredName == null
                ? filename
                : desiredName;

        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment; filename=" +  desiredName);


        in.close();
        out.flush();
        out.close();
    }

    /**
     * Downloadneme desifrovany subor
     *
     * @param filename
     * @param response
     */
    private void decryptAndDownloadFile(String filename, File tempKey, HttpServletResponse response) throws Exception {
        //todo: decrypt
        File toDownload = new File(filename);
        File tempDownloadFile =  new File(KEYDIR + File.separator + "tempFile");

        CryptoUtils cryptoUtils = new CryptoUtils();

        cryptoUtils.decrypt(tempKey, toDownload, tempDownloadFile);
        downloadFile(tempDownloadFile.getName(), toDownload.getName().replace(".enc", "") , response);

        //todo: delete temp
    }

    private File getFileFromName(String fileName) throws MalformedURLException {
        FileListManager flm = new FileListManager();
        String path = flm.getUploads().get(fileName) == null
            ? flm.getKeys().get(fileName)
            : flm.getUploads().get(fileName);

        return new File(path);
    }
}

