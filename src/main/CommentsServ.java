package main;

import javax.servlet.ServletException;
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> params = parseUrlParams(request.getRequestURL().toString());
        HttpSession session = request.getSession();
        if (params.get("method").equals("add")) {
            if (
                !CommentsHandler.addComment(
                    request.getParameter("body"),
                    session.getAttribute("userId").toString(),
                    params.get("param")
                )
            ) {
                response.sendRedirect("/view.jsp?id=" + params.get("param") + "&error=comment");
            }
            response.sendRedirect("/view.jsp?id=" + params.get("param") + "&success=comment");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

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

            if (splitUrl[i].equals("comments")) {
                startParams = true;
            }
        }
        if (params.get("method") == null || params.get("param") == null) {
            return null;
        }
        return params;
    }
}
