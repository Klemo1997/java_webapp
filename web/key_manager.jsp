<%@ page import="main.FileListManager" %>
<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: matus
  Date: 25-Oct-19
  Time: 22:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    //allow access only if session exists
    String userId;

    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    } else {
        userId = (String) session.getAttribute("userId");
    }
    String userName = null;

    Cookie[] cookies = request.getCookies();

    if (cookies !=null) {
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("user")) {
                userName = cookie.getValue();
            }
            if(cookie.getName().equals("userId")) {
                userId = cookie.getValue();
            }
        }
    }

    if (userName == null) {
        response.sendRedirect("/logout");
    }
    Map<String, String> keysFiles = null;

    if (userId != null) {
        FileListManager flm = new FileListManager(userId);
        keysFiles =  flm.getKeys();
    }

%>
<html>
<head>
    <title>Vygenerovať nové kľúče</title>
    <link rel="stylesheet" href="assets/css/bootstrap.min.css">
    <link href="assets/fontawesome/css/all.css" rel="stylesheet">
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

    <div class="container text-center mt-5">

        <div class="text-center border-primary border p-5 regenerate-keys">
            <h3><%= keysFiles.size() == 0 ? "Vygenerovať nové kľúče" : "Pregenerovať kľúče" %></h3>

            <form name="fm" action="keys_regenerate" method="post" id="regenerate-form" enctype="multipart/form-data">
                <div class="input-group input-group-sm mt-3 mb-2" style="margin: auto">
                    <input type="submit" class="btn btn-primary btn-block" value="<%= keysFiles.size() == 0 ? "Vygeneruj nové kľúče" : "Pregeneruj kľúče" %>">
                </div>
            </form>
        </div>


        <% if (keysFiles.size() != 0) { %>
            <div class="container text-center p-5">
                <h3 class="mb-3"> Stiahni kľúče </h3>
                <ul class="list-group list-group-flush border-primary border">
                    <li class="list-group-item list-group-item-action active">Zoznam dostupných súborov</li>
                    <% if (keysFiles != null) {
                         for (Map.Entry<String,String> entry : keysFiles.entrySet()) { %>
                        <li class="list-group-item"><%=entry.getKey()%> <a class="to-right" href="download/<%=entry.getKey()%>"><i class="fas fa-download"></i></a></li>
                        <% }
                     } %>
                </ul>
            </div>
        <% } %>
    </div>

    <script src="assets/js/jquery-3.4.1.min.js"></script>
    <script src="assets/js/popper.min.js"></script>
    <script src="assets/js/bootstrap.min.js"></script>

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
