<%--
  Created by IntelliJ IDEA.
  User: adria
  Date: 11/9/2019
  Time: 11:30 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session.getAttribute("userId") != null) {
        response.sendRedirect("index.jsp");
    }
%>
<html>
<head>
    <meta http-equiv="Content−Type" content="text/html; charset=UTF−8">
    <title>Registrácia</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <script src="https://kit.fontawesome.com/fc14f2d665.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="main.css">
</head>
<body>
<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="#">UBP</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" href="login.jsp"><i class="fas fa-sign-in-alt"></i> Prihlásenie</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="registration.jsp"><i class="fas fa-user-plus"></i> Registrácia</a>
                </li>
            </ul>
        </div>
    </div>
</nav>



<div class="container text-center mt-5">

    <div class="alert alert-danger hidden error-flash" role="alert"></div>


    <div class="form-box text-center">
        <h2 class="display-5 mb-4"> Registrácia </h2>

        <form name="login-data" action="register" method="post">
            <label class="custom-label" for="password">Prihlasovacie meno:</label>
            <div class="input-group mb-3">
                <input type="text" class="form-control" placeholder="Prihlasovacie meno" name="user_login" required>
            </div>

            <label class="custom-label" for="password">Zadajte heslo:</label>
            <div class="input-group mb-4">
                <input type="password" id="password" class="form-control" placeholder="Heslo" name="user_password" required>
            </div>
            <label class="custom-label" for="password">Znova zadajte heslo:</label>
            <div class="input-group mb-4">
                <input type="password" id="passwordcheck" class="form-control" placeholder="Heslo" name="user_password_check" required>
            </div>
            <div class="input-group input-group-sm my-2">
                <input type="submit" class="btn btn-primary btn-block" value="Vytvoriť">
            </div>

        </form>
    </div>
</div>

<script
        src="http://code.jquery.com/jquery-3.4.1.min.js"
        integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script type="application/javascript">
    var errorTexts = {
        userexists   : "Tento používateľ už je zaregistrovaný",
        passmismatch : "Heslá sa nezhodujú",
        passinsec    : "Heslo nieje bezpečné. Uistite sa, že obsahuje aspoň 1 veľké písmeno, 1 malé písmeno a aspoň jednu číslicu a je v rozmedzí 8 až 40 znakov",
        passindict   : "Vaše heslo nieje bezpečné, pretože bolo nájdené medzi nebezpečnými heslami nášho slovníka"
    };

    var url = new URL(window.location.href);
    if (url.searchParams.get('error') !== null) {
        var errorType = url.searchParams.get('error');

        if (typeof errorTexts[errorType] !== "undefined") {
            $('.error-flash').text(errorTexts[errorType]).show();
        } else {
            $('.error-flash').text("Pri registrácii došlo k chybe, skúste to znova, prosím.").show();
        }
    }

    $('.error-flash').on('click', function () {
        $(this).fadeOut(400, function () {
            $(this).hide();
        });
    });

</script>
</body>


</html>
