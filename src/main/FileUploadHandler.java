package main;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

public class FileUploadHandler extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    public static String setMetaString = null;
    public static String privateKeyToSee = null;

    public FileUploadHandler() throws MalformedURLException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
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
                        if (item.getFieldName().equals("keyFile")) {
                            String name = new File(item.getName()).getName();
                            keyFile = new File(DirectoryManager.getUploadRoot(session.getAttribute("userId")) + "inputKey");
                            item.write(keyFile);
                        } else {
                            String name = new File(item.getName()).getName();
                            // Osetrime aby sa subor s rovnakym nazvom neprepisal, subor.txt sa nahradi suborom subor-new.txt
                            name = fileNameIfExists(multiparts.get(2).getString().equals("enc"), name, session.getAttribute("userId").toString());

                            temp = new File(DirectoryManager.getUploadRoot(session.getAttribute("userId")) + name);
                            item.write(temp);
                            String encryptedAppend = multiparts.get(2).getString().equals("enc") ? "enc" : "";
                            encrypted = new File(DirectoryManager.getUploadRoot(session.getAttribute("userId")) + name +  encryptedAppend);
                            if (encrypted.exists()) {

                            }
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
                if (ex.getMessage().equals("Temp file not deleted properly")) {
                    response.sendRedirect(request.getContextPath() + "?error=2" );
                } else {
                    response.sendRedirect(request.getContextPath() + "?error=1" );
                }
            }
        }
        else
        {
            request.setAttribute("message", "Sorry this Servlet only handles file upload request");
        }

    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    }
    /**
     * Tu kontrolujeme ci uz subor s takym nazvom neexistuje, ak ano
     * pridame mu k nazvu koncovku "-new", napr test.txt -> test-new.txt
     *
     * Funguje aj "rekurzivne", ak uz existuje aj test-new-new.txt, najde nazov
     * text-new-new-new.txt
     */
    protected String fileNameIfExists(boolean isEnc, String name, String userid) throws SQLException, ClassNotFoundException {
        FileListManager flm = new FileListManager(userid);
        name = isEnc ? name + ".enc" : name;
        while (flm.getUploads(null).get(name) != null) {
                String[] split = name.split("\\.");
                split[0] = split[0] + "-new";
                name = String.join(".", split);
        }
        return name;
    }
}
