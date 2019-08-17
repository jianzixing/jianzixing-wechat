Ext.define('App.recommend.RecommendGroupWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 240,
    width: 500,
    layout: 'fit',
    title: '添加推荐分组',
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
                    name: 'name',
                    anchor: '100%',
                    fieldLabel: '分组名称',
                    labelWidth: 150,
                    allowBlank: false
                },
                {
                    xtype: 'combobox',
                    name: 'type',
                    fieldLabel: '推荐类型',
                    labelWidth: 150,
                    anchor: '100%',
                    allowBlank: false,
                    displayField: 'name',
                    valueField: 'type',
                    editable: false,
                    store: {
                        data: [
                            {name: '链接地址', type: 0},
                            {name: '标准内容', type: 10},
                            {name: '新闻', type: 11},
                            {name: '产品', type: 12},
                            {name: '解决方案', type: 13},
                            {name: '文档', type: 14},
                            {name: '混合推荐(所有内容)', type: 100}
                        ]
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'code',
                    anchor: '100%',
                    fieldLabel: '分组代码(A-Z+_)',
                    regex: /^[A-Za-z0-9_]+$/,
                    labelWidth: 150,
                    listeners: {
                        change: 'onTextfieldChange'
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
            if (self._parentId) {
                data['pid'] = self._parentId;
            }
            if (this._data) {
                data['id'] = this._data['id'];
                this.apis.Recommend.updateGroup
                    .wait(self, '正在修改推荐分组...')
                    .call({object: data}, function () {
                        if (self._callback) {
                            self._callback();
                        }
                    });
            } else {
                this.apis.Recommend.addGroup
                    .wait(self, '正在保存推荐分组...')
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
        field.setValue(field.getValue().toUpperCase());
    },

    setValue: function (value) {
        this._data = value;
        this.setTitle('修改推荐分组');
        this.find('form').getForm().setValues(value);
        this.find('type').disable();
    }

});