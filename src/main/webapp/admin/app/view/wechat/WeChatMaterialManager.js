Ext.define('App.wechat.WeChatMaterialManager', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.wechat_material_manager',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.toolbar.Paging',
        'Ext.selection.CheckboxModel'
    ],

    activeTab: 0,
    defaultListenerScope: true,

    border: false,
    header: false,
    columns: [
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'url',
            text: '封面',
            renderer: function (v, mate, record) {
                var type = record.get('type');
                var width = 40;
                var height = 40;
                if (type == 'image') {
                    var url = record.get('url');
                    return '<div style="height: ' + height + 'px;width: ' + width + 'px;vertical-align: middle;display:table-cell;background-color: #BBBBBB">' +
                        '<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;vertical-align: middle" src=' + url + '></div> ';
                }
                if (type == 'news') {
                    var content = record.get('content');
                    var news_item = content['news_item'];
                    var html = [];
                    for (var i = 0; i < news_item.length; i++) {
                        var url = news_item[i]['thumb_url'];
                        var link = news_item[i]['url'];
                        html.push('<a style="text-decoration: none" target="_blank" href="' + link + '">');
                        html.push('<div style="height: ' + height + 'px;width: ' + width + 'px;' +
                            'align-items: center;justify-content:center;display:flex;' +
                            'background-color: #BBBBBB;float: left;margin-right: 2px">');
                        html.push('<img style="max-height: ' + height + 'px;max-width: ' + width + 'px;" src=' + url + '>');
                        html.push('</div>');
                        html.push('</a>');
                    }
                    return html.join('');
                }
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'media_id',
            text: '微信素材ID'
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'type',
            text: '素材类型',
            renderer: function (v) {
                if (v == 'image') return '图片';
                if (v == 'voice') return '语音';
                if (v == 'video') return '视频';
                if (v == 'thumb') return '缩略图';
                if (v == 'news') return '图文';
                return '其他';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'name',
            text: '素材名称',
            renderer: function (v) {
                if (v) return v;
                return '[其他]';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 200,
            dataIndex: 'url',
            text: '连接地址',
            renderer: function (v) {
                if (v) return v;
                return '[其他]';
            }
        },
        {
            xtype: 'gridcolumn',
            width: 150,
            dataIndex: 'update_time',
            text: '创建时间',
            renderer: function (v) {
                if (v) return new Date(v * 1000).format();
                return '';
            }
        },
        {
            xtype: 'actioncolumn',
            dataIndex: 'id',
            text: '操作',
            align: 'center',
            items: [
                {
                    iconCls: 'x-fa fa-times',
                    tooltip: '删除资源',
                    handler: 'onDelAClick'
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'pagingtoolbar',
            dock: 'bottom',
            width: 360,
            displayInfo: true
        },
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    icon: Resource.png('jet', 'toolWindowPalette'),
                    name: 'image_button',
                    text: '列出图片素材',
                    materialType: 'image',
                    handler: 'onListAClick'
                },
                {
                    icon: Resource.png('jet', 'recording_3'),
                    name: 'video_button',
                    text: '列出视频素材',
                    materialType: 'video',
                    handler: 'onListAClick'
                },
                {
                    icon: Resource.png('jet', 'includeNonStartedTests_Rerun'),
                    name: 'voice_button',
                    text: '列出语音素材',
                    materialType: 'voice',
                    handler: 'onListAClick'
                },
                {
                    icon: Resource.png('jet', 'moveToAnotherChangelist_dark'),
                    name: 'news_button',
                    text: '列出图文素材',
                    materialType: 'news',
                    handler: 'onListAClick'
                },
                {
                    xtype: 'button',
                    name: 'delete_button',
                    icon: Resource.png('jet', 'delete'),
                    text: '删除永久素材',
                    listeners: {
                        click: 'onDelAClick'
                    }
                }
            ]
        }
    ],
    selModel: {
        selType: 'checkboxmodel'
    },

    onListAClick: function (button, e, eOpts) {
        var self = this;
        var type = button.materialType;
        if (type && type != '') {
            var id = this.accountData['id'];
            var openType = this.openType;
            var store = self.apis.WeChatMaterial.getForeverMaterials.createPageStore(
                {accountId: id, type: type, openType: openType}
            );
            self.setStore(store);
            store.load();
        }
    },

    onDelAClick: function (button, e, eOpts) {
        var self = this;
        var datas = this.getIgnoreSelects(arguments);
        if (datas) {
            var id = this.accountData['id'];
            var openType = this.openType;
            Dialog.batch({
                message: '确定删除资源{d}吗？',
                data: datas,
                key: 'name',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        var ids = Array.splitArray(datas, "media_id");
                        self.apis.WeChatMaterial.deleteForeverMaterials
                            .wait(self, '正在删除资源...')
                            .call({accountId: id, openType: openType, mediaIds: ids}, function () {
                                self.refreshStore();
                            });
                    }
                }
            });
        } else {
            Dialog.alert('请先选中一条资源记录');
        }
    },

    getDataSelects: function () {
        var datas = [];
        var selects = this.getSelection();
        for (var i = 0; i < selects.length; i++) {
            datas.push(selects[i].data);
        }
        if (datas) {
            for (var i = 0; i < datas.length; i++) {
                var data = datas[i];
                data['id'] = data['media_id'];
                if (data['type'] == 'image') {
                    data['coverUrl'] = [data['url']];
                }
                if (data['type'] == 'voice') {
                    data['coverUrl'] = [data['url']];
                }
                if (data['type'] == 'video') {
                    data['coverUrl'] = [data['url']];
                }
                if (data['type'] == 'thumb') {
                    data['coverUrl'] = [data['url']];
                }
                if (data['type'] == 'news') {
                    var content = data['content'];
                    var news_item = content['news_item'];
                    var urls = [];
                    for (var i = 0; i < news_item.length; i++) {
                        var url = news_item[i]['thumb_url'];
                        var link = news_item[i]['url'];
                        urls.push(url);
                    }
                    data['coverUrl'] = urls;
                }
            }
        }
        return datas;
    },

    onAfterApply: function () {
        var self = this;
        if (this.accountData) {
            var id = this.accountData['id'];
            var openType = this.openType;
            var type = 'image';
            if (this.materialType == 3) type = 'image';
            if (this.materialType == 4) type = 'voice';
            if (this.materialType == 5) type = 'video';
            if (this.materialType == 'thumb') type = 'thumb';
            if (this.materialType == 'news') type = 'news';
            var store = self.apis.WeChatMaterial.getForeverMaterials.createPageStore(
                {accountId: id, type: type, openType: openType}
            );
            self.setStore(store);
            store.load();
        }
    }

});
