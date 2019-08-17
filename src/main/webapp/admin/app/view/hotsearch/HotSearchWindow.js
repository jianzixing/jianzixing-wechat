Ext.define('App.hotsearch.HotSearchWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 350,
    width: 550,
    layout: 'fit',
    title: '添加热门搜索',
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
                    fieldLabel: '关键字',
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    name: 'pos',
                    anchor: '100%',
                    fieldLabel: '排序',
                    inputType: 'number',
                    emptyText: '0'
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
                    text: '确定保存',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消关闭',
                    listeners: {
                        click: 'onCloseClick'
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
            var values = form.getValues();
            values['type'] = "wx";
            if (self._data) {
                values['id'] = self._data['id'];
                this.apis.HotSearch.updateHotSearch
                    .wait(this, '正在更新热门搜索...')
                    .call({object: values}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    })
            } else {
                this.apis.HotSearch.addHotSearch
                    .wait(this, '正在添加热门搜索...')
                    .call({object: values}, function () {
                        self.close();
                        if (self._callback) {
                            self._callback();
                        }
                    })
            }
        }
    },

    onCloseClick: function (button, e, eOpts) {
        this.close();
    },

    setValue: function (data) {
        this._data = data;
        var form = this.find('form').getForm();
        form.setValues(data);
        this.setTitle('修改热门搜索')
    }

});
