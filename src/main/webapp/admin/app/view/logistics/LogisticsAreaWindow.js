Ext.define('App.logistics.LogisticsAreaWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.column.Boolean',
        'Ext.grid.View',
        'Ext.selection.CheckboxModel',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    autoShow: true,
    height: 610,
    width: 680,
    layout: 'fit',
    title: '选择地区',
    defaultListenerScope: true,

    items: [
        {
            xtype: 'container',
            padding: '20',
            scrollable: 'y',
            name: 'areas'
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
                    text: '确定选择',
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
        var areaItems = this.find('areas').getEl().query('.city-ck:checked', true);
        var cityCode = '';
        var popName = '';
        if (areaItems) {
            for (var i = 0; i < areaItems.length; i++) {
                if (i == 0) {
                    var cityName = areaItems[i].parentNode.parentNode.children[1].firstChild.innerHTML;
                    var porName = areaItems[i].parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.children[0].children[1].firstChild.innerHTML;
                    popName = porName + cityName + '等' + areaItems.length + '省市';
                    cityCode = areaItems[i].value;
                } else {
                    cityCode += ',' + areaItems[i].value;
                }
            }
        }
        if (areaItems.length > 0) {
            this.close();
            var me = this;
            if (this.callback) {
                this.callback({
                    code: cityCode,
                    name: popName,
                    type: me.type
                });
            }
        } else {
            Dialog.alert('提示', '请选择区域');
        }
    },

    setSelectCallback: function (fun) {
        this.callback = fun;
    },
    setType: function (type) {
        this.type = type;
    },
    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    loadData: function () {
        var self = this;
        this.apis.Area.getClassifyArea
            .wait(this, '正在加载地区...')
            .call({}, function (data) {
                var html = self.getHtmls(data);
                self.find('areas').setHtml(html);
                self.setSelfListeners();
            });
    },

    setSelfListeners: function () {
        var funs = {
            showDialog: function (self) {
                var me = Ext.get(self.getParent());
                var item = me.selectNode('.law-items', true);
                item.style.border = '1px solid #f7e4a5';
                item.style.borderBottom = '0px';
                item.style.backgroundColor = '#FFFEC6';
                var dialog = me.selectNode('.law-show-areas', false);
                dialog.show();
                var arr = self.selectNode('i', true);
                arr.className = 'fa fa-caret-up';
                self._self_is_select = true;
            },
            closeDialog: function (self) {
                var me = Ext.get(self.getParent());
                var item = me.selectNode('.law-items', true);
                item.style.border = '1px solid #FFFFFF';
                item.style.borderBottom = '0px';
                item.style.backgroundColor = null;
                var dialog = me.selectNode('.law-show-areas', false);
                dialog.hide();
                var arr = self.selectNode('i', true);
                arr.className = 'fa fa-caret-down';
                self._self_is_select = false;
            },
            calCount: function (self) {
                var me = Ext.get(self.parent('.law-items-box'));
                var texts = me.query('.law-items-text span', true);
                // var box = Ext.get(this.parent('.law-show-areas'));
                var checkboxs = self.query('input', true);
                var c = 0;
                for (var j = 0; j < checkboxs.length; j++) {
                    if (checkboxs[j].checked) c++;
                }
                if (c == 0) {
                    texts[0].innerHTML = '';
                } else {
                    texts[0].innerHTML = '(' + c + ')';
                }
            }
        };
        var el = this.getEl();
        var itemsClicks = el.query('.law-items-click', true);
        if (itemsClicks) {
            for (var i = 0; i < itemsClicks.length; i++) {
                Ext.get(itemsClicks[i]).on('click', function () {
                    if (this._self_is_select) {
                        funs.closeDialog(this);
                    } else {
                        funs.showDialog(this);
                    }
                });
            }
        }

        var closeBtns = el.query('.law-close-btn', true);
        if (closeBtns) {
            for (var i = 0; i < closeBtns.length; i++) {
                Ext.get(closeBtns[i]).on('click', function () {
                    var me = Ext.get(this.parent('.law-items-box'));
                    funs.closeDialog(me.selectNode('.law-items-click', false));
                })
            }
        }


        // 市级input改变
        var subItems = el.query('.law-show-areas', true);
        if (subItems) {
            for (var i = 0; i < subItems.length; i++) {
                Ext.get(subItems[i]).on('change', function () {
                    funs.calCount(this);
                });
            }
        }

        // 省级input改变
        var provinceItems = el.query('.law-items', true);
        if (provinceItems) {
            for (var i = 0; i < provinceItems.length; i++) {
                Ext.get(provinceItems[i]).on('change', function () {
                    var selfInput = this.selectNode('input', true);
                    if (selfInput.checked) {
                        var me = Ext.get(this.parent('.law-items-box'));
                        var inputs = me.query('.law-show-areas input', true);
                        for (var j = 0; j < inputs.length; j++) {
                            inputs[j].checked = true;
                        }
                        funs.calCount(me.selectNode('.law-show-areas', false));
                    } else {
                        var me = Ext.get(this.parent('.law-items-box'));
                        var inputs = me.query('.law-show-areas input', true);
                        for (var j = 0; j < inputs.length; j++) {
                            inputs[j].checked = false;
                        }
                        funs.calCount(me.selectNode('.law-show-areas', false));
                    }
                });
            }
        }

        //区域input改变
        var areaItems = el.query('.law-area-input', true);
        if (areaItems) {
            for (var i = 0; i < areaItems.length; i++) {
                Ext.get(areaItems[i]).on('change', function () {
                    var selfInput = this.selectNode('input', true);
                    if (selfInput.checked) {
                        var me = Ext.get(this.parent('.law-area-box'));
                        var inputs = me.query('input', true);
                        for (var j = 0; j < inputs.length; j++) {
                            inputs[j].checked = true;
                        }
                        var showAreas = me.query('.law-show-areas', false);
                        for (var j = 0; j < showAreas.length; j++) {
                            funs.calCount(showAreas[j]);
                        }
                    } else {
                        var me = Ext.get(this.parent('.law-area-box'));
                        var inputs = me.query('input', true);
                        for (var j = 0; j < inputs.length; j++) {
                            inputs[j].checked = false;
                        }
                        var showAreas = me.query('.law-show-areas', false);
                        for (var j = 0; j < showAreas.length; j++) {
                            funs.calCount(showAreas[j]);
                        }
                    }
                });
            }
        }
    },

    getHtmls: function (datas) {
        var html = [];
        html.push('<div style="width: 100%;height: 100%;font-size: 13px">');
        for (var i = 0; i < datas.length; i++) {
            var area = datas[i];
            var ps = area['provinces'];
            if (ps) {
                html.push('<div class="law-area-box" style="width: 100%;overflow: hidden;line-height: 18px">');
                html.push('<div style="float: left;width: 80px;overflow: hidden">');
                html.push('<div style="width: 100%;overflow: hidden;">');
                html.push('<div class="law-area-input" style="float: left"><input type="checkbox" value="' + area['code'] + '"/></div>');
                html.push('<div style="float: left"><span>' + area['name'] + '</span></div>');
                html.push('</div>');
                html.push('</div>');
                html.push('<div style="margin-left: 80px;overflow: hidden">');
                for (var j = 0; j < ps.length; j++) {
                    var psItem = ps[j];
                    var citys = psItem['citys'];

                    html.push('<div class="law-items-box" style="float: left;width: 33%;height: 35px">');
                    html.push('<div class="law-items" style="float: left;border:1px solid #FFFFFF;border-bottom: 0px;">');
                    html.push('<div style="float: left"><input type="checkbox"/></div>');
                    html.push('<div class="law-items-text law-text-btn" style="float: left;margin-right: 5px"><label>' + psItem['name'] + '</label><span style="margin-left:2px;color:#dd2727;font-style: italic"></span></div>');
                    html.push('</div>');
                    html.push('<div class="law-items-click" style="float: left;line-height: 16px;margin-left: 6px;cursor: pointer"><i class="fa fa-caret-down" style="color: #dd2727;font-size: 20px"></i></div>');
                    // html.push('<div style="float: left;line-height: 16px;margin-left: 6px;"><i class="fa fa-caret-up" style="color: #dd2727;font-size: 20px"></i></div>');


                    html.push('<div class="law-show-areas" style="position: absolute;z-index: 10;width: 190px;display: none">');
                    html.push('<div style="position: absolute;z-index: 10;top:20px;width: 190px;min-height: 50px;background-color:#FFFEC6;border:1px solid #f7e4a5;border-top:0px;overflow-y: scroll;max-height: 200px;">');
                    html.push('<div style="padding-top: 10px;overflow: hidden">');
                    if (citys) {
                        for (var k = 0; k < citys.length; k++) {
                            var city = citys[k];
                            html.push('<div style="overflow: hidden;height: 30px;margin-right: 11px;float: left;line-height: 30px">');
                            html.push('<div style="float: left;margin-top: 6px"><input class="city-ck" type="checkbox" value="' + city['code'] + '"/></div>');
                            html.push('<div class="law-text-btn" style="float: left;margin-right: 5px"><label>' + city['name'] + '</label></div>');
                            html.push('</div>');
                        }
                    }
                    html.push('</div>');
                    html.push('<div style="width: 100%"><button class="law-close-btn" style="width: 40px;font-size:12px;float: right;margin-right: 10px;margin-bottom: 6px">关闭</button></div>');

                    html.push('</div>');
                    html.push('</div>');

                    html.push('</div>');
                }
                html.push('</div>');
                html.push('</div>');
            }
        }
        html.push('</div>');
        return html.join('');
    }

});