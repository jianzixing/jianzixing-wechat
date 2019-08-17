Ext.define('App.EditPasswordWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 240,
    width: 400,
    layout: 'fit',
    title: '修改当前用户密码',
    defaultListenerScope: true,
    apis: {
        Admin: {
            editSelfPassword: {}
        }
    },

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
                    name: 'oldPassword',
                    allowBlank: false,
                    anchor: '100%',
                    fieldLabel: '请输入原密码',
                    inputType: 'password'
                },
                {
                    xtype: 'textfield',
                    name: 'newPassword',
                    allowBlank: false,
                    anchor: '100%',
                    fieldLabel: '请输入新密码',
                    inputType: 'password'
                },
                {
                    xtype: 'textfield',
                    name: 'rePassword',
                    allowBlank: false,
                    anchor: '100%',
                    fieldLabel: '重复输入新密码',
                    inputType: 'password'
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
                    text: '确定修改',
                    icon: Resource.png('jet', 'inspectionsOK'),
                    listeners: {
                        click: 'onOkClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '取消修改',
                    icon: Resource.png('jet', 'closeActive'),
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],

    onOkClick: function (button, e, eOpts) {
        var self = this;
        var value = self.find('form').getForm().getValues();
        if (value['newPassword'] != value['rePassword']) {
            Dialog.alert('新密码两次输入的不一致');
            return false;
        }
        if (this.find('form').getForm().isValid()) {
            Dialog.confirm('提示', '确定修改当前密码？', function (btn) {
                if (btn == 'yes') {
                    self.apis.Admin.editSelfPassword.call({
                        oldPassword: value['oldPassword'],
                        newPassword: value['newPassword']
                    }, function () {
                        Dialog.alert('修改成功!');
                        self.close();
                        if (self.callback) {
                            self.callback()
                        }
                    })
                }
            })
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    }

});