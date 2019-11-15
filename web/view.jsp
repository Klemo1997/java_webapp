<%@ page import="main.FileFilter" %>
<%@ page import="main.FileListManager" %>
<%@ page import="main.User" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="main.CommentsHandler" %>
<%--
  Created by IntelliJ IDEA.
  User: matus
  Date: 13-Nov-19
  Time: 17:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    //allow access only if session exists
    String user = null;

    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
    } else {
        user = (String) session.getAttribute("userId");
    }
    String userName = null;
    String userId = null;
    String sessionID = null;

    Cookie[] cookies = request.getCookies();

    if (cookies !=null) {
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("user")) {
                userName = cookie.getValue();
            }

            if (cookie.getName().equals("userId")) {
                userId = cookie.getValue();
            }

            if (cookie.getName().equals("JSESSIONID")) {
                sessionID = cookie.getValue();
            }
        }
    }

    if (userName == null) {

    }

    String fileId = request.getParameter("id");
    FileFilter filter = new FileFilter(fileId);

    HashMap<String,String> fileData = null;

    if (userId != null) {
        FileListManager flm = new FileListManager(userId);
        fileData = flm.getCompleteInfo(filter).get(fileId);
        if (fileData == null) {
            response.sendRedirect("/files.jsp?err=filenotfound");
        }
    }

    // Vytiahneme komentare
    ArrayList<HashMap<String,String>> comments = CommentsHandler.getCommentsByFile(fileId);

%>
<html>
<head>
    <title>File <%= fileData.get("filename") %></title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <script src="https://kit.fontawesome.com/fc14f2d665.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="main.css">
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container" style="margin: 0 auto;">
            <a class="navbar-brand" href="#">UBP</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link" href="index.jsp"><i class="fas fa-upload"></i> Nahrať súbor</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="files.jsp"><i class="fas fa-folder"></i> Moje súbory</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="key_manager.jsp"><i class="fas fa-key"></i> Pregenerovať kľúče</a>
                    </li>
                </ul>
                <ul class="navbar-nav ml-auto">
                    <li class="nav-item logged-as">
                        Prihlásený ako: <%= userName %>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="logout"><i class="fas fa-power-off"></i> Odhlásiť sa</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="card text-center">
            <div class="card-header">
                <h1> Detail súboru : </h1>
            </div>
            <div class="card-body">
                <h1 class="large-file-box">
                    <i class="fas fa-file-alt" style="font-size: 250%"></i>
                </h1>
                <h5 class="card-title"><%= fileData.get("filename") %></h5>
                <ul class="list-group mb-4" style="width: 40%; margin: auto">
                    <li class="list-group-item">Typ : <%= fileData.get("mime_type") %></li>
                    <li class="list-group-item">Vlastní : <%= User.getNameById(fileData.get("owner_id")) %>
                        <% if (fileData.get("owner_id").equals(userId)) { %>
                            <small class="text-muted"> (vy) </small>
                        <% } %>
                    </li>
                </ul>

                <% if (fileData.get("owner_id").equals(userId)) { %>
                     <a href="download/<%= fileId %>" class="btn btn-primary disabled">Stiahnuť súbor</a>
                <% } else { %>
                    <p class="card-text">Ku tomuto súboru nemáte prístup</p>
                    <a href="#" class="btn btn-primary disabled">Požiadať o povolenie k prístupu</a>
                <% } %>
            </div>
            <div class="card-footer text-muted"></div>
        </div>

        <div class="row bootstrap snippets comment-section">
            <div class="col-md-6 col-md-offset-2 col-sm-12 mx-auto">
                <div class="comment-wrapper">
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            Panel komentárov
                        </div>
                        <div class="panel-body">
                            <form method="post" name="add-comment" action="comments/add/<%= fileId %>">
                                <textarea class="form-control" name="body" placeholder="Napíš komentár..." rows="3"></textarea>
                                <br>
                                <button type="submit" class="btn btn-info" style="float: right;">Pridať komentár</button>
                            </form>
                            <div class="clearfix"></div>
                            <hr>
                            <ul class="media-list">
                               <% if (comments != null) { %>
                                    <% for(int i = comments.size() - 1; i >= 0; i--) {
                                        // Potrebujeme vypisovat od najnovsich, opacne
                                        HashMap<String, String> comment = comments.get(i);
                                    %>
                                    <li class="media">
                                        <div class="media-body">
                                            <strong class="text-success"><%= User.getNameById(comment.get("author_id")) %></strong>
                                            <p>
                                                <%= comment.get("body") %>
                                            </p>
                                        </div>
                                    </li>
                                    <% } %>
                               <% } %>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
