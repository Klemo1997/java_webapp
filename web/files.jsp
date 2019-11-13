<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.util.stream.Stream" %>
<%@ page import="main.FileListManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="main.FileListManager" %>
<%@ page import="java.nio.file.FileSystems" %>
<%@ page import="main.FileFilter" %>
<%--
  Created by IntelliJ IDEA.
  User: matus
  Date: 21-Oct-19
  Time: 16:15
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

    FileFilter filter = new FileFilter(
            request.getParameter("search_by_names") != null && request.getParameter("search_by_names").equals("on"),
            request.getParameter("search_by_authors") != null && request.getParameter("search_by_authors").equals("on"),
            request.getParameter("file_query"),
            request.getParameter("files_selector") != null && request.getParameter("files_selector").equals("allfiles")
    );
    if (request.getParameter("file_query") != null) {

    }


    Map<String, String> uploadfiles = null;

    if (userId != null) {
        FileListManager flm = new FileListManager(userId);
        uploadfiles = !filter.allFiles
            ? flm.getUploads(filter)
            : flm.getAllUploads(filter);
    }
%>

<html>
<head>
    <title>Moje súbory</title>
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
    <!-- /Navbar -->

    <div class="container filter text-center my-4">
        <h3>Filtrovať:</h3>

        <form class="form" name="search_files" method="get" action="">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <label class="input-group-text" for="inputGroupSelect01">Vyhľadať:</label>
                </div>
                <select class="custom-select" id="inputGroupSelect01" name="files_selector">
                    <option value="myfiles"  <%= request.getParameter("files_selector") == null || !request.getParameter("files_selector").equals("allfiles") ? "selected" : "" %>>Moje súbory</option>
                    <option value="allfiles" <%= request.getParameter("files_selector") != null && request.getParameter("files_selector").equals("allfiles") ? "selected" : "" %>>Všetky súbory</option>
                </select>
            </div>
            <div class="border border-primary my-3 p-5">
            <input class="form-control mr-sm-2" type="search" placeholder="Vyhľadať..." aria-label="Search" name="file_query"
                <%= request.getParameter("file_query") != null ? "value='" + request.getParameter("file_query") + "'" : "" %> >
                <div class="custom-control custom-checkbox mr-sm-2">
                    <input type="checkbox" class="custom-control-input" id="customControlAutosizing" name="search_by_names" <%= request.getParameter("search_by_names") != null && request.getParameter("search_by_names").equals("on") ? "checked" : ""  %> >
                    <label class="custom-control-label" for="customControlAutosizing">Vyhľadať podľa názvu</label>
                </div>
                <div class="custom-control custom-checkbox mr-sm-2" id="author-search">
                    <input type="checkbox" class="custom-control-input" id="customControlAutosizing2" name="search_by_authors" <%= request.getParameter("search_by_authors") != null && request.getParameter("search_by_authors").equals("on") ? "checked" : ""  %> >
                    <label class="custom-control-label" for="customControlAutosizing2">Vyhľadať podľa autora</label>
                </div>
                <small class="text-muted"><i class="fas fa-info"></i> Pre vyhľadanie súborov podľa  musí byť zaškrtnuté aspoň jedno políčko.</small>
            </div>
            <button class="btn btn-outline-success my-2 my-sm-0 search-file-btn" type="submit">Vyhľadaj súbor</button>
        </form>
    </div>

    <div class="container text-center border border-primary">

        <ul class="list-group list-group-flush">
            <li class="list-group-item list-group-item-action active">Zoznam dostupných súborov</li>
                <% if (uploadfiles != null) { %>
                    <% for (Map.Entry<String,String> entry : uploadfiles.entrySet()) { %>
                    <li class="list-group-item">
                        <a href="files/view/<%= entry.getValue() %>"><%=entry.getKey()%></a>
                        <% if (entry.getValue().split("/")[1].equals(userId)) { %>
                        <a href="download/<%=entry.getKey()%>" class="to-right" type="submit">
                             <i class="fas fa-download"></i>
                        </a>
                        <% } %>
                    </li>
                    <% } %>
                <% } %>
        </ul>
    </div>

    <script
            src="http://code.jquery.com/jquery-3.4.1.min.js"
            integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
            crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script>
    $('select[name=files_selector]').on('change', function () {
        if ($(this).val() === 'myfiles') {
            $('#author-search').fadeOut(400, function () {
                $('input[name=search_by_names]').prop('checked', true);
                $(this).find('input').prop('checked', false);
            });

        } else {
            $('#author-search').fadeIn(400, function () {});
        }
    });

    $('input[type=checkbox]').on('change', function () {
        var isAtLeastOneChecked = $('input[type=checkbox]').is(':checked');
        $('input[name=file_query]').prop('readonly', !isAtLeastOneChecked);
    });

    if ($('select[name=files_selector]').val() === 'myfiles') {
        $('#author-search').fadeOut(400, function () {});
    }

    $('input[name=file_query]').prop('readonly', !$('input[type=checkbox]').is(':checked'));

</script>
</body>
</html>
