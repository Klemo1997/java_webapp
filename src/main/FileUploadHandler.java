
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
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;

public class FileUploadHandler extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    public static String setMetaString = null;
    public static String privateKeyToSee = null;
    private final String UPLOADDIRECTORY = "C:/Users/adria/eclipse-workspace/upb/uploads";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // process only if its multipart content
        if(ServletFileUpload.isMultipartContent(request)) {
            try {
                List <FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

                File
                temp = null,
                encrypted = null;

                String
                key = null,
                mode = null;

                for(FileItem item : multiparts)
                {
                    if(!item.isFormField())
                    {
                        String name = new File(item.getName()).getName();
                        temp = new File(UPLOADDIRECTORY + File.separator + name);
                        item.write(temp);
                        encrypted = new File(UPLOADDIRECTORY + File.separator + name + ".enc");
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
                    CryptoUtils.encrypt(key,temp,encrypted);
                } else if (mode.equals("dec")) {
                    CryptoUtils.decrypt(key,temp,encrypted);
                }

                String fileName = mode.equals("enc")
                    ? encrypted.getName()
                    : encrypted.getName().replace(".enc", "");

                response.setContentType("application/x-msdownload");
                response.setHeader("Content-Disposition", "attachment; filename=" +  fileName);
                
                
                FileStore fs = Files.getFileStore(encrypted.toPath());
                if (fs.supportsFileAttributeView("basic"))
                if (setMetaString != null && privateKeyToSee != null) {
                	// response.setHeader("metadata", setMetaString);
                	// malo by nasetovat atribut zo zasifrovanej RSA a AES z vrateneho retazca v cryptoUtils
                	UserDefinedFileAttributeView view = Files
                		    .getFileAttributeView(encrypted.toPath(), UserDefinedFileAttributeView.class);
                	String name = "user.myAttribute";
                	view.write(name, Charset.defaultCharset().encode(setMetaString));
                	ByteBuffer buf = ByteBuffer.allocate(view.size(name));
                	view.read(name, buf);
                	buf.flip();
                	String value = Charset.defaultCharset().decode(buf).toString();
                	System.out.println(String.format("Public Key -> %s \n Private Key -> %s \n", value, privateKeyToSee));
                	// Files.setAttribute(encrypted.toPath(), "basic:read", setMetaString, LinkOption.NOFOLLOW_LINKS);
                	Files.getAttribute(encrypted.toPath(), name, LinkOption.NOFOLLOW_LINKS);
//                	Files.write(encrypted.toPath(), setMetaString.getBytes(), StandardOpenOption.APPEND);
                	
                	// Files.setAttribute(encrypted.toPath(), "dos:readonly", setMetaString);
                }

                FileDownloadServlet fileDownload = new FileDownloadServlet();
                fileDownload.doGet(request, response);

                if (!temp.delete()) {
                    // Exception file delete failed
                }


                request.setAttribute("message","File Uploaded Successfully");
            }
            catch(Exception ex)
            {
                request.setAttribute("message", "File Enc/Dec Failed due to" + ex);
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
