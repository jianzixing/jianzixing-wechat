Ext.define('App.wechat.WeChatAccountReplyWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.form.field.Display',
        'Ext.form.field.TextArea',
        'Ext.form.FieldContainer',
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.view.Table',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 600,
    width: 900,
    layout: 'fit',
    title: '添加自动回复',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            scrollable: 'y',
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'displayfield',
                    name: 'curr_public',
                    anchor: '100%',
                    fieldLabel: '当前公众号',
                    value: ''
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '回复名称'
                },
                {
                    xtype: 'combobox',
                    name: 'type',
                    anchor: '100%',
                    displayField: 'name',
                    valueField: 'id',
                    fieldLabel: '触发回复',
                    listeners: {
                        change: 'onTypeChange'
                    },
                    editable: false,
                    store: {
                        data: [
                            {id: 1, name: '全匹配关键字'},
                            {id: 2, name: '半匹配关键字'},
                            {id: 5, name: '关注公众号'},
                            {id: 7, name: '扫描带参数二维码'},
                            {id: 8, name: '自定义菜单事件'}
                        ]
                    }
                },
                {
                    xtype: 'combobox',
                    name: 'replyType',
                    anchor: '100%',
                    fieldLabel: '回复类型',
                    listeners: {
                        change: 'onReplyTypeChange'
                    },
                    displayField: 'name',
                    valueField: 'id',
                    editable: false,
                    store: {
                        data: [
                            {id: 1, name: '图文消息'},
                            {id: 2, name: '文字'},
                            {id: 3, name: '图片'},
                            {id: 4, name: '语音'},
                            {id: 5, name: '视频'}
                        ]
                    }
                },
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'value',
                    anchor: '100%',
                    fieldLabel: '关键字'
                },
                {
                    xtype: 'textareafield',
                    hidden: true,
                    name: 'text',
                    anchor: '100%',
                    fieldLabel: '回复内容'
                },
                {
                    xtype: 'choosefield',
                    hidden: true,
                    name: 'mediaId',
                    anchor: '100%',
                    fieldLabel: '回复素材(ID或mediaId)',
                    listeners: {
                        click: 'onMediaSelectClick'
                    }
                },
                {
                    xtype: 'textfield',
                    hidden: true,
                    name: 'pos',
                    anchor: '100%',
                    fieldLabel: '排序(匹配最小)'
                },
                {
                    xtype: 'textareafield',
                    name: 'detail',
                    anchor: '100%',
                    fieldLabel: '描述'
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

    onSaveClick: function () {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            data['openType'] = this.openType;
            data['accountId'] = this.accountData['id'];
            data['mediaId'] = data['mediaId'][0];
            if (this._data) {
                data['id'] = this._data['id'];
                this.apis.WeChatReply.updateReply
                    .wait(this, '正在更新回复...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                this.apis.WeChatReply.addReply
                    .wait(this, '正在添加回复...')
                    .call({object: data}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    });
            }
        }
    },

    onCancelClick: function () {
        this.close();
    },

    onTypeChange: function (field, newValue, oldValue, eOpts) {
        var type = newValue;
        this.find('replyType').setValue(null);

        this.find('value').hide();
        if (type == 1 || type == 2 || type == 7 || type == 8) {
            this.find('value').show();
            if (type == 7) this.find('value').setFieldLabel('场景号');
            if (type == 8) this.find('value').setFieldLabel('菜单名称');
            if (type == 1) this.find('value').setFieldLabel('关键字');
        }

        this.find('text').hide();
        this.find('mediaId').hide();
    },

    onReplyTypeChange: function (field, newValue, oldValue, eOpts) {
        if (newValue == 2) {
            this.find('text').show();
            this.find('mediaId').hide();
        } else {
            this.find('text').hide();
            this.find('mediaId').show();
        }
    },

    onMediaSelectClick: function () {
        var self = this;
        var type = self.find('replyType').getValue();
        if (type == 1) {
            var win = Dialog.openWindow('App.wechat.WeChatImageTextSelector', {
                apis: this.apis,
                accountData: this.accountData,
                openType: this.openType,
                _callback: function (data) {
                    var ids = [];
                    var names = [];
                    for (var i = 0; i < data.length; i++) {
                        ids.push(data[i]['id']);
                        names.push(data[i]['id']);
                        break;
                    }
                    self.find('mediaId').setValue(ids ? ids[0] : null);
                    self.find('mediaId').containerField.setHtml('<span style="color:#666666;font-weight: bold">' + names.join(' , ') + '</span>');
                }
            });
            win.setInit();
        } else {
            var win = Dialog.openWindow('App.wechat.WeChatMaterialSelector', {
                apis: this.apis,
                accountData: this.accountData,
                openType: this.openType,
                materialType: type,
                _callback: function (data) {
                    var ids = [];
                    var names = [];
                    for (var i = 0; i < data.length; i++) {
                        ids.push(data[i]['id']);
                        names.push(data[i]['id']);
                        break;
                    }
                    self.find('mediaId').setValue(ids ? ids[0] : null);
                    self.find('mediaId').containerField.setHtml('<span style="color:#666666;font-weight: bold">' + names.join(' , ') + '</span>');
                }
            });
            win.setMode('SINGLE');
            win.setInit();
        }
    },

    setInit: function () {
        if (this.accountData) {
            var text = this.accountData['name'];
            if (this.accountData['weAccount']) {
                text += " (" + this.accountData['weAccount'] + ")";
            }
            this.find('curr_public').setValue(text);
        }
    },

    setValue: function (data) {
        var form = this.find('form');
        form.getForm().setValues(data);
        this.setTitle('修改自动回复');
        this._data = data;
    }

});
