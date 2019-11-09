<%--
  Created by IntelliJ IDEA.
  User: adria
  Date: 11/9/2019
  Time: 11:30 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%

%>
<html>
<head>
    <meta http−equiv="Content−Type" content="text/html; charset=UTF−8">
    <title>Registruj sa</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
</head>
<body>
<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-light bg-light" style="background-color: #e6e6e6 !important;">
    <div class="container">
        <a class="navbar-brand" href="#">UBP</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" href="/java_webapp_war_exploded/login.jsp">Prihlásenie</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/java_webapp_war_exploded/registration.jsp">Registrácia</a>
                </li>
            </ul>
        </div>
    </div>
</nav>



<div class="container text-center mt-5">

    <div class="form-box text-center" align="center">
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
                <input type="submit" class="btn btn-primary btn-block" value="Registruj sa">
            </div>

        </form>
    </div>
</div>

<style>

    .form-box {
        margin: auto;
        background-color: #e6e6e6;
        width: 350px;

        padding: 20px 20px 20px 20px;
    }

    .generate-key-div {
        width: 100%;
    }

</style>
<script
        src="http://code.jquery.com/jquery-3.4.1.min.js"
        integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script type="application/javascript">
    $('input[type=radio][name=cryption-type]').on('change', function () {
        var keystr = $(this).val() === 'enc'
            ? 'Šifrovací kľúč'
            : 'Dešifrovací kľúč';

        if ($(this).val() === 'enc') {
            $('.only-enc-visible').removeClass('d-none');
        } else if ($(this).val() === 'dec') {
            $('.only-enc-visible').addClass('d-none');
        }

        $('#key-label').text(keystr);
    })

    $('#enable-generate').on('click', function () {
        if ($('.generate-key-div').hasClass('d-none')) {
            $('.generate-key-div').removeClass('d-none');
        } else {
            $('.generate-key-div').addClass('d-none');
        }
    })

    $('.generate-key-btn').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var key_bytes = $('#key-bits').val() / 8;
        var rand_string = generateId(key_bytes);
        $('#key').val(rand_string);
    })

    function dec2hex (dec) {
        return ('0' + dec.toString(16)).substr(-2)
    }

    // generateId :: Integer -> String
    function generateId (len) {
        var arr = new Uint8Array((len || 40) / 2)
        window.crypto.getRandomValues(arr)
        return Array.from(arr, dec2hex).join('')
    }

</script>
</body>


</html>
