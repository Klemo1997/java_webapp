<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("user")) {
                userName = cookie.getValue();
            }
            if(cookie.getName().equals("userId")) {
                userId = cookie.getValue();
            }
            if(cookie.getName().equals("JSESSIONID")) {
                sessionID = cookie.getValue();
            }
        }
    }

    if (userName == null) {
        // todo: tu musime osetrit "Prihlaseny ako null" bug
    }

%>


<!DOCTYPE HTML PUBLIC "−//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http−equiv="Content−Type" content="text/html; charset=UTF−8">
    <title>Nahraj súbor </title>
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
                    <a class="nav-link" href=""><i class="fas fa-upload"></i> Nahrať súbor</a>
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

    <div class="alert alert-danger hidden error-flash" role="alert">
        Upload súboru zlyhal, skúste to znova, prosím<br>
        Pri dešifrovaní skontrolujte, či ste použili správny privátny kľúč
    </div>

    <div class="form-box text-center" align="center">
        <h3> Vyber súbor </h3>

        <form name="fm" action="upload" method="post" id="crypto-form" enctype="multipart/form-data">
            <div class="input-group mt-2 mb-3">
                <div class="custom-file">
                    <input type="file" name="file" class="custom-file-input form-control" id="inputGroupFile01" required="required">
                    <label class="custom-file-label text-left" for="inputGroupFile01">Vyber súbor</label>
                </div>
            </div>

            <div class="input-group mt-2 mb-3">
                <div class="custom-file">
                    <input type="file" name="keyFile" class="custom-file-input form-control" id="inputGroupFile02" required="required">
                    <label class="custom-file-label text-left" id="key_label" for="inputGroupFile02">Nahraj verejný kľúč</label>
                </div>
            </div>

            <div class="custom-control custom-radio custom-control-inline">
                <input type="radio" id="customRadioInline1" name="cryption-type" class="custom-control-input" value="enc" checked>
                <label class="custom-control-label" for="customRadioInline1">Šifrovať</label>
            </div>
            <div class="custom-control custom-radio custom-control-inline">
                <input type="radio" id="customRadioInline2" name="cryption-type" class="custom-control-input" value="dec">
                <label class="custom-control-label" for="customRadioInline2">Dešifrovať</label>
            </div>

            <div class="input-group input-group-sm mt-3 mb-2" style="margin: auto">
                <input type="submit" class="btn btn-primary btn-block" value="Nahraj súbor">
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
    $('input[type=radio][name=cryption-type]').on('change', function () {
          var keystr, keyfilestr;
        if ($(this).val() === 'enc') {
            keyfilestr = 'Nahraj verejný kľúč';
            $('.only-enc-visible').removeClass('d-none');
        } else if ($(this).val() === 'dec') {
            keyfilestr = 'Nahraj privátny kľúč';
            $('.only-enc-visible').addClass('d-none');
        }

        $('#key_label').text(keyfilestr)
    });

    var url = new URL(window.location.href);
    if (url.searchParams.get('error') === "1") {
        $('.error-flash').show();
    }

    $('.error-flash').on('click', function () {
        $(this).fadeOut(400, function () {
            $(this).hide();
        });
    });

    // Add the following code if you want the name of the file appear on select
    $(".custom-file-input").on("change", function() {
        var fileName = $(this).val().split("\\").pop();
        $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
    });


</script>
</body>


</html>
