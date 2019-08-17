Ext.define('UXApp.panel.OrderProductPanel', {
    extend: 'Ext.Component',

    alias: 'widget.orderproductpanel',

    style: {
        backgroundColor: '#f3fbfe'
    },

    tplBody: [
        '<div class="goods-list">',
        '<table>',
        '<thead>',
        '<tr>',
        '<th class="gd-title" colspan="5">商家：JD自营</th>',
        '</tr>',
        '<tr class="gd-hd">',
        '<th class="goods-logo">图片</th>',
        '<th class="goods-name">名称</th>',
        '<th class="goods-code">编号</th>',
        '<th class="goods-price">价格</th>',
        '<th class="goods-amt">数量</th>',
        '</tr>',
        '</thead>',
        '<tbody id="{pane_id}-tb-body">',
        '</tbody>',
        '</table>',
        '</div>'
    ],

    tplItem: [
        '<tr>',
        '<th class="gd-title" colspan="5"><span class="sales-icon">换购</span>嘻嘻嘻活动</th>',
        '</tr>',
        '<tr>',
        '<td class="goods-logo">',
        '<div class="p-img">',
        '<a target="_blank" href="http://item.jd.com/867716.html">',
        '<img src="http://img14.360buyimg.com/N4/g10/M00/19/0B/rBEQWVF45aYIAAAAAAGdq7QchsEAAE82QOc7z0AAZ3D100.jpg" alt="">',
        '</a>',
        '</div>',
        '</td>',
        '<td class="goods-name">',
        '<div class="p-name">',
        '<a href="http://item.jd.com/867716.html" target="_blank"> 佳能（Canon） EOS 100D 单反套机 （EF-S 18-55mm f/3.5-5.6 IS STM 镜头）</a>',
        '</div>',
        '</td>',
        '<td class="goods-code">',
        '<span>45858285</span>',
        '</td>',
        '<td class="goods-price">',
        '<div class="p-price">',
        '<strong class="jd-price">￥4988.00</strong>',
        '</div>',
        '</td>',
        '<td class="goods-amt">',
        '<div class="amt-txt">x1</div>',
        '<input type="text" value="1">',
        '</td>',
        '</tr>'
    ],

    afterRender: function () {
        this.callParent(arguments);

        var tpl = new Ext.XTemplate(this.tplBody);
        tpl.overwrite(this.getEl().dom, {pane_id: this.id});

        var itemTpl = new Ext.XTemplate(this.tplItem);
        itemTpl.overwrite(this.id + '-tb-body');
    }
});