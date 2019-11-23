package main;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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

                String mode = null;

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
                            name = FileListManager.sanitizeFileName(
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
                        if ("cryption-type".equals(item.getFieldName())) {
                            mode = item.getString();
                        }
                    }
                }
                //File uploaded successfully
                assert mode != null;
                if (mode.equals("enc")) {
                    CryptoUtils.encrypt(keyFile,temp,encrypted);
                } else if (mode.equals("dec")) {
                    CryptoUtils.decrypt(keyFile,temp,encrypted);
                }

                if (encrypted == null) {
                    throw new Exception("crypt_file_error");
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


}
