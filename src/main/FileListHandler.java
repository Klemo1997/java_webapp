package main;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// You can configure one or multiple 'URL Patterns' can access this Servlet.
@WebServlet(urlPatterns = { "/files/download/*" })
public class FileListHandler extends HttpServlet {
    private final String UPLOADDIRECTORY = "C:/Users/matus/IdeaProjects/java_webapp/uploads";
    private static final long serialVersionUID = 1L;


    public FileListHandler() {
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> fileMap = new HashMap<String, String>();

        List<String> uploads = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(Paths.get(UPLOADDIRECTORY))) {

            uploads = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());



        } catch (IOException e) {
            e.printStackTrace();
        }

        ServletOutputStream out = response.getOutputStream();
        out.println("<html>");
        out.println("<head><title>Hello Servlet</title></head>");
        out.println("<body>");
        out.println("<h3>Output servlet</h3>");

        out.println("<form name=\"fd\" action=\"files/download\" method=\"post\" id=\"file-download\" >");
        out.println("<input type=\"hidden\" id=\"file-path\" name=\"file-path\">");
        for (String item : uploads) {
            String itemName = item.substring(item.lastIndexOf("\\") + 1);
            fileMap.put(itemName, item.replace("\\", "/"));
            out.println("<a href=\"#\" onclick=\"document.querySelector('#file-path').value = '" + fileMap.get(itemName)  + "'; document.querySelector('#file-download').submit();\">" + itemName + "</a> <br>");
        }
        out.println("</form>");
        out.println("</body>");
        out.println("<html>");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String filePath = request.getParameter("file-path");
    }
}
