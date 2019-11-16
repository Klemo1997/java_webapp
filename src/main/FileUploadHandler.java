package main;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUploadHandler extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession session = request.getSession();

        // process only if its multipart content
        if(ServletFileUpload.isMultipartContent(request)) {
            try {
                List <FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

                File
                temp = null,
                encrypted = null,
                keyFile = null;

                String
                key = null,
                mode = null;

                for(FileItem item : multiparts)
                {
                    if(!item.isFormField())
                    {
                        String name = new File(item.getName()).getName();
                        if (item.getFieldName().equals("keyFile")) {
                            keyFile = new File(DirectoryManager.getUploadRoot(session.getAttribute("userId")) + "inputKey");
                            item.write(keyFile);
                        } else {
                            // Osetrime aby sa subor s rovnakym nazvom neprepisal, subor.txt sa nahradi suborom subor-new.txt
                            name = sanitizeFileName(
                                    multiparts.get(2).getString().equals("enc"),
                                    name,
                                    session.getAttribute("userId").toString()
                            );

                            temp = new File(DirectoryManager.getUploadRoot(session.getAttribute("userId")) + name);
                            item.write(temp);
                            String encryptedAppend = multiparts.get(2).getString().equals("enc") ? ".enc" : "";
                            encrypted = new File(DirectoryManager.getUploadRoot(session.getAttribute("userId")) + name +  encryptedAppend);
                        }
                    }
                    else
                    {
                        switch (item.getFieldName()) {
                            case "key" :
                                key = item.getString();
                                break;
                            case "cryption-type" :
                                mode = item.getString();
                                break;
                            default :

                        }
                    }
                }
                //File uploaded successfully
                if (mode.equals("enc")) {
                    CryptoUtils.encrypt(keyFile,temp,encrypted);
                } else if (mode.equals("dec")) {
                    CryptoUtils.decrypt(keyFile,temp,encrypted);
                }

                String fileName = mode.equals("enc")
                        ? encrypted.getName()
                        : encrypted.getName().replace(".enc", "");

                // Pridame file do databazy
                DbHandler db = new DbHandler();
                db.AddFile(encrypted, session.getAttribute("userId").toString());

                OutputStream out = response.getOutputStream();
                response.setContentType("application/x-msdownload");
                response.setHeader("Content-Disposition", "attachment; filename=" +  fileName);

                FileInputStream in = new FileInputStream(encrypted);

                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, length);
                }
                // Neviem ci to s tymto funguje tak to pushnem aspon zakomentovane :D
                if (mode.equals("enc") && !temp.delete()) {
                    throw new Exception("Temp file not deleted properly");
                }
                request.setAttribute("message","File Uploaded Successfully");
                out.flush();
            }
            catch(Exception ex)
            {
                request.setAttribute("message", "File Enc/Dec Failed due to" + ex);

                File file = new File("test.log");
                PrintStream ps = new PrintStream(file);
                ex.printStackTrace(ps);
                if (ex.getMessage().equals("Temp file not deleted properly")) {
                    response.sendRedirect("index.jsp" + "?error=2" );
                } else {
                    response.sendRedirect("index.jsp" + "?error=1" );
                }
            }
        }
        else
        {
            request.setAttribute("message", "Sorry this Servlet only handles file upload request");
        }

    }

    /**
     * Tu kontrolujeme ci uz subor s takym nazvom neexistuje, ak ano
     * pridame mu k nazvu koncovku "-new", napr test.txt -> test-new.txt
     *
     * Funguje aj "rekurzivne", ak uz existuje aj test-new-new.txt, najde nazov
     * text-new-new-new.txt
     */
    protected String sanitizeFileName(boolean isEnc, String name, String userid) throws SQLException, ClassNotFoundException, FileNotFoundException {
        FileListManager flm = new FileListManager(userid);
        FileFilter filter = new FileFilter(FileFilter.ALL_MY_FILES);
        filter.setToFindFileName(name);

        // Extraktneme si nazvy fileov
        ArrayList<String> fileNames = new ArrayList<>();
        for (Map<String, String> file : flm.getUploads(filter).values()) {
            fileNames.add(file.get("filename"));
        }
        // Hladame fily s rovnakym menom
        while (fileNames.contains(name)) {
                String[] split = name.split("\\.");
                split[0] = split[0] + "-new";
                name = String.join(".", split);
        }
        return name;
    }
}
