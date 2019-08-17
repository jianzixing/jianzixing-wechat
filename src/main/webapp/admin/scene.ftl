<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Admin</title>

    <#--<script type="text/javascript" src="lib/extjs/ext-all.js"></script>-->
    <script type="text/javascript" src="lib/extjs/ext-all-debug.js"></script>
    <script type="text/javascript" src="lib/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="lib/extjs/classic/theme-triton/theme-triton.js"></script>
    <script type="text/javascript" charset="utf-8" src="lib/ckeditor/ckeditor.js"></script>

    <link rel="stylesheet" href="lib/extjs/classic/theme-triton/resources/theme-triton-all.css"/>
    <link rel="stylesheet" href="css/extend.css"/>
    <script type="text/javascript" src="app/base.min.js"></script>

    <script src="lib/codemirror/codemirror.js"></script>
    <script src="lib/codemirror/mode/htmlembedded/htmlembedded.js"></script>
    <script src="lib/codemirror/mode/htmlmixed/htmlmixed.js"></script>
    <script src="lib/codemirror/mode/xml/xml.js"></script>
    <script src="lib/codemirror/mode/javascript/javascript.js"></script>
    <script src="lib/codemirror/addon/mode/multiplex.js"></script>
    <script src="lib/codemirror/mode/css/css.js"></script>
    <link rel="stylesheet" href="lib/codemirror/codemirror.css">

</head>
<body>
</body>

<script type="text/javascript">
    Ext.Loader.setPath('App', 'app/view');
    Ext.Loader.setPath('UXApp', 'app/ux');
    Ext.Loader.setPath('AppRoot', 'app');

    Ext.onReady(function () {

        Ext.define('Ext.zc.form.HtmlEditorImage', {
            extend: 'Ext.util.Observable',
            alias: 'widget.zc_form_HtmlEditorImage',
            langTitle: '插入图片',
            langIconCls: 'heditImgIcon',

            init: function (view) {
                var self = this;
                view.on('render', function () {
                    self.onRender(view);
                });
            },

            /**
             * 添加"插入图片"按钮
             * */
            onRender: function (view) {
                var self = this;
                var toolbar = view.getToolbar();
                toolbar.add({
                    iconCls: self.langIconCls,
                    text: 'ac',
                    tooltip: {
                        title: self.langTitle,
                        width: 60
                    },
                    handler: function () {
                        self.showImgWindow(view);
                    }
                });
            }
        });

        Ext.create('Ext.form.HtmlEditor', {
            width: 800,
            height: 380,
            plugins: [
                Ext.create('Ext.zc.form.HtmlEditorImage')
            ],
            renderTo: Ext.getBody()
        });
    });
</script>
</html>
