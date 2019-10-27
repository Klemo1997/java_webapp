<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "−//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http−equiv="Content−Type" content="text/html; charset=UTF−8">
    <title>Nahraj súbor </title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
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

<div class="container text-center mt-5">

    <div class="form-box text-center" align="center">
        <h3> Vyber súbor </h3>

        <form name="fm" action="upload" method="post" id="crypto-form" enctype="multipart/form-data">
            <div class="input-group mt-2 mb-3">
                <div class="custom-file">
                    <input type="file" name="file" class="form-control" id="inputGroupFile01" required="required">
                    <label class="custom-file-label" for="inputGroupFile01">Vyber súbor</label>
                </div>
            </div>

            <div class="input-group mt-2 mb-3">
                <div class="custom-file">
                    <input type="file" name="keyFile" class="form-control" id="inputGroupFile02" required="required">
                    <label class="custom-file-label" id="key_label" for="inputGroupFile02">Nahraj verejný kľúč</label>
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
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
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


</script>
</body>


</html>
