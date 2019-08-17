Ext.define('App.file.ImageManager', {
    extend: 'Ext.panel.Panel',

    border: false,
    height: 250,
    width: 400,
    layout: 'fit',
    defaultListenerScope: true,
    requires: ['UXApp.image.ImageView'],
    
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    text: '删除图片',
                    iconCls: 'jushou_icon',
                    listeners: {
                        click: 'onDeleteClick'
                    }
                },
                {
                    xtype: 'button',
                    text: '下载图片',
                    icon: Resource.png('jet', 'download'),
                    listeners: {
                        click: 'onDownloadClick'
                    }
                }
            ]
        },
        {
            xtype: 'pagingtoolbar',
            name: 'image_toolbar',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        }
    ],
    items: [
        {
            xtype: 'imagelistview',
            name: 'images_view',
            multiSelect: true
        }
    ],

    onDeleteClick: function (button, e, eOpts) {
        var self = this,
            data = this.find('images_view').getSelects(),
            ids = [];

        for (var i in data) {
            ids.push(data[i]['id'])
        }

        Dialog.batch({
            message: '确定删除文件{d}吗？',
            data: data,
            key: 'realName',
            callback: function (btn) {
                if (btn == Global.YES) {
                    JController.APIFile.deleteFiles(ids).call(function (value) {
                        if (ResultCode.isSuccess(value)) {
                            self.getImageView().refreshStore();
                        } else {
                            Dialog.alert('提示', '删除文件错误:' + ResultCode.getMessage(value), Dialog.ERROR);
                        }
                    }, function () {
                        Dialog.alert('提示', '网络错误！', Dialog.ERROR);
                    });
                }
            }
        });
    },

    onDownloadClick: function (button, e, eOpts) {
        var self = this,
            data = this.find('images_view').getSelects();

        if (data) {
            data.forEach(function (o) {
                var id = o['id'];
                window.open(Resource.getImagePath() + "/admin/download.action?id=" + id);
            });
        }
    },

    onAddTab: function (record) {
        var info = JController.APIFile.getFilesByType().info();
        info.param['type'] = record.get('type');

        var store = Ext.create('Ext.data.Store', {
            autoLoad: true,
            fields: ['id', 'realName', 'url', 'shortName'],
            proxy: {
                type: 'ajax',
                url: info.url,
                extraParams: info.param,
                reader: {
                    type: 'json',
                    rootProperty: 'records',
                    totalProperty: 'total'
                }
            }
        });
        this.find('images_view').setStore(store);
        this.find('image_toolbar').bindStore(store);
    }

});