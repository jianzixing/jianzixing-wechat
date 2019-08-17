Ext.define('App.spcard.ShoppingCardWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Number',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 450,
    width: 600,
    layout: 'fit',
    title: '创建购物卡批次',
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
                    fieldLabel: '批次名称',
                    allowBlank: false
                },
                {
                    xtype: 'numberfield',
                    name: 'money',
                    anchor: '100%',
                    fieldLabel: '面额',
                    allowBlank: false
                },
                {
                    xtype: 'numberfield',
                    name: 'count',
                    anchor: '100%',
                    fieldLabel: '数量',
                    allowBlank: false
                },
                {
                    xtype: 'displayfield',
                    anchor: '100%',
                    fieldLabel: '注意',
                    value: '<span style="color: #999999">必须设置密码，批次密码在以后操作中经常使用，亲您牢记您的密码!</span>'
                },
                {
                    xtype: 'textfield',
                    name: 'password',
                    anchor: '100%',
                    fieldLabel: '密码',
                    allowBlank: false
                },
                {
                    xtype: 'datefield',
                    name: 'finishTime',
                    anchor: '100%',
                    fieldLabel: '截止有效期',
                    format: 'Y-m-d H:i:s',
                    allowBlank: false
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

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            if (this._data) {
                data['id'] = this._data['id'];
                this.apis.ShoppingCard.updateShoppingCard
                    .wait(this, '正在修改批次...')
                    .call({object: data}, function () {
                        self.close();
                        if (self.callback) self.callback();
                    })
            } else {
                this.apis.ShoppingCard.addShoppingCard
                    .wait(this, '正在添加批次...')
                    .call({object: data}, function () {
                        self.close();
                        if (self.callback) self.callback();
                    })
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (data) {
        this._data = data;
        this.find('password').allowBlank = true;
        var form = this.find('form').getForm();
        form.setValues(data);
        this.find('password').setValue('');
        this.find('finishTime').setValue(new Date(data['finishTime']));
        this.find('password').setEmptyText('如果您不填写则不会更改批次密码');
        this.setTitle('修改购物卡批次');
    }

});