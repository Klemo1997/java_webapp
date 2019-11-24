package main;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class PermissionServ extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HashMap<String, String> params = parseUrlParams(req.getRequestURL().toString(), "permission");
        HttpSession session = req.getSession();
        String redirect = "";

        assert params != null;
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
            redirect = Objects.requireNonNull(PermissionHandler.getRequestStatus(req.getParameter("permission-id"))).get("to_file_id");
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
    static HashMap<String, String> parseUrlParams(String url, String rootparam) {
        String[] splitUrl = url.split("/");
        boolean startParams = false;
        int addedParams = 0;

        HashMap<String, String> params;
        params = new HashMap<>();
        for (String s : splitUrl) {

            if (startParams) {
                // dalej zapisujeme dalsie parametre ktore pridu v url
                if (addedParams > 0) {
                    params.put("param" + (addedParams - 1 == 0 ? "" : String.valueOf(addedParams - 1)), s);
                    addedParams++;
                }

                if (addedParams == 0) {
                    params.put("method", s);
                    addedParams++;
                }
            }

            if (s.equals(rootparam)) {
                startParams = true;
            }
        }
        if (params.get("method") == null && params.get("param") == null) {
            return null;
        }
        return params;
    }
}
