Ext.define('App.file.UploadWindow', {
    extend: 'Ext.window.Window',

    height: 450,
    width: 630,
    layout: {
        type: 'fit'
    },
    title: '上传文件',
    plain: true,
    modal: true,
    constrainHeader: true,
    defaultListenerScope: true,
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                "->",
                {
                    xtype: 'button',
                    name: 'upload',
                    text: '上传并刷新',
                    icon: Resource.png('jet', 'upload'),
                    listeners: {
                        click: 'onButtonClick'
                    }
                }
            ]
        },
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                "->",
                {
                    xtype: 'button',
                    text: '选择文件',
                    icon: Resource.png('jet', 'upload'),
                    listeners: {
                        click: 'onSelectClick'
                    }
                }
            ]
        }
    ],

    items: [
        {
            xtype: 'form',
            layout: 'column',
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'container',
                    name: 'container',
                    html: '<input type="file" style="display: none" name="file" multiple="multiple" value="选择文件..."/>',
                },
                {
                    xtype: 'container',
                    name: 'files',
                    html: ''
                }
            ]
        }
    ],

    onSelectClick: function (button, e, options) {
        var self = this;
        var ctr = this.find('container');
        var filesCtr = this.find('files');
        var el = ctr.el.dom;
        var input = el.querySelector("input");
        input.onchange = function () {
            var files = input.files;
            self.updateUploadInfo(filesCtr, files, true);
        };

        if (self.singleFile) {
            var inputNode = ctr.el.select("input");
            inputNode.set({multiple: undefined});
        }

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

    onButtonClick: function (button, e, options) {
        var self = this;
        var ctr = this.find('container');
        var filesCtr = this.find('files');
        var button = this.find('upload');
        var el = ctr.el.dom;
        var input = el.querySelector("input");

        var fun = function (files, count) {
            if (files[count] && files[count]['name']) {
                var rc = new RequestContainer('/admin/file/upload_file.action');
                var xmlHttp = rc._xmlHttp;
                rc.append(files[count]['name'], files[count]);
                rc.append("gid", self.groupId);
                if (self.canDownload && self.canDownload == "false") {
                    rc.append("canDownload", 0);
                }
                rc.addUploadProgressListener(function (evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = Math.round(evt.loaded * 100 / evt.total);
                        files[count]['progressNumber'] = percentComplete;
                        // document.getElementById('progressNumber').innerHTML = percentComplete.toString() + '%';
                    } else {
                        files[count]['progressNumber'] = -1;
                        // document.getElementById('progressNumber').innerHTML = 'unable to compute';
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

                rc.addReadyStateChangeListener(function (resp) {
                    if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                        files[count]['progressNumber'] = 100;
                        self.updateUploadInfo(filesCtr, files);
                        count++;
                        if (count == files.length) {
                            Ext.Msg.alert('上传成功', '文件上传成功.');
                            self.close();
                            if (self.refreshImages) {
                                self.refreshImages();
                            }

                            if (self._callback) {
                                try {
                                    var text = xmlHttp.response;
                                    self._callback(JSON.parse(text));
                                } catch (e) {
                                    console.error(e);
                                }
                            }
                        } else {
                            fun(files, count);
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
        };

        var files = input.files;
        var fs = [];
        if (files) {
            for (var i = 0; i < files.length; i++) {
                if (files[i]['name']) {
                    fs.push(files[i]);
                }
            }
            button.disable();
            fun(fs, 0)
        }
    }
});
