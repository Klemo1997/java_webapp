package main;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FileDeleteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HashMap<String, String> params = PermissionServ.parseUrlParams(req.getRequestURL().toString(), "delete");

        HttpSession session = req.getSession();


        String redirect = "/files.jsp";
        if (session == null || params == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("/login.jsp");
            return;
        }

        try {
            Integer.parseInt(params.get("method"));
            String fileId = params.get("method");
            String userId = session.getAttribute("userId").toString();

            DbHandler db = new DbHandler();

            // Vytiahneme path k suboru
            FileListManager flm = new FileListManager(userId);
            HashMap<String, String> fileInfo = flm.getCompleteInfo(new FileFilter(fileId)).get(fileId);

            if (!fileInfo.get("owner_id").equals(userId)) {
                // Nejaky haker sa pokusa vymazavat
                throw new Exception("unauthorized");
            }
            String path = fileInfo.get("path");
            // Odstranime file z databazy
            db.deleteFile(fileId);
            // Odstranime ho zo zlozky
            File toDelete = new File(path);

            if (!toDelete.delete()) {
                throw new Exception("fileio_delete_error");
            }

            redirect += "?success=deleted";
        } catch (Exception e) {
            //Error
            redirect += "?err=deleteerror";
        }
        resp.sendRedirect(redirect);
    }
}
