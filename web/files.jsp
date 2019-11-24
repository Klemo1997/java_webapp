<%@ page import="main.FileFilter" %>
<%@ page import="main.FileListManager" %>
<%@ page import="main.PermissionHandler" %>
<%@ page import="main.User" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%--
  Created by IntelliJ IDEA.
  User: matus
  Date: 21-Oct-19
  Time: 16:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%
    //allow access only if session exists
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String userName = null;
    String userId = null;

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

    if (userName == null) {
        response.sendRedirect("logout");
    }

    FileFilter filter = new FileFilter(
            request.getParameter("search_by_names") != null && request.getParameter("search_by_names").equals("on"),
            request.getParameter("search_by_authors") != null && request.getParameter("search_by_authors").equals("on"),
            request.getParameter("file_query"),
            request.getParameter("files_selector") != null && request.getParameter("files_selector").equals("allfiles")
    );

    HashMap<String, Map<String, String>> uploadfiles = null;
    HashMap<String, Integer> unacceptedRequestsCount = new HashMap<>();
    if (userId != null) {
        FileListManager flm = new FileListManager(userId);
        uploadfiles = flm.getUploads(filter);


        for (String fileId : uploadfiles.keySet()) {
            if (uploadfiles.get(fileId).get("owner_id").equals(userId)) {
                unacceptedRequestsCount.put(fileId, PermissionHandler.getRequestsForFile(fileId, false) != null ? PermissionHandler.getRequestsForFile(fileId, false).size() : 0);
            }
        }
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
        <div class="alert alert-danger hidden error-flash" role="alert"></div>
        <div class="alert alert-info hidden success-flash" role="alert"></div>

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
                    <small class="text-muted my-2"><i class="fas fa-info"></i> Pre vyhľadanie súborov podľa autora alebo názvu musí byť zaškrtnuté aspoň jedno políčko.</small>
                <div class="custom-control custom-checkbox mr-sm-2">
                    <input type="checkbox" class="custom-control-input" id="customControlAutosizing" name="search_by_names" <%= request.getParameter("search_by_names") != null && request.getParameter("search_by_names").equals("on") ? "checked" : ""  %> >
                    <label class="custom-control-label" for="customControlAutosizing">Vyhľadať podľa názvu</label>
                </div>
                <div class="custom-control custom-checkbox mr-sm-2" id="author-search">
                    <input type="checkbox" class="custom-control-input" id="customControlAutosizing2" name="search_by_authors" <%= request.getParameter("search_by_authors") != null && request.getParameter("search_by_authors").equals("on") ? "checked" : ""  %> >
                    <label class="custom-control-label" for="customControlAutosizing2">Vyhľadať podľa autora</label>
                </div>

            </div>
            <button class="btn btn-outline-success my-2 my-sm-0 search-file-btn" type="submit">Vyhľadaj súbor</button>
        </form>
    </div>

    <div class="container text-center mb-0"><small class="text-muted mt-2"><i class="fas fa-info"></i> V zátvorke pri cudzích súboroch sa nachádza meno vlastníka súboru </small></div>

    <div class="container text-center border border-primary mt-1">
        <ul class="list-group list-group-flush">
            <li class="list-group-item list-group-item-action active">Zoznam dostupných súborov</li>

            <% if (uploadfiles != null) { %>
                <% for (String file_id : uploadfiles.keySet()) { %>
                <li class="list-group-item">
                    <a href="view.jsp?id=<%= file_id %>">
                        <%=uploadfiles.get(file_id).get("filename")%>
                        <small class="user-name-filelist text-muted">
                            <% try {
                                if (!uploadfiles.get(file_id).get("owner_id").equals(userId)) { %>
                                    <%= "(" + User.getNameById(uploadfiles.get(file_id).get("owner_id")) + ")" %>
                                <% }
                            } catch (Exception e) { %>
                                <%= "error" %>
                            <%
                            } %>
                        </small>
                        <% if (
                           uploadfiles.get(file_id).get("owner_id").equals(userId) &&
                           Integer.parseInt(uploadfiles.get(file_id).get("key_deprecated")) == 1
                        ) { %>
                        <small class="user-name-filelist text-danger">(Neaktuálny kľúč)</small>
                        <% } %>
                    </a>

                    <% if(PermissionHandler.canAccess(userId, file_id)) { %>
                        <a href="download/<%=file_id%>" class="to-right" type="submit" title="Stiahnuť súbor">
                             <i class="fas fa-download"></i>
                        </a>
                    <%  } %>

                    <% if (uploadfiles.get(file_id).get("owner_id").equals(userId)) { %>
                    <span class="badge badge-info"><%= unacceptedRequestsCount != null && unacceptedRequestsCount.get(file_id) > 0 ? "Nové žiadosti : " + unacceptedRequestsCount.get(file_id) : "" %></span>
                    <form class="delete-file to-right" method="post" action="delete/<%= file_id %>">
                        <button type="submit" class="" title="Vymazať súbor"><i class="fas fa-trash-alt text-danger"></i></button>
                    </form>
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

    $('.search-file-btn').on('click', function (e) {
       if ($('input[name=file_query]').val().length > 256) {
           e.preventDefault();
           e.stopPropagation();
           alert('Váš vyhľadávací výraz je príliš dlhý');
       }
    });

    var url = new URL(window.location.href);

    if (url.searchParams.get('err') === "1") {
        $('.error-flash').text('Nastala neočakávaná chyba').show();
    } else if (url.searchParams.get('err') === "filenotfound") {
        $('.error-flash').text('Súbor sa nenašiel').show();
    } else if (url.searchParams.get('err') === "deleteerror") {
        $('.success-flash').text('Pri odstraňovaní súboru nastala chyba').show();
    } else if (url.searchParams.get('success') === "deleted") {
        $('.success-flash').text('Súbor bol odstránený').show();
    }

    $('.error-flash, .success-flash').on('click', function () {
        $(this).fadeOut(400, function () {
            $(this).hide();
        });
    });
    $('.delete-file').hide();

    $('.list-group-item').on('mouseenter', function(){
        $(this).find('.delete-file').fadeIn(200, function () {});
    }).on('mouseleave', function () {
        $('.delete-file').hide();
    });

    $('.delete-file').on('submit', function (e) {
        if (!confirm('Chcete naozaj odstrániť tento súbor ?')) {
            e.preventDefault();
            e.stopPropagation();
        }
    });
</script>
</body>
</html>
