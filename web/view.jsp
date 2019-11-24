<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="main.*" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.Objects" %>
<%--
  Created by IntelliJ IDEA.
  User: matus
  Date: 13-Nov-19
  Time: 17:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String userId = null;
    String userName = null;
    //allow access only if session exists
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Cookie[] cookies = request.getCookies();

    if (cookies !=null) {
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("user")) {
                userName = cookie.getValue();
            }

            if (cookie.getName().equals("userId")) {
                userId = cookie.getValue();
            }
        }
    }

    String fileId = request.getParameter("id");
    FileFilter filter = new FileFilter(fileId);

    HashMap<String,String> fileData = null;

    if (userId != null) {
        FileListManager flm = new FileListManager(userId);
        try {
            fileData = flm.getCompleteInfo(filter).get(fileId);
        } catch (SQLException | ClassNotFoundException e) {
            fileData = null;
        }
        if (fileData == null) {
            response.sendRedirect("files.jsp?err=filenotfound");
        }
    }
    ArrayList<HashMap<String, String>> unacceptedRequests = null;
    assert fileData != null;
    if (fileData.get("owner_id").equals(userId)) {
        unacceptedRequests = PermissionHandler.getRequestsForFile(fileId, false);
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

        <div class="alert alert-danger hidden error-flash text-center" role="alert">
            Chyba
        </div>
        <div class="alert alert-success hidden success-flash text-center" role="alert">
        </div>

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
                    <%
                    String ownerName;
                        try {
                            ownerName = User.getNameById(fileData.get("owner_id"));
                        } catch (Exception e) {
                            ownerName = "error";
                        }
                    %>
                    <li class="list-group-item">Vlastní : <%= ownerName %>
                        <% if (fileData.get("owner_id").equals(userId)) { %>
                            <small class="text-muted"> (vy) </small>
                        <% } %>
                    </li>
                </ul>

                <% if (fileData.get("owner_id").equals(userId)) { %>
                    <a href="download/<%= fileId %>" class="btn btn-primary">Stiahnuť súbor</a>
                    <div class="permission-request-container mt-4" style="margin: auto; width: 200px;">
                        <% if (unacceptedRequests != null) { %>
                        <h4>Žiadosti o prístup</h4>
                            <% for (HashMap<String, String> permission : unacceptedRequests) { %>

                                <div class="my-1">
                                    <form name="accept-permission" method="post" action="${pageContext.request.contextPath}/permission/accept">
                                        <%
                                            String permissionFrom;
                                            try {
                                                permissionFrom = User.getNameById(permission.get("to_id"));
                                            } catch (Exception e) {
                                                permissionFrom = "error";
                                            }
                                         %>
                                        <div style="float:left; line-height: 30px;">Žiadosť od : <strong class="text-success"><%= permissionFrom %></strong></div>
                                        <div style="float:right">
                                            <input type="hidden" name="permission-id" value="<%= permission.get("id_p") %>" required>
                                            <button class="btn btn-outline-success"><i class="far fa-thumbs-up"></i></button>
                                        </div>
                                    </form>
                                </div>
                            <% } %>
                        <% } %>
                    </div>
                <% } else { %>
                    <% if (PermissionHandler.canAccess(userId, fileId)) { %>
                        <p class="card-text">K tomuto súboru máte udelený prístup</p>

                        <% if (Integer.parseInt(fileData.get("key_deprecated")) == 1) { %>
                            <a href="download/<%= fileId %>" class="btn btn-primary">Stiahnuť súbor</a><br/>

                            <div class="info-div">
                                <small class="text-muted mt-1 mb-2">
                                    <i class="fas fa-info" aria-hidden="true"></i>
                                    Kľúč použitý na šifrovanie tohto súboru je neaktuálny. Používateľ ho od
                                    nahratia súboru odstránil, alebo pregeneroval. Pre odšifrovanie tohto súboru
                                    je potrebné získať privátny kľúč od majiteľa.
                                </small>
                            </div>
                        <% } else { %>
                        <form action="download/recrypt/<%= fileId %>" method="post" name="decrypt-download">
                            <button type="submit" class="btn btn-primary">Stiahnuť súbor v mojej šifre</button>
                        </form>

                        <div class="info-div">
                            <small class="text-muted mt-1 mb-2">
                                <i class="fas fa-info" aria-hidden="true"></i>
                                Systém sa pokúsi súbor prešifrovať pre váš aktuálny kľúč. Ak ho od šifrovania súboru
                                používateľ zmenil, oznámi vám to systém a ponúkne na stiahnutie súbor ku ktorému budete
                                potrebovať privátny kľúč od majiteľa.
                            </small>
                        </div>
                        <% } %>
                    <% } else { %>
                        <p class="card-text">K tomuto súboru nemáte prístup</p>
                        <% if (
                               PermissionHandler.getRequestStatus(userId, fileId) != null &&
                               Objects.requireNonNull(PermissionHandler.getRequestStatus(userId, fileId)).get("granted").equals("0")
                        ) { %>
                            <p class="text-danger">Čaká sa na potvrdenie vlastníka</p>
                        <% } else { %>
                        <form name="request-file" method="post" action="permission/request">
                            <input type="hidden" name="file-id" value="<%= fileId %>" required>
                            <button type="submit" class="btn btn-primary">Požiadať o povolenie k prístupu</button>
                        </form>
                        <% } %>
                    <% } %>
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

                                        String commentAuthor;
                                        try {
                                            commentAuthor = User.getNameById(comment.get("author_id"));
                                        } catch (Exception e) {
                                            commentAuthor = "error";
                                        }
                                    %>
                                    <li class="media">
                                        <div class="media-body">
                                            <strong class="text-success"><%= commentAuthor %></strong>
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

<script
        src="http://code.jquery.com/jquery-3.4.1.min.js"
        integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script type="application/javascript">
    var url = new URL(window.location.href);
    if (['requestfailed', 'acceptfailed'].includes(url.searchParams.get('error'))) {
        $('.error-flash').show();
    } else if (url.searchParams.get('permission') === "accepted") {
        $('.success-flash').text('Práve ste udelili používateľovi prístup k vášmu súboru').show();
    } else if (url.searchParams.get('permission') === 'requested') {
        $('.success-flash').text('Žiadosť o prístup bola odoslaná').show();
    } else if (url.searchParams.get('success') === 'comment') {
        $('.success-flash').text('Komentár bol pridaný').show();
    } else if (url.searchParams.get('error') === "deprecatedprivatekey") {
        $('.error-flash').text('Nepodarilo sa prešifrovať súbor. Privátny kľúč užívateľa bol od nahratia súbora zmenený, môžete stiahnuť neprešifrovaný súbor, od užívateľa ale na dešifrovanie potrebujete jeho pôvodný privátny kľúč použitý pri šifrovaní súboru').show();
    }

    $('.error-flash, .success-flash').on('click', function () {
        $(this).fadeOut(400, function () {
            $(this).hide();
        });
    });
</script>
</html>
