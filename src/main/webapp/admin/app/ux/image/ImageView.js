Ext.define('UXApp.image.ImageView', {
    extend: 'Ext.Component',

    alias: 'widget.imagelistview',

    cls: 'images_view',
    tpl: [
        '<tpl for=".">',
        '<div class="thumb-wrap">',
        '<div class="thumb"><img src="' + Resource.image('{fileName}') + '" title="{originalName}"></div>',
        '<nobr><span class="x-editable">{originalName}</span></nobr></div>',
        '</tpl>',
        '<div class="x-clear"></div>'
    ],
    emptyText: '拖拽文件到这里...',
    itemSelector: 'div.thumb-wrap',
    multiSelect: false,
    overItemCls: 'x-item-over', //选中
    focusItemCls: 'x-view-item-focused', //鼠标移动
    itemSelectedCls: 'x-item-selected', //选中
    trackOver: true,

    afterRender: function () {
        this.callParent(arguments);
        var dom = this.getEl().dom;
        if (!FormData) {
            this.emptyText = "没有上传文件";
        }
        this.setStore(this.store);

        this.setDragUpload()
    },

    setDragUpload: function () {
        var dom = this.getEl().dom;
        var self = this;
        if (FormData) {
            dom.setAttribute("draggable", "true")
            dom.ondragenter = function (e) {
                e.preventDefault();
            };
            dom.ondragover = function (e) {
                e.preventDefault();
                console.log("松开鼠标开始上传");
            };
            dom.ondragleave = function (e) {
                e.preventDefault();
                console.log("拖拽到这里上传");
            };

            dom.ondrop = function (e) {
                e.preventDefault();
                var files = e.dataTransfer.files;

                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    var fileName = file['name'];

                    var msg = Ext.MessageBox.show({
                        title: "标题",
                        msg: '正在上传文件:' + fileName,
                        buttons: Ext.MessageBox.YES,    //对话框的按钮组合
                        closable: false,                        //是否可关闭
                        width: 400,
                        progress: true,
                        progressText: "上传中..."
                    });

                    var formData = new FormData();
                    formData.append(file['name'], file);
                    if (self.uploadParam) {
                        for (var i in self.uploadParam) {
                            formData.append(i, self.uploadParam[i]);
                        }
                    }
                    var xmlHttp = new XMLHttpRequest();
                    xmlHttp.open("post", self.uploadUrl);
                    xmlHttp.send(formData);
                    xmlHttp.onreadystatechange = function () {
                        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                            msg.close();
                            self.getStore().reload();
                        } else if (xmlHttp.status != 200 && xmlHttp.status != 0) {
                            msg.close();
                            Ext.MessageBox.show({
                                title: "上传失败",
                                msg: '文件上传失败:' + fileName,
                                icon: Ext.MessageBox.WARNING,
                            });
                        }
                    };

                }
            };
        }
    },

    setUploadParam: function (url, json) {
        this.uploadUrl = url;
        this.uploadParam = json
    },

    setStore: function (store) {
        var me = this, el = me.getEl();
        if (el) {
            if (store == null) {
                this.getEl().dom.innerHTML = '<div style="width: 100%;height: 100%;text-align: center;padding: 100px;font-size: 20px;color: #666666">' + this.emptyText + '</div>';
            } else {
                me.mask('读取中...');
                store.addListener('datachanged', function (tis, eOpts) {
                    me.unmask();
                    me.getEl().dom.innerHTML = '';
                    me.renderImages(tis.getData().items);
                });

                if (store.getData().length > 0) {
                    var items = store.getData().items;
                    me.renderImages(items);
                }
            }
        }
        this.store = store;
    },

    bindStore: function (store) {
        this.setStore(store);
    },

    renderImages: function (records) {
        var data = [], me = this, dom = me.getEl().dom;
        if (Ext.isArray(records)) {
            for (var i = 0; i < records.length; i++) {
                data.push(records[i].getData());
            }
            if (this.imgId) {
                if (Ext.isArray(this.tpl)) {
                    this.tpl[2] = this.tpl[2].replace('{id}', '{' + this.imgId + "}");
                } else {
                    this.tpl.html = this.tpl.html.replace('{id}', '{' + this.imgId + "}");
                }
            }
            var tpl = Ext.XTemplate(this.tpl, data),
                last;
            data.forEach(function (o, i) {
                if (i == 0) {
                    last = tpl.overwrite(dom, o);
                } else {
                    last = tpl.insertAfter(last, o);
                }
                last._data = o;
            });
            this.elEvent();
        }
    },

    elEvent: function () {
        var elements = Ext.query(this.itemSelector),
            me = this;
        Ext.each(elements, function (item) {
            var ele = Ext.get(item);
            ele.on({
                scope: me,
                click: me.onItemClick
            });

            var img = ele.child('div.thumb img');
            img.on({
                mousemove: me.onItemMouseMove,
                mouseout: me.onItemMouseOut,
                scope: me
            });
        })

        this.getEl().on({
            scope: me,
            contextmenu: this.onCancelClick
        });
    },

    onCancelClick: function (e, t) {
        var elements = Ext.query(this.itemSelector);
        for (var i in elements) {
            var ele = Ext.get(elements[i]);
            ele.removeCls(this.overItemCls);
            ele.removeCls(this.itemSelectedCls);
        }
        e.preventDefault();
    },

    onItemMouseMove: function (e, t) {
        t = Ext.get(t);
        t.parent().parent().addCls(this.focusItemCls);
    },

    onItemMouseOut: function (e, t) {
        var elements = Ext.query(this.itemSelector);
        for (var i = 0; i < elements.length; i++) {
            var ele = Ext.get(elements[i]);
            ele.removeCls(this.focusItemCls);
        }
    },

    onItemClick: function (e) {
        var t = Ext.get(e.currentTarget);
        if (!this.multiSelect) {
            var elements = Ext.query(this.itemSelector);
            for (var i = 0; i < elements.length; i++) {
                var ele = Ext.get(elements[i]);
                ele.removeCls(this.overItemCls);
                ele.removeCls(this.itemSelectedCls);
            }
        }

        if (t.hasCls(this.itemSelectedCls)) {
            t.removeCls(this.overItemCls);
            t.removeCls(this.itemSelectedCls);
        } else {
            t.addCls(this.itemSelectedCls);
            t.addCls(this.overItemCls);
        }

        if (this.setItemClick) {
            this.setItemClick(e);
        }
    },

    refreshStore: function () {
        this.store.load();
    },

    getStore: function () {
        return this.store;
    },

    getSelects: function () {
        var elements = Ext.query(this.itemSelector),
            data = [];
        for (var i = 0; i < elements.length; i++) {
            var ele = Ext.get(elements[i]);
            if (ele.hasCls(this.itemSelectedCls)) {
                data.push(elements[i]._data);
            }
        }
        return data;
    },

    getValue: function () {
        var elements = Ext.query(this.itemSelector),
            data = [];
        for (var i = 0; i < elements.length; i++) {
            data.push(elements[i]._data);
        }
        return data;
    }
});