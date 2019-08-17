Ext.define('App.discount.impl.MoneyOffDiscountView', {
    extend: 'Ext.container.Container',
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
                    xtype: 'button',
                    text: '添加满减区间规则',
                    listeners: {
                        click: 'onAddButton'
                    }
                },
                {
                    xtype: 'container',
                    name: 'views',
                    layout: 'vbox',
                    margin: '10 0 0 0',
                    items: []
                },
                {
                    xtype: 'container',
                    margin: '10 0 0 0',
                    items: [
                        {
                            xtype: 'checkboxfield',
                            name: 'hasLimit',
                            hideLabel: true,
                            inputValue: '1',
                            boxLabel: '上不封顶(勾选后按照最后一条满减记录递减,比如10减2、20减4、40减8, 依次类推)'
                        }
                    ]
                }
            ]
        }
    ],

    itemTpl: {
        xtype: 'container',
        margin: '10 0 0 0',
        layout: 'column',
        items: [
            {
                xtype: 'label',
                margin: '7 7 7 0',
                text: '消费满'
            },
            {
                xtype: 'numberfield',
                name: 'firstMoney',
                width: 100,
                hideLabel: true
            },
            {
                xtype: 'label',
                margin: '7 7 7 7',
                text: '元, 减'
            },
            {
                xtype: 'numberfield',
                name: 'subMoney',
                width: 100,
                hideLabel: true
            },
            {
                xtype: 'label',
                margin: '7 7 7 7',
                text: '元'
            },
            {
                xtype: 'button',
                margin: '0 0 0 15',
                text: '删除',
                listeners: {
                    click: 'onDelButton'
                }
            }
        ]
    },

    onAddButton: function () {
        this.find('views').add(this.itemTpl);
    },

    onDelButton: function (button) {
        button.ownerCt.ownerCt.remove(button.ownerCt);
    },

    onInitApply: function () {
        this.find('views').add(this.itemTpl);
    },

    getValue: function () {
        var form = this.find('form').getForm();
        var data = form.getValues();
        if (!Ext.isArray(data['firstMoney'])) {
            data['firstMoney'] = [data['firstMoney']];
        }
        if (!Ext.isArray(data['subMoney'])) {
            data['subMoney'] = [data['subMoney']];
        }
        return data;
    },

    getDetail: function (params) {
        var html = [];
        if (params['hasLimit'] && params['hasLimit'] == 1) {
            html.push(
                '<div style="width: 100%;padding: 8px 8px 8px 0px;margin-bottom: 10px">' +
                '<div style="float: left;width: 100%">已经使用<span style="color: #0f74a8;font-weight: bold;margin: 0 7px">上不封顶</span>设置</div>' +
                '</div>'
            )
        }

        if (params['firstMoney'] && params['subMoney']) {
            var len = params['firstMoney'].length;
            for (var i = 0; i < len; i++) {
                var fv = params['firstMoney'][i];
                var sv = params['subMoney'][i];
                html.push(
                    '<div style="width: 100%;padding: 8px 8px 8px 0px">' +
                    '<div style="float: left;width: 100%">消费满' +
                    '<span style="color: #0f74a8;font-weight: bold;margin: 0 7px">' + fv + '</span>元，' +
                    '减<span style="color: #0f74a8;font-weight: bold;margin: 0 7px">' + sv + '</span>元' + '</div>' +
                    '</div>'
                )
            }
        }
        return html.join('');
    }
});