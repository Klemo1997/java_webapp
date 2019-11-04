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

public class FileUploadHandler extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    public static String setMetaString = null;
    public static String privateKeyToSee = null;

    private final String UPLOADDIRECTORY = "/usr/local/uploads";

    public FileUploadHandler() throws MalformedURLException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
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
                            keyFile = new File(UPLOADDIRECTORY + File.separator + "inputKey");
                            item.write(keyFile);
                        } else {
                            String name = new File(item.getName()).getName();
                            temp = new File(UPLOADDIRECTORY + File.separator + name);
                            item.write(temp);
                            encrypted = new File(UPLOADDIRECTORY + File.separator + name + ".enc");
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

                response.setContentType("application/x-msdownload");
                response.setHeader("Content-Disposition", "attachment; filename=" +  fileName);

                OutputStream out = response.getOutputStream();
                FileInputStream in = new FileInputStream(encrypted);

                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, length);
                }

                FileDownloadServlet fileDownload = new FileDownloadServlet();
                fileDownload.doGet(request, response);
                // Neviem ci to s tymto funguje tak to pushnem aspon zakomentovane :D
//                if (!temp.delete()) {
//                    // Exception file delete failed
//                }

                request.setAttribute("message","File Uploaded Successfully");
            }
            catch(Exception ex)
            {
                request.setAttribute("message", "File Enc/Dec Failed due to" + ex);
                response.sendRedirect(request.getContextPath() + "?error=1" );
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
}
