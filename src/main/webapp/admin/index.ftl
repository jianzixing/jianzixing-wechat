<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta charset="UTF-8">
    <meta name="referrer" content="never">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Admin</title>

    <script type="text/javascript" src="lib/extjs/ext-all.js"></script>
    <#--<script type="text/javascript" src="lib/extjs/ext-all-debug.js"></script>-->
    <script type="text/javascript" src="lib/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="lib/extjs/classic/theme-triton/theme-triton.js"></script>
    <script type="text/javascript" charset="utf-8" src="lib/ckeditor/ckeditor.js"></script>

    <link rel="stylesheet" href="lib/extjs/classic/theme-triton/resources/theme-triton-all.css"/>
    <link rel="stylesheet" href="css/extend.css"/>

    <script type="text/javascript" src="app/base.min.js"></script>
    <script type="text/javascript" src="app/app.js"></script>
</head>
<body>
<input type="hidden" id="login_user_name" value="${(user['userName'])!''}"/>
<input type="hidden" id="login_user_real_name" value="${(user['realName'])!''}"/>
<input type="hidden" id="login_user_logo" value="${(user['logo'])!''}"/>
<script type="text/javascript">
    if (navigator.serviceWorker != null) {
        navigator.serviceWorker.register("service-worker.js")
            .then(function (value) {
                console.log("Registered events at scope:", value.scope)
            })
    }
</script>
</body>
</html>
