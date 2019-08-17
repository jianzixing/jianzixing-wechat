Ext.define('App.refund.RefundOrderWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Display'
    ],

    autoShow: true,
    height: 550,
    width: 800,
    layout: 'fit',
    title: '退款单详情',

    items: [
        {
            xtype: 'form',
            autoScroll: true,
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'displayfield',
                    name: 'userName',
                    anchor: '100%',
                    fieldLabel: '用户名',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'from',
                    anchor: '100%',
                    fieldLabel: '来源',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'auditStatus',
                    anchor: '100%',
                    fieldLabel: '审核状态',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'type',
                    anchor: '100%',
                    fieldLabel: '退款类型',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'status',
                    anchor: '100%',
                    fieldLabel: '退款状态',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'money',
                    anchor: '100%',
                    fieldLabel: '退款金额',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'sourceMoney',
                    anchor: '100%',
                    fieldLabel: '原订单金额',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'chargeMoney',
                    anchor: '100%',
                    fieldLabel: '扣款金额',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'detail',
                    anchor: '100%',
                    fieldLabel: '退款原因',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'remark',
                    anchor: '100%',
                    fieldLabel: '客服备注',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'log',
                    anchor: '100%',
                    fieldLabel: '退款服务日志',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'refundTime',
                    anchor: '100%',
                    fieldLabel: '退款时间',
                    value: '[空]'
                },
                {
                    xtype: 'displayfield',
                    name: 'createTime',
                    anchor: '100%',
                    fieldLabel: '创建时间',
                    value: '[空]'
                }
            ]
        }
    ],

    setValue: function (data) {
        if (data) {
            this.find('userName').setValue(this.mp.getRendererResult.userName(data['TableUser']));
            this.find('from').setValue(this.mp.getRendererResult.from(data['from']));
            this.find('auditStatus').setValue(this.mp.getRendererResult.auditStatus(data['auditStatus']));
            this.find('type').setValue(this.mp.getRendererResult.type(data['type']));
            this.find('status').setValue(this.mp.getRendererResult.status(data['status']));
            this.find('money').setValue(this.mp.getRendererResult.money(data['money']));
            this.find('sourceMoney').setValue(this.mp.getRendererResult.money(data['sourceMoney']));
            this.find('chargeMoney').setValue(this.mp.getRendererResult.money(data['chargeMoney']));
            this.find('detail').setValue(data['detail']);
            this.find('remark').setValue(data['remark']);
            this.find('log').setValue(data['log']);
            this.find('refundTime').setValue(this.mp.getRendererResult.time(data['refundTime']));
            this.find('createTime').setValue(this.mp.getRendererResult.time(data['createTime']));
        }
    }

});