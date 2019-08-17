Ext.define('UXApp.panel.AddressPanel', {
    extend: 'Ext.view.View',
    alias: 'widget.addresspanel',

    tpl: [
        '<div class="rbb_parent">',
        '<tpl for=".">',
        '<div class="receive_box rb_unsel">',
        '<div class="receive_w">',
        '<div class="receive_people">',
        '<span class="prov">北京</span>',
        '<span class="city">北京</span>',
        '<span>（</span>',
        '<span class="name">杨安康</span>',
        '<span> 收）</span>',
        '</div>',
        '<div class="receive_addr">',
        '<span class="dist">朝阳</span>',
        '<span class="town">孙河</span>',
        '<span class="j_4tip"></span>',
        '<span class="street">孙河地区康营家园社区23区8号楼一单元1602室</span>',
        '<span class="phone">15910714231</span>',
        '<span class="last">&nbsp;</span>',
        '</div>',
        '<div class="addr-toolbar">',
        '<a title="修改地址" href="javascript:void(0);">修改</a>',
        '</div>',
        '</div>',
        '<ins class="curmarker"></ins>',
        '<ins class="deftip">默认地址</ins>',
        '</div>',
        '</tpl>',
        '<div class="empty_txt"></div>',
        '</div>',
        '<div class="addr_tb_add">',
        '<a title="添加地址" href="javascript:void(0);">添加收货地址</a>',
        '</div>'
    ],
    itemSelector: 'div.receive_box',
    multiSelect: false,
    overItemCls: 'rb_sel',
    trackOver: true,

    onBindStore: function (store) {
        this.callParent(arguments);
        var me = this;
        store.addListener('datachanged', function (tis, eOpts) {
            me.resetTplListeners();
        });
    },

    onBoxReady: function (width, height) {
        this.callParent(arguments);
        this.resetTplListeners();
    },

    resetTplListeners: function () {
        var as = Ext.query('#' + this.id + ' div.addr_tb_add a');
        if (as && as.length > 0) {
            Ext.get(as[0]).on('click', function (e) {
                console.log('--')
            })
        }
        this._dataLength();
    },

    _dataLength: function () {
        if (this.store != null && this.store.getData().length > 0) {
            return true;
        } else {
            var as = Ext.query('#' + this.id + ' div.empty_txt')
            if (as != null && as.length > 0) {
                Ext.get(as[0]).setHtml('当前用户没有收获地址');
            }
        }
    }
});