Ext.define('App.cooperation.FriendLinkWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.Text'
    ],

    height: 260,
    width: 480,
    layout: 'fit',
    title: '编辑友情链接',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消',
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
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'hiddenfield',
                    name: 'id',
                    anchor: '100%'
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    anchor: '60%',
                    fieldLabel: '名称',
                    labelWidth: 100
                },
                {
                    xtype: 'textfield',
                    name: 'link',
                    anchor: '100%',
                    fieldLabel: '链接地址',
                    regex: /^(https?):[\s\S]*$/,
                    labelWidth: 100,
                    listeners: {
                        change: 'onTextfieldChange'
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'order',
                    anchor: '50%',
                    fieldLabel: '排序',
                    regex: /[0-9]{1,3}$/,
                    labelWidth: 100,
                    listeners: {
                        change: 'onTextfieldChange'
                    }
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            if (this._data) {
                data['id'] = this._data['id'];
                self.apis.FriendLink.updateFriendLink
                    .wait(self, '正在修改友情链接...')
                    .call({object: data}, function () {
                    if (self._callback) {
                        self._callback();
                    }
                });
            } else {
                self.apis.FriendLink.addFriendLink
                    .wait(self, '正在添加友情链接...')
                    .call({object: data}, function () {
                    if (self._callback) {
                        self._callback();
                    }
                });
            }
            self.close();
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onTextfieldChange: function (field, newValue, oldValue, eOpts) {

    },

    setValues: function (data) {
        this._data = data;
        var form = this.find('form').getForm();
        form.setValues(data);
    }

});