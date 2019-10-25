<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.util.stream.Stream" %>
<%@ page import="main.FileListManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%--
  Created by IntelliJ IDEA.
  User: matus
  Date: 21-Oct-19
  Time: 16:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    FileListManager flm = new FileListManager();
    Map<String, String> uploadfiles =  flm.getUploads();
%>
<html>
<head>
    <title>Moje súbory</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <script src="https://kit.fontawesome.com/fc14f2d665.js" crossorigin="anonymous"></script>
</head>
<body>
<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="#">UBP</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" href="/java_webapp_war_exploded">Nahrať súbor</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/java_webapp_war_exploded/files.jsp">Moje súbory</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/java_webapp_war_exploded/key_manager.jsp">Pregenerovať kľúče</a>
            </li>
        </ul>
    </div>
</nav>

<!-- /Navbar -->

    <div class="container text-center border border-primary">
        <ul class="list-group list-group-flush">
            <li class="list-group-item list-group-item-action active">Zoznam dostupných súborov</li>
                <% for (Map.Entry<String,String> entry : uploadfiles.entrySet()) { %>
                    <li class="list-group-item"><div class="to-left"><input type="checkbox" name="decrypt_or_nah" class="decrypt-check"><i class="fas fa-lock-open"></i></div> <%=entry.getKey()%> <a href="download/<%=entry.getKey()%>"><i class="fas fa-download"></i></a></li>
                <% } %>
        </ul>
    </div>


<style>
    .container {
        padding: 0;
        margin-top: 50px;
    }
    .fas.fa-lock-open {
        margin-right: 10px;
        margin-left: 10px;
        font-size: 20px;
    }

    .to-left {
        float: left;
    }

    .list-group a {
        float: right;
        display: inline-block;
    }

</style>

<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script>
    $('.list-group-item a').on('click', function () {

        var checkbox_checked = $(this).siblings('.to-left').find('input').is(':checked');

        if (checkbox_checked) {
            window.location.replace($(this).attr('href') + '?decrypt=true');
        }
    });
</script>
</body>
</html>
