Ext.define('UXApp.panel.WayListPanel', {
    extend: 'Ext.view.View',
    alias: 'widget.waylistpanel',

    tpl: [
        '<tpl for=".">',
        '<div class="pay_box">',
        '银联支付',
        '<ins class=""></ins>',
        '</div>',
        '</tpl>',
        '<div class="empty_txt">当前系统没有支付方式</div>'
    ],
    itemSelector: 'div.pay_box',
    multiSelect: false,
    overItemCls: 'pb_sel',
    trackOver: true
});