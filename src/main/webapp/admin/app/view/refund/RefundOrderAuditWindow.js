Ext.define('App.refund.RefundOrderAuditWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Display',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 300,
    width: 500,
    layout: 'fit',
    title: '审核退款单',
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
                    name: 'id',
                    anchor: '100%',
                    fieldLabel: 'ID'
                },
                {
                    xtype: 'displayfield',
                    name: 'tips',
                    anchor: '100%',
                    fieldLabel: '审核提示',
                    value: 'Display Field'
                },
                {
                    xtype: 'textareafield',
                    name: 'remark',
                    anchor: '100%',
                    height: 100,
                    allowBlank: false,
                    fieldLabel: '退款单备注'
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
                    name: 'save_btn',
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
            if (this._type) {
                this.apis.RefundOrder.setRefundPass
                    .wait(this, '正在通过审核...')
                    .call(data, function () {
                        self.close();
                        if (self._callback) self._callback();
                    });
            } else {
                this.apis.RefundOrder.setRefundReject
                    .wait(this, '正在拒绝审核...')
                    .call(data, function () {
                        self.close();
                        if (self._callback) self._callback();
                    })
            }
        }
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (data, isYes) {
        this._data = data;
        this.find('id').setValue(data['id']);
        this._type = isYes;
        if (isYes) {
            this.setTitle('退款单审核通过');
            this.find('tips').setValue('<span style="color: #0f74a8">审核通过后需要手动退款</span>');
            this.find('save_btn').setText('通过审核');
        } else {
            this.setTitle('退款单审核不通过');
            this.find('tips').setValue('<span style="color: red">审核不通过请联系用户协商</span>');
            this.find('save_btn').setText('拒绝审核');
        }
    }
});