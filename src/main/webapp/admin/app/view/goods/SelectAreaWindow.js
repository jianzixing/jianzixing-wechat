/**
 * Created by qmt216 on 2017/11/1.
 */
Ext.define('App.goods.SelectAreaWindow', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.selectareawindow',

    requires: [
        'Ext.tree.View',
        'Ext.selection.CheckboxModel'
    ],

    height: 250,
    width: 400,
    title: '选择区域',
    apis: {
        Area: {
            getAreaList: {}
        }
    },
    viewConfig: {},
    selModel: {
        selType: 'checkboxmodel'
    },
    onAfterApply: function () {
        var self = this;
        this.apis.Area.getAreaList
            .wait(self, '正在加载地区...')
            .call({}, function (d) {
                var store = Ext.create('Ext.data.TreeStore', {
                    nodeParam: 'pid',
                    root: {
                        expanded: true,
                        groupName: '中国',
                        id: '0',
                        children: d
                    }
                });
                self.setStore(store);
                self.expandPath('0');
            });
    }
});