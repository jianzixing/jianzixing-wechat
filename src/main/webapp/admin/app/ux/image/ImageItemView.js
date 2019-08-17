Ext.define('UXApp.image.ImageItemView', {
    extend: 'Ext.container.Container',

    alias: 'widget.imageitemview',

    tpl: [
        '<img style="display: none" src="{src}" alt="{alt}"/>',
        '<div name="box" style="display: none;display: flex;overflow: hidden">',
        '<div style="flex: 1;cursor: pointer"><i style="font-size: 20px;color: white;line-height: 30px" class="fa fa-trash" aria-hidden="true"></i></div>',
        '</div>'
    ],

    data: {},

    src: null,
    alt: null,
    width: 180,
    height: 180,
    margin: 5,
    style: {
        border: '1px solid #EFEFEF',
        overflow: 'hidden'
    },

    initComponent: function () {
        this.callParent();
        if (this.src) {
            this.data['src'] = this.src;
        }
        if (this.alt) {
            this.data['alt'] = this.alt;
        }
    },

    afterRender: function () {
        this.callParent();
        var dom = this.getEl();
        var img = dom.select('img', false);
        var box = dom.select('div[name=box]', false);
        var ops = box.query('div', false);
        var width = this.width || 300;
        var height = this.height || 300;
        var self = this;

        this.$box = box;

        img.setStyle({
            display: 'block',
            maxWidth: width + 'px',
            maxHeight: height + 'px',
            margin: 'auto'
        });

        var parent = Ext.get(img.elements[0].parentNode);
        parent.setStyle({
            display: 'table-cell',
            verticalAlign: 'middle',
            textAlign: 'center'
        });

        box.setStyle({
            width: '100%',
            height: '30px',
            background: 'rgba(0,0,0,0.5)',
            position: 'absolute',
            top: '0',
            display: 'none'
        });

        this.getEl().on('mouseenter', this._onMouseEnter, this);
        this.getEl().on('mouseleave', this._onMouseLeave, this);
        ops.elements[0].onclick = function () {
            self.fireEvent("deleteclick", self)
        };
    },

    _onMouseEnter: function () {
        this.$box.setStyle({
            display: 'block'
        })
    },

    _onMouseLeave: function () {
        this.$box.setStyle({
            display: 'none'
        })
    }

});