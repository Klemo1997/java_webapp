<%@ page import="main.FileListManager" %>
<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: matus
  Date: 25-Oct-19
  Time: 22:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    FileListManager flm = new FileListManager();
    Map<String, String> keysFiles =  flm.getKeys();

%>
<html>
<head>
    <title>Vygenerovať nové kľúče</title>
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
                    <a class="nav-link" href="/java_webapp_war">Nahrať súbor</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/java_webapp_war/files.jsp">Moje súbory</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/java_webapp_war/key_manager.jsp">Pregenerovať kľúče</a>
                </li>
            </ul>
        </div>
    </nav>
    <!-- /Navbar -->

    <div class="container text-center mt-5">

        <div class="form-box text-center" align="center">
            <h3> Pregenerovať kľúče </h3>

            <form name="fm" action="keys_regenerate" method="post" id="regenerate-form" enctype="multipart/form-data">
                <div class="input-group input-group-sm mt-3 mb-2" style="margin: auto">
                    <input type="submit" class="btn btn-primary btn-block" value="Pregeneruj kľúče">
                </div>
            </form>
        </div>

        <h3> Stiahni kľúče </h3>
        <div class="container text-center border border-primary">
            <ul class="list-group list-group-flush">
                <li class="list-group-item list-group-item-action active">Zoznam dostupných súborov</li>
                <% for (Map.Entry<String,String> entry : keysFiles.entrySet()) { %>
                <li class="list-group-item"><%=entry.getKey()%> <a class="to-right" href="download/<%=entry.getKey()%>"><i class="fas fa-download"></i></a></li>
                <% } %>
            </ul>
        </div>

    </div>

    <style>
        .to-right {
            float: right;
        }

        .container {
            padding: 0;
            margin-top: 50px;
            margin-bottom: 40px;
        }
    </style>

    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

    <script>

        $('#regenerate-form').on('submit', function (e) {
            if (!confirm('Naozaj chcete pregenerovať svoje kľúče?')) {
                e.preventDefault();
                e.stopPropagation();
            }
        });
    </script>

</body>
</html>
