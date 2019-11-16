package main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

public class PermissionServ extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> params = parseUrlParams(req.getRequestURL().toString());
        HttpSession session = req.getSession();
        String redirect = "";

        if (params.get("method").equals("request")) {
            // Ziadame o file
            //todo: skontroluj ci nieje nas vlastny
            //todo: skontroluj ci uz taky req neexistuje nahodoff
            redirect = req.getParameter("file-id");
            try {
                PermissionHandler.sendRequest(session.getAttribute("userId").toString(), req.getParameter("file-id"));
                redirect += "&permission=requested";
            } catch (Exception e) {
                //Error
                redirect += "&error=requestfailed";
            }
        } else if (params.get("method").equals("accept")) {
            redirect = PermissionHandler.getRequestStatus(req.getParameter("permission-id")).get("to_file_id").toString();
            try {
                if (!PermissionHandler.acceptRequest(req.getParameter("permission-id")))
                    throw new Exception("error");
                redirect += "&permission=accepted";

            } catch (Exception e) {
                //Error
                redirect += "&error=acceptfailed";
            }
        }
        resp.sendRedirect("/java_webapp_war/view.jsp?id="+redirect);
    }


    // Vyparsuje urlku v tvare permission/metoda/parameter/...
    private HashMap<String, String> parseUrlParams(String url) {
        String[] splitUrl = url.split("/");
        boolean startParams = false;
        int addedParams = 0;

        HashMap<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < splitUrl.length; i++) {

            if (startParams && addedParams > 0) {
                // dalej zapisujeme dalsie parametre ktore pridu v url
                params.put("param" + (addedParams - 1 == 0 ? "" : String.valueOf(addedParams - 1)), splitUrl[i]);
                addedParams++;
            }

            if (startParams && addedParams == 0) {
                params.put("method", splitUrl[i]);
                addedParams++;
            }

            if (splitUrl[i].equals("permission")) {
                startParams = true;
            }
        }
        if (params.get("method") == null && params.get("param") == null) {
            return null;
        }
        return params;
    }
}
