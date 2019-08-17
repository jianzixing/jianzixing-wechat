Ext.define('UXApp.field.MyCheckbox', {
    extend: 'Ext.form.Checkbox',

    alias: 'widget.mycheckbox',
    defaultListenerScope: true,
    listeners: {
        change: 'onCheckboxfieldChange'
    },

    initComponent: function () {
        if (this.textValue) {
            this.checked = true
        }
        this.callParent(arguments);
    },

    afterRender: function () {
        this.callParent(arguments);
        var dom = this.getEl();
        var label = dom.select('#' + this.id + '-boxLabelEl');
        label.setStyle({
            paddingLeft: '30px'
        });

        if (this.checkBoxColor) {
            label.insertHtml('beforeBegin',
                '<div id="' + this.id + '-colorEl" style="width: 26px;height: 26px;background-color: '
                + this.checkBoxColor + ';overflow: hidden;float: left;margin-left: 30px;margin-top: 2px;"></div>');
            label.setStyle({
                paddingLeft: '10px'
            })
        }

        if (this.textValue) {
            this.onCheckboxfieldChange(null, true)
        }

    },

    onCheckboxfieldChange: function (field, newValue, oldValue, eOpts) {
        var dom = this.getEl();
        var width = dom.getWidth();
        var self = this;
        var label = dom.select('#' + this.id + '-boxLabelEl');
        var inputTextEl = dom.select('#' + this.id + '-inputTextEl');
        var colorEl = dom.select('#' + this.id + '-colorEl');
        var ml = 10;
        var value = this.textValue || this.boxLabel;
        if (colorEl.elements.length == 0) {
            ml = 30;
        }

        if (newValue && inputTextEl.elements.length == 0) {
            label.insertHtml('beforeBegin', '<input id="' + this.id + '-inputTextEl" type="text" value="' + value + '" style="height: 26px;padding-left:5px;margin-top: 2px;margin-left: ' + ml + 'px;">');
            inputTextEl = dom.select('#' + this.id + '-inputTextEl');
            inputTextEl.elements[0].addEventListener('keyup', function () {
                self.onInputChange();
            });
            //当前的宽度前去左对齐30px和颜色块26px和文本框距离10px
            inputTextEl.setStyle({
                width: width - 30 - 26 - 10 + 'px'
            })
        }
        if (newValue) {
            if (inputTextEl.elements.length != 0) {
                inputTextEl.setStyle({display: null})
            }
            label.setStyle({display: 'none'});
        } else {
            if (inputTextEl) {
                inputTextEl.setStyle({display: 'none'})
            }
            label.setStyle({display: null});
        }
    },

    getLabelValue: function () {
        var dom = this.getEl();
        if (dom) {
            var inputTextEl = dom.select('#' + this.id + '-inputTextEl');

            if (inputTextEl.elements.length == 0) {
                return this.boxLabel;
            } else {
                return inputTextEl.elements[0].value;
            }
        }
        return this.textValue;
    },

    onInputChange: function () {
        var dom = this.getEl();
        var self = this;
        var inputTextEl = dom.select('#' + this.id + '-inputTextEl');
        var value = inputTextEl.elements[0].value;

        this.fireEvent("inputchange", value);
    }

});