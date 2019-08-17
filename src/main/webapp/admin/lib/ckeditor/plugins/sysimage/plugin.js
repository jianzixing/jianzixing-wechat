CKEDITOR.plugins.add('sysimage', {
    icons: 'sysimage',
    init: function (editor) {
        editor.addCommand('openFileDialog', {
            exec: function (editor) {
                editor.config.sysimageCallback(editor);
                // var now = new Date();
                // editor.insertHtml('The current date and time is: <em>' + now.toString() + '</em>');
            }
        });
        editor.ui.addButton('sysimage', {
            label: '打开文件浏览器',
            command: 'openFileDialog',
            toolbar: 'insert'
        });
    }
});