Ext.define('App.wechat.WeChatImageTextWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.Img',
        'Ext.button.Button',
        'Ext.form.field.Text',
        'Ext.form.field.HtmlEditor',
        'Ext.toolbar.Toolbar',
        'UXApp.editor.CKEditorField'
    ],

    height: 650,
    width: 1000,
    layout: 'fit',
    title: '添加自定义图文',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'hiddenfield',
                    name: 'coverUrl',
                },
                {
                    xtype: 'hiddenfield',
                    name: 'thumbMediaId'
                },
                {
                    xtype: 'fieldcontainer',
                    height: 120,
                    fieldLabel: '封面URL',
                    items: [
                        {
                            xtype: 'image',
                            name: 'image',
                            height: 100,
                            width: 100,
                            src: Resource.create('/admin/image/exicon/nopic_60.gif')
                        },
                        {
                            xtype: 'button',
                            margin: 30,
                            text: '选择图片',
                            listeners: {
                                click: 'openFileWindow'
                            }
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    name: 'title',
                    anchor: '100%',
                    fieldLabel: '标题',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'author',
                    anchor: '100%',
                    fieldLabel: '作者'
                },
                {
                    xtype: 'textfield',
                    name: 'desc',
                    anchor: '100%',
                    fieldLabel: '描述',
                    allowBlank: false
                },
                {
                    xtype: 'ckeditorfield',
                    name: 'content',
                    border: false,
                    fieldLabel: '图文内容',
                    allowBlank: false,
                    height: 400,
                    openFileWindow: function (editor) {
                        var self = this['ownerCt']['ownerCt'];
                        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
                        win.setSelectionCallback(function (files) {
                            self.apis.WeChatMaterial.uploadImageWeChat
                                .wait(self, '正在上传图片...')
                                .call({
                                    openType: self.openType,
                                    accountId: self.accountData['id'],
                                    fileName: files.fileName
                                }, function (data) {
                                    // editor.insertHtml("<img src=\"" + Resource.image(files.fileName) + "\"/>");
                                    editor.insertHtml("<img src=\"" + data + "\"/>");
                                });

                        }, true);
                    }
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
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '确定保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消关闭',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var form = this.find('form').getForm();
        var self = this;
        if (form.isValid()) {
            var values = form.getValues();
            console.log(values);

            values['resUrl'] = this.resUrl;
            var data = {
                openType: this.openType,
                accountId: this.accountData['id'],
                subs: [values]
            };

            if (this._data) {
                data['id'] = this._data['id'];
                values['url'] = this._data['TableWeChatImageTextSub'][0]['url'];
                this.apis.WeChatMaterial.updateImageText
                    .wait(this, '正在修改图文...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    })
            } else {
                this.apis.WeChatMaterial.addImageText
                    .wait(this, '正在添加图文...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    })
            }
        }
    },

    openFileWindow: function () {
        var self = this;
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            self.apis.WeChatMaterial.uploadMaterial
                .wait(self, '正在上传图片...')
                .call({
                    openType: self.openType,
                    accountId: self.accountData['id'],
                    fileName: files.fileName
                }, function (data) {
                    // self.find('image').setSrc(Resource.image(files.fileName));
                    // self.find('coverUrl').setValue(Resource.image(files.fileName));

                    self.find('image').setSrc(data['url']);
                    self.find('coverUrl').setValue(data['url']);
                    self.find('thumbMediaId').setValue(data['media_id']);
                });

        }, true);
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (data) {
        this._data = data;
        data = data['TableWeChatImageTextSub'][0];
        var form = this.find('form').getForm();
        form.setValues(data);
        this.find('image').setSrc(data['coverUrl']);
    }

});
