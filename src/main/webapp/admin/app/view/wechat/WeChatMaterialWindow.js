Ext.define('App.wechat.WeChatMaterialWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Display',
        'Ext.form.FieldContainer',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 350,
    width: 500,
    layout: 'fit',
    title: '',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'displayfield',
                    name: 'label',
                    anchor: '100%',
                    fieldLabel: '提示',
                    value: '上传说明'
                },
                {
                    xtype: 'fieldcontainer',
                    fieldLabel: '选择文件',
                    items: [
                        {
                            xtype: 'button',
                            text: '点击上传文件',
                            listeners: {
                                click: 'onUploadButtonClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    hidden: true,
                    name: 'select_files',
                    fieldLabel: '&nbsp;',
                    html: '<input type="file" style="display: none" name="file" multiple="multiple" value="选择文件..."/>',
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'show_files',
                    height: 120,
                    fieldLabel: '&nbsp;',
                    html: '',
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    text: '确定上传',
                    icon: Resource.png('jet', 'upFolder'),
                    listeners: {
                        click: 'onOKClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '取消关闭',
                    icon: Resource.png('jet', 'cancel'),
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onOKClick: function (button, e, eOpts) {
        var self = this;
        var cmp = this.find('select_files');
        var el = cmp.el.dom;
        var filesCtr = this.find('show_files');
        var input = el.querySelector("input");
        var files = input.files;
        var fileLength = files.length;

        for (var i = 0; i < fileLength; i++) {
            var count = 0;
            var rc = new RequestContainer('/admin/WeChatMaterial/uploadFile.action');
            var xmlHttp = rc._xmlHttp;
            rc.append("file", files[count]);
            rc.addUploadProgressListener(function (evt) {
                if (evt.lengthComputable) {
                    var percentComplete = Math.round(evt.loaded * 100 / evt.total);
                    files[count]['progressNumber'] = percentComplete;
                } else {
                    files[count]['progressNumber'] = -1;
                }
                self.updateUploadInfo(filesCtr, files);
            });

            rc.addUploadErrorListener(function (evt) {
                Ext.MessageBox.show({
                    title: "上传失败",
                    msg: '文件上传失败:' + files[count].name + ",可能是文件过大(最大100M)也可能是网络原因！",
                    icon: Ext.MessageBox.WARNING
                });
                button.enable();
            });

            rc.addReadyStateChangeListener(function () {
                if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                    files[count]['progressNumber'] = 100;
                    self.updateUploadInfo(filesCtr, files);
                    count++;
                    if (count == files.length) {
                        Ext.Msg.alert('上传成功', '文件上传成功.');
                        self.close();
                        self.refreshImages();
                    } else {
                        Ext.MessageBox.show({
                            title: "上传失败",
                            msg: '文件上传失败:' + files[count].name,
                            icon: Ext.MessageBox.WARNING
                        });
                        button.enable();
                    }
                } else if (xmlHttp.status != 200 && xmlHttp.status != 0) {
                    Ext.MessageBox.show({
                        title: "上传失败",
                        msg: '文件上传失败:' + files[count].name,
                        icon: Ext.MessageBox.WARNING
                    });
                    button.enable();
                }
            });
            rc.submit();
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onUploadButtonClick: function () {
        var self = this;
        var cmp = this.find('select_files');
        var el = cmp.el.dom;
        var input = el.querySelector("input");
        var filesCtr = this.find('show_files');

        input.onchange = function () {
            var files = input.files;
            self.updateUploadInfo(filesCtr, files, true);
        };

        if (document.all) {
            input.click();
        } else {
            var e = document.createEvent("MouseEvents");
            e.initEvent("click", true, true);
            input.dispatchEvent(e);
        }
    },

    updateUploadInfo: function (filesCtr, files, msg) {
        var text = [];
        if (files) {
            for (var i in files) {
                if (files[i]['size']) {
                    var s = "";
                    var pn = files[i]['progressNumber'];
                    if (pn) {
                        if (pn == -1) {
                            s = "上传进度：无法计算"
                        } else {
                            s = "上传进度：" + pn + "%"
                        }
                    }
                    var size = parseInt("" + (files[i]['size'] / 1000));
                    if (parseInt("" + size / 1000) > 100 && msg) {
                        Dialog.alert('文件太大可能导致无法上传');
                    }
                    text.push(
                        "类型：" + files[i]['type'] +
                        "&nbsp;&nbsp;&nbsp;大小：" + size + "Kb" +
                        "&nbsp;&nbsp;&nbsp;名称：" + files[i]['name'] +
                        "</br>" + s
                    );
                }
            }
        }
        filesCtr.setHtml(text.join("</br>"));
    },

    setForever: function (bool) {
        if (bool) {
            this.setTitle('添加永久素材');
        } else {
            this.setTitle('添加临时素材');
        }
        this.isForever = bool;
        if (bool) {
            this.find('label').setValue('公众号的素材库保存总数量有上限：图文消息素材、图片素材上限为5000，其他类型为1000。' +
                '支持图片(2M , PNG\\JPEG\\JPG\\GIF)、语言(2M , AMR\\MP3)、视频(10M , MP4)、缩略图(64KB , JPG)。' +
                '图文消息图片仅支持jpg/png格式，大小必须在1MB以下');
        } else {
            this.find('label').setValue('微信临时素材有效期为3天，支持图片(2M , PNG\\JPEG\\JPG\\GIF)、语言(2M , AMR\\MP3)、视频(10M , MP4)、缩略图(64KB , JPG)');
        }
    }

});