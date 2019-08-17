Ext.define('App.message.SystemMessageWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.FieldContainer',
        'Ext.form.field.TextArea',
        'UXApp.editor.CKEditor'
    ],

    height: 700,
    width: 800,
    layout: 'fit',
    title: '发送系统消息',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'send'),
                    text: '确定发送',
                    listeners: {
                        click: 'onSendClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消发送',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],
    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'fieldcontainer',
                    name: 'toAdminIds',
                    fieldLabel: '收信人'
                },
                {
                    xtype: 'combobox',
                    name: 'type',
                    anchor: '100%',
                    fieldLabel: '消息类型',
                    displayField: 'name',
                    valueField: 'value',
                    queryMode: 'remote',
                    editable: false
                },
                {
                    xtype: 'textfield',
                    name: 'title',
                    anchor: '100%',
                    fieldLabel: '消息标题'
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    fieldLabel: '消息内容',
                    style: {width: '100%'},
                    items: [
                        {
                            xtype: 'ckeditor',
                            height: 500,
                            name: 'content'
                        }
                    ]
                }
            ]
        }
    ],

    onSendClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            data['content'] = this.find('content').getValue();
            if (this._admins) {
                var ids = Array.splitArray(this._admins, "id");
                self.apis.SystemMessage.sendMessage
                    .wait(self, '正在发送...')
                    .call({object: data, ids: ids}, function () {
                        self.close();
                        if (self._sendSelects) {
                            self._sendSelects();
                        }
                    });
            } else {
                if (this._sendAll) {
                    self.apis.SystemMessage.sendAllMessage
                        .wait(self, '正在发送...')
                        .call({object: data}, function () {
                            self.close();
                            if (self._sendAll) {
                                self._sendAll();
                            }
                        });
                }
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onShow: function () {
        this.callParent(arguments);
        var store = this.apis.SystemDict.getDictsByTable.createListStore({
            tableName: 'TableSystemMessage',
            field: 'type'
        });
        this.find('type').setStore(store);
        store.load();
    },

    setToAdmins: function (datas) {
        if (datas) {
            var userNames = [];
            for (var i = 0; i < datas.length; i++) {
                userNames.push(datas[i]['userName']);
            }
            this.find('toAdminIds').setHtml(userNames.join(" , "));
            this._admins = datas;
        }
    },

    setToAll: function () {
        this.find('toAdminIds').setHtml('<span style="color: #5bc794">所有人</span>');
        this._sendAll = true;
    }

});