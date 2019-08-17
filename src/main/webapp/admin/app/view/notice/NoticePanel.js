Ext.define('App.notice.NoticePanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.TextArea'
    ],

    border: false,
    height: 666,
    width: 726,
    layout: 'fit',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'back'),
                    text: '返回列表',
                    listeners: {
                        click: 'onBackClick'
                    }
                },
                '-',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                }
            ]
        }
    ],
    items: [
        {
            xtype: 'form',
            name: 'form',
            autoScroll: true,
            border: false,
            layout: 'form',
            bodyPadding: 20,
            items: [
                {
                    xtype: 'hiddenfield',
                    name: 'id',
                    anchor: '100%'
                },
                {
                    xtype: 'textfield',
                    name: 'title',
                    width: 500,
                    fieldLabel: '标题'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '是否置顶',
                    items: [
                        {boxLabel: '否', name: 'up', inputValue: '0', checked: true, width: "100"},
                        {boxLabel: '是', name: 'up', inputValue: '1'}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    name: 'content',
                    style: {width: '100%'},
                    fieldLabel: '编辑内容',
                    listeners: {
                        resize: 'onFieldResizeClick'
                    }
                }
            ]
        }
    ],

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
    },

    onResetClick: function (button, e, eOpts) {
        this.parent.redraw();
    },

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var content = this._ckEditor;
        var form = this.find('form');
        var data = form.getValues();
        data['content'] = content.getData();
        console.dir(data);
        Dialog.confirm('提示', '确定保存当前填写的内容？', function (btn) {
            if (form.isValid() && btn == 'yes') {
                if (parseInt(data['id']) > 0) {
                    self.apis.Notice.updateNotice
                        .wait(self, '正在修改...')
                        .call({object: data}, function () {
                            self.parent.back();
                            if (self._callback) {
                                self._callback();
                            }
                        })
                } else {
                    self.apis.Notice.addNotice
                        .wait(self, '正在添加...')
                        .call({object: data}, function () {
                            self.parent.back();
                            if (self._callback) {
                                self._callback();
                            }
                        })
                }
            }
        });
    },
    onAfterApply: function () {
        var cnt = this.find('content');
        var cel = document.getElementById(cnt.id + "-outerCt");
        cel.style.width = "100%";
        var id = this.id + "_ck_content";
        cnt.setHtml("<div style='overflow:hidden;' id='" + id + "'></div>");
        this._ckEditor = CKEDITOR.replace(id, {
            sysimageCallback: function (editor) {
                self.openFileWindow(editor);
            }
        });
    },
    onReturnClick: function (button, e, eOpts) {
        this.parent.back();
    },

    openFileWindow: function (editor) {
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            editor.insertHtml("<img src=\"" + Resource.image(files.fileName) + "\" style='width: 300px'/>");
        }, true);
    },

    setValue: function (data) {
        this._data = data;
        this.find('form').getForm().setValues(data);
        if (this._ckEditor) {
            this._ckEditor.setData(data['content'])
        }
    }
});