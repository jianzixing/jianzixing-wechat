Ext.define('App.wechat.WeChatMassWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.ComboBox',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 400,
    width: 600,
    layout: 'fit',
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
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '群发名称'
                },
                {
                    xtype: 'combobox',
                    name: 'type',
                    anchor: '100%',
                    editable: false,
                    fieldLabel: '群发类型',
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
                    },
                    listeners: {
                        change: 'onTimerTypeChange'
                    }
                },
                {
                    xtype: 'combobox',
                    name: 'tagid',
                    anchor: '100%',
                    editable: false,
                    fieldLabel: '用户标签',
                    displayField: 'name',
                    valueField: 'id'
                },
                {
                    xtype: 'datetimefield',
                    name: 'triggerTime',
                    anchor: '100%',
                    format: 'Y-m-d H:i:s',
                    fieldLabel: '触发时间'
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
                    fieldLabel: '回复素材',
                    listeners: {
                        click: 'onMediaSelectClick'
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
                    text: '保存定时群发',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消定时群发',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            data['openType'] = this.openType;
            data['accountId'] = this.accountData['id'];
            if (this._data) {
                data['id'] = this._data['id'];
                this.apis.WeChatMass.updateMass.call({object: data}, function () {
                    self.close();
                    if (self._callback) {
                        self._callback();
                    }
                });
            } else {
                this.apis.WeChatMass.addMass.call({object: data}, function () {
                    self.close();
                    if (self._callback) {
                        self._callback();
                    }
                });
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onMediaSelectClick: function () {
        var self = this;
        var type = self.find('type').getValue();
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
                        names.push(data[i]['title']);
                        break;
                    }
                    self.find('mediaId').setValue(ids);
                    self.find('mediaId').containerField.setHtml('<span style="color:#666666;font-weight: bold">' + names.join(' , ') + '</span>');
                }
            });
            win.setInit();
        } else {
            var win = Dialog.openWindow('App.wechat.WeChatMaterialSelector', {
                apis: this.apis,
                openType: this.openType,
                accountData: this.accountData,
                materialType: type,
                _callback: function (data) {
                    var ids = [];
                    var names = [];
                    for (var i = 0; i < data.length; i++) {
                        ids.push(data[i]['id']);
                        names.push(data[i]['name']);
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

    onTimerTypeChange: function (field, newValue, oldValue, eOpts) {
        if (newValue == 2) {
            this.find('text').show();
            this.find('mediaId').hide();
        } else {
            this.find('text').hide();
            this.find('mediaId').show();
        }
    },

    setInit: function (params) {
        var tagidVeiw = this.find('tagid');
        var store = this.apis.WeChatMass.getWeChatLabels.createListStore(params);
        tagidVeiw.setStore(store);
    },

    setValue: function (data) {
        this._data = data;
        var tagidVeiw = this.find('tagid');
        this.setTitle('修改定时群发');
        tagidVeiw.getStore().load();
        var form = this.find('form').getForm();
        form.setValues(data);
    }

});
