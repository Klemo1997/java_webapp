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
<div class="container text-center mt-5">

    <div class="form-box text-center" align="center">
        <h3> Vyber súbor </h3>

        <form name="fm" action="upload" method="post" id="crypto-form" enctype="multipart/form-data">
            <div class="input-group mt-2 mb-3">
                <div class="custom-file">
                    <input type="file" name="file" class="form-control" id="inputGroupFile01">
                    <label class="custom-file-label" for="inputGroupFile01">Vyber súbor</label>
                </div>
            </div>
            <label class="custom-input-label" for="key" id="key-label">Šifrovací kľúč</label>
            <div class="input-group input-group-sm mb-3">
                <input type="text" class="form-control" id="key" name="key" placeholder="Zadaj kľúč">
            </div>
            <div class="input-group input-group-sm mb-3 text-center only-enc-visible">
                <a id="enable-generate" href="#">Vygenerovať kľúč</a>
                <div class="generate-key-div d-none">

                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <label class="input-group-text" for="key-bits">Dĺžka v bitoch</label>
                        </div>
                        <select class="custom-select" id="key-bits">
                            <option value="128" selected>128</option>
                            <option value="192">192</option>
                            <option value="256">256</option>
                        </select>

                    </div>
                    <div class="input-group input-group-sm mt-3 mb-2">
                        <button class="btn btn-primary generate-key-btn">Generuj</button>
                    </div>

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

    $('#crypto-form').on('submit', function (e) {
       if (![128/8,192/8,256/8].includes($('#key').val().length)) {
           e.stopPropagation();
           e.preventDefault();
           alert('Dĺžka kľúča nesedí');
       }
    });

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
