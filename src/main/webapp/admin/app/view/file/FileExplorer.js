Ext.define('App.file.FileExplorer', {
    extend: 'Ext.panel.Panel',

    height: 445,
    width: 733,
    border: false,
    layout: {
        type: 'border'
    },
    alias: 'widget.filexplorer',
    requires: ['UXApp.image.ImageView'],

    apis: {
        File: {
            getFileGroups: {},
            addFileGroup: {},
            deleteFiles: {},
            moveGroup: {},
            deleteFileGroup: {},

            getFiles: {},
            getFileHttpUrl: {}
        }
    },

    preventHeader: true,
    defaultListenerScope: true,
    folderIcon: Resource.create('/admin/image/icon/folder.png'),
    dockedItems: [
        {
            xtype: 'toolbar',
            name: 'select_file_tb',
            dock: 'bottom',
            hidden: true,
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'inspectionsOK'),
                    listeners: {
                        click: 'onAddClick'
                    },
                    text: '确定选择'
                },
                '->'
            ]
        }
    ],
    items: [
        {
            xtype: 'treepanel',
            name: 'file_dir',
            region: 'west',
            displayField: 'groupName',
            split: true,
            border: false,
            width: 310,
            title: '文件目录',
            useArrows: true,
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'editFolder'),
                            listeners: {
                                click: 'onListClassClick'
                            },
                            text: '列出目录'
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'addFolder'),
                            listeners: {
                                click: 'onAddClassClick'
                            },
                            text: '添加目录'
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'delete'),
                            listeners: {
                                click: 'onDeleteClassClick'
                            },
                            text: '删除目录'
                        }
                    ]
                }
            ],
            tools: [
                {
                    xtype: 'tool',
                    type: 'prev',
                    listeners: {
                        click: 'onToolPrevClick'
                    }
                }
            ],
            viewConfig: {
                listeners: {
                    itemclick: 'onTreeviewItemClick'
                }
            }
        },
        {
            xtype: 'panel',
            border: false,
            region: 'center',
            layout: {
                type: 'fit'
            },
            title: '当前目录下所有的文件',
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'upload'),
                            listeners: {
                                click: 'onUploadClick'
                            },
                            text: '上传文件'
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'RemoveMulticaret_dark'),
                            listeners: {
                                click: 'onDeleteFileClick'
                            },
                            text: '删除文件'
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'MoveTo2'),
                            listeners: {
                                click: 'onMoveFileClick'
                            },
                            text: '移动分组'
                        },
                        '-',
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'copy_dark'),
                            listeners: {
                                click: 'onCopyURLClick'
                            },
                            text: '拷贝URL'
                        },
                        {
                            xtype: 'button',
                            icon: Resource.png('jet', 'download'),
                            listeners: {
                                click: 'onDownloadFileClick'
                            },
                            text: '下载文件'
                        },
                        '->',
                        {
                            xtype: 'label',
                            name: 'top_ctrl_lb',
                            width: 300,
                            html: '<img style="display: block;float: left" src="/admin/image/hd.png" alt="提示"/>'
                                + '<span style="color: #999999;font-size: 13px;display: block;float: left;margin-top: 2px;margin-left: 3px">' +
                                '提示:拖拽文件到下面区域可以上传文件</span>',
                            text: ''
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
            ]
        }
    ],

    onToolPrevClick: function (b, e, o) {
        this.find('file_dir').collapse(Ext.Component.DIRECTION_LEFT);
    },

    getTree: function () {
        return this.find('file_dir');
    },

    getImageView: function () {
        return this.find('images_view');
    },

    onListClassClick: function () {
        this.setTreeStore();
    },

    onAddClassClick: function (button, e, options) {
        var tree = this.find('file_dir');
        var self = this;
        var selections = this.find('file_dir').getIgnoreSelect(arguments, 'id');
        if (!selections) {
            Dialog.alert('提示', '必须选择一个父目录');
            return false;
        }
        Dialog.prompt('添加目录', '输入目录名称', function (btn, text) {
            if (btn == Global.OK && text != '' && text != null) {
                var data = {groupName: text, pid: selections};
                self.apis.File.addFileGroup
                    .wait(self, '正在添加文件夹...')
                    .call({object: data}, function () {
                        self.setTreeStore();
                    });
            }
        });
    },

    onDeleteClassClick: function (button, e, options) {
        var self = this,
            jsons = this.find('file_dir').getIgnoreSelect(arguments);

        Dialog.batch({
            message: '<span style="color: red">警告：删除当前目录后，当前目录下的图片会转移到根目录下，确定删除目录{d}吗？</span>',
            data: jsons,
            key: 'groupName',
            callback: function (btn) {
                if (btn == Global.YES) {
                    self.apis.File.deleteFileGroup
                        .wait(self, '正在删除文件夹...')
                        .call({id: jsons['id']}, function () {
                            self.setTreeStore();
                        });
                }
            }
        });
    },

    onTreeviewItemClick: function (dataview, record, item, index, e, options) {
        var id = this.find('file_dir').getIgnoreSelect(arguments, 'id');
        var info = this.apis.File.getFiles.info({gid: id});

        var store = Ext.create('Ext.data.Store', {
            autoLoad: true,
            pageSize: 50,
            proxy: {
                type: 'ajax',
                url: info.url,
                extraParams: info.data,
                reader: {
                    type: 'json',
                    rootProperty: 'records',
                    totalProperty: 'total'
                }
            }
        });
        this.find('images_view').setStore(store);
        this.find('image_toolbar').bindStore(store);
        this.find('images_view').setUploadParam("/admin/file/upload_file.action", {gid: id});
    },

    onUploadClick: function (button, e, options) {
        var self = this;
        var id = this.find('file_dir').getIgnoreSelect(arguments, 'id');
        if (!id) {
            Dialog.alert('提示', '必须选中一个目录');
            return false;
        }
        Dialog.openWindow('App.file.UploadWindow', {
            groupId: id,
            refreshImages: function () {
                self.getImageView().refreshStore();
            }
        });
    },

    onDeleteFileClick: function (button, e, options) {
        var self = this,
            data = this.find('images_view').getSelects(),
            ids = [];

        for (var i = 0; i < data.length; i++) {
            ids.push(data[i]['id'])
        }

        if (data.length > 0) {
            Dialog.batch({
                message: '<span style="color: red">警告：如果删除当前图片也会删除硬盘的图片，之后任何人都无法看到这个图片！确定删除文件{d}吗？</span>',
                data: data,
                key: 'fileName',
                callback: function (btn) {
                    if (btn == Global.YES) {
                        self.apis.File.deleteFiles
                            .wait(self, '正在删除文件...')
                            .call({ids: ids}, function () {
                                self.find('images_view').getStore().reload()
                            });
                    }
                }
            });
        } else {
            Dialog.alert("删除文件前请先选择一个文件")
        }
    },

    onMoveFileClick: function (button, e, options) {
        var self = this;
        var datas = self.find('images_view').getSelects();

        if (datas && datas.length > 0) {
            self.mask('正在获取分组信息...');
            this.apis.File.getFileGroups
                .wait(self, '正在获取分组信息...')
                .call({}, function (d) {
                    self.unmask();
                    var store = Ext.create('Ext.data.TreeStore', {
                        nodeParam: 'pid',
                        root: {
                            expanded: true,
                            groupName: '我的文件',
                            id: '0',
                            children: d
                        }
                    });

                    Dialog.openWindow('App.file.MoveGroupWindow', {
                        _callback: function (json) {
                            var ids = Array.splitArray(datas, 'id');
                            self.apis.File.moveGroup
                                .wait(self, '正在移动文件分组...')
                                .call({fid: ids, gid: json['id']}, function () {
                                    self.find('images_view').getStore().reload();
                                })
                        }
                    }).setStore(store);
                }, function () {
                    self.unmask()
                });
        } else {
            Dialog.alert('请选中文件后再移动文件分组!');
        }
    },

    onDownloadFileClick: function (button, e, options) {
        var self = this,
            data = this.find('images_view').getSelects();
        if (data && data.length > 0) {
            data.forEach(function (o) {
                var id = o['id'];
                window.open(Resource.getImagePath() + "/admin/file/download.action?id=" + id);
            });
        } else {
            Dialog.alert("先选择一个文件再点击下载")
        }
    },

    onCopyURLClick: function () {
        var self = this,
            data = this.find('images_view').getSelects();
        if (data && data.length > 0) {
            var id = data[0]['id'];
            self.apis.File.getFileHttpUrl
                .wait(self, '正在获取文件地址...')
                .call({fid: id}, function (data) {
                    Dialog.openWindow('App.file.CopyURLWindow').setValue(data);
                });
        } else {
            Dialog.alert("先选择一个文件再拷贝URL")
        }
    },

    onAfterApply: function (one, groupSourceType) {
        var self = this;
        if (one) {
            this.one = true;
            this.find('images_view').multiSelect = false;
        }
        this._groupSourceType = groupSourceType;
        this.setTreeStore();
    },

    setTreeStore: function () {
        var self = this;
        this.apis.File.getFileGroups
            .wait(self.find('file_dir'), '正在加载文件目录...')
            .call({groupSourceType: this._groupSourceType}, function (d) {
                var store = Ext.create('Ext.data.TreeStore', {
                    nodeParam: 'pid',
                    root: {
                        expanded: true,
                        groupName: '我的文件',
                        id: '0',
                        children: d
                    }
                });
                self.find('file_dir').setStore(store);
                self.find('file_dir').expandPath('0');
            });
    },

    onSelectWindow: function (callback) {
        this.callback = callback;
        var view = this.find('select_file_tb');
        if (view) view.show();
    },

    onAddClick: function () {
        var files = this.find('images_view').getSelects();
        if (!files) {
            if (this.callback) {
                this.callback();
            }
        } else {
            if (this.callback) {
                if (this.one) this.callback(files[0]); else this.callback(files);
            }
        }
    }
});
