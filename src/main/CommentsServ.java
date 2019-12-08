package main;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "CommentsServ")
public class CommentsServ extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = parseUrlParams(request.getRequestURL().toString());
        HttpSession session = request.getSession();
        assert params != null;
        if (params.get("method").equals("add")) {
            // Pokusili sme sa pridat prazdny komentar
            if (request.getParameter("body").equals("")) {
                response.sendRedirect("/view.jsp?id=" + params.get("param") + "&error=emptycomment");
                return;
            }

            if (
                !CommentsHandler.addComment(
                    request.getParameter("body"),
                    session.getAttribute("userId").toString(),
                    params.get("param")
                )
            ) {
                response.sendRedirect("/java_webapp_war/view.jsp?id=" + params.get("param") + "&error=comment");
            }
            response.sendRedirect("/java_webapp_war/view.jsp?id=" + params.get("param") + "&success=comment");
            return;
        }

    }

    private HashMap<String, String> parseUrlParams(String url) {
        String[] splitUrl = url.split("/");
        boolean startParams = false;
        int addedParams = 0;


        HashMap<String, String> params = new HashMap<>();
        for (String s : splitUrl) {

            if (startParams && addedParams > 0) {
                // dalej zapisujeme dalsie parametre ktore pridu v url
                params.put("param" + (addedParams - 1 == 0 ? "" : String.valueOf(addedParams - 1)), s);
                addedParams++;
            }

            if (startParams && addedParams == 0) {
                params.put("method", s);
                addedParams++;
            }

            if (s.equals("comments")) {
                startParams = true;
            }
        }
        if (params.get("method") == null || params.get("param") == null) {
            return null;
        }
        return params;
    }
}
