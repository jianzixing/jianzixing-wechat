Ext.define('App.system.RobotsWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 230,
    width: 400,
    layout: 'fit',
    title: '添加新闻分组',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            items: [
                {
                    xtype: 'hiddenfield',
                    name: 'id',
                    anchor: '100%'
                },
                {
                    xtype: 'combobox',
                    name: 'cmd',
                    anchor: '100%',
                    fieldLabel: 'Robots指令',
                    allowBlank: false,
                    displayField: 'name',
                    valueField: 'name',
                    store: {
                        data: [
                            {name: 'User-agent'},
                            {name: 'Disallow'},
                            {name: 'Allow'},
                            {name: 'Sitemap'},
                            {name: 'Crawl-delay'}
                        ]
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'value',
                    anchor: '100%',
                    fieldLabel: 'Robots条件',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'pos',
                    anchor: '100%',
                    fieldLabel: '排序',
                    inputType: 'number',
                    allowBlank: false
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

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        if (form.isValid()) {
            var data = form.getValues();
            if (this._data) {
                data['id'] = this._data['id'];
                this.apis.Robots.updateRobots
                    .wait(self, '正在保存Robots...')
                    .call({object: data}, function () {
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                this.apis.Robots.addRobots
                    .wait(self, '正在保存Robots...')
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

    setValue: function (value) {
        this._data = value;
        this.setTitle('修改新闻分组');
        this.find('form').getForm().setValues(value);
    }

});