Ext.define('App.comment.SuggestionListWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 600,
    width: 600,
    layout: 'fit',
    title: '建议详情',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            items: [
                {
                    xtype: 'textfield',
                    name: 'userName',
                    anchor: '100%',
                    fieldLabel: '用户',
                    // disabled: true,
                    editable: false
                },
                {
                    xtype: 'textfield',
                    name: 'qq',
                    anchor: '100%',
                    fieldLabel: 'QQ号码',
                    // disabled: true,
                    editable: false
                },
                {
                    xtype: 'textfield',
                    name: 'email',
                    anchor: '100%',
                    fieldLabel: '邮箱',
                    // disabled: true,
                    editable: false
                },
                {
                    xtype: 'textfield',
                    name: 'phone',
                    anchor: '100%',
                    fieldLabel: '手机号码',
                    // disabled: true,
                    editable: false
                },
                {
                    xtype: 'textfield',
                    name: 'typeName',
                    anchor: '100%',
                    fieldLabel: '意见类型',
                    // disabled: true,
                    editable: false
                },
                {
                    xtype: 'textfield',
                    name: 'ctime',
                    anchor: '100%',
                    fieldLabel: '创建时间',
                    // disabled: true,
                    editable: false
                },
                {
                    xtype: 'textareafield',
                    name: 'text',
                    fieldLabel: '建议内容',
                    // disabled: true,
                    anchor: '100%',
                    editable: false
                },
                {
                    xtype: 'textareafield',
                    name: 'reply',
                    height: 130,
                    fieldLabel: '回复内容<span style="color: red">*</span>',
                    anchor: '100%'
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
                    icon: Resource.png('jet', 'messageBean'),
                    text: '确定回复',
                    listeners: {
                        click: 'onReplyClick'
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

    onReplyClick: function (button, e, eOpts) {
        var self = this;
        var text = this.find('reply').getValue();
        if (text && text != '') {
            Dialog.confirm('提示', '确定回复当前意见吗？', function (btn) {
                if (btn == 'yes') {
                    self.apis.Suggestion.sendReply
                        .wait(self, '正在回复意见...')
                        .call({id: self._sid, reply: text}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    })
                }
            })
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (value) {
        this._data = value;
        this.find('form').getForm().setValues(value);
    }

});