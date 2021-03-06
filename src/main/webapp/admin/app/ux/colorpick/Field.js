/**
 * A field that can be clicked to bring up the color picker.
 * The selected color is configurable via {@link #value}.
 *
 *      @example
 *      Ext.create({
 *          xtype: 'colorfield',
 *          renderTo: Ext.getBody(),
 *
 *          value: '#993300',  // initial selected color
 *
 *          listeners : {
 *              change: function (field, color) {
 *                  console.log('New color: ' + color);
 *              }
 *          }
 *      });
 */
Ext.define('UXApp.colorpick.Field', {
    extend: 'Ext.form.field.Picker',
    xtype: 'colorfield',

    mixins: [
        'UXApp.colorpick.Selection'
    ],

    requires: [
        'Ext.window.Window',
        'UXApp.colorpick.Selector',
        'UXApp.colorpick.ColorUtils',
        'Ext.layout.container.Fit'
    ],

    editable: false,

    matchFieldWidth: false, // picker is usually wider than field

    // "Color Swatch" shown on the left of the field
    beforeBodyEl: [
        '<div class="' + Ext.baseCSSPrefix + 'colorpicker-field-swatch">' +
        '<div id="{id}-swatchEl" data-ref="swatchEl" class="' + Ext.baseCSSPrefix +
        'colorpicker-field-swatch-inner"></div>' +
        '</div>'
    ],

    cls: Ext.baseCSSPrefix + 'colorpicker-field',
    childEls: [
        'swatchEl'
    ],
    checkChangeEvents: ['change'],

    config: {
        /**
         * @cfg {Object} popup
         * This object configures the popup window and colorselector component displayed
         * when this button is clicked. Applications should not need to configure this.
         * @private
         */
        popup: {
            lazy: true,
            $value: {
                xtype: 'window',
                closeAction: 'hide',
                referenceHolder: true,
                minWidth: 540,
                minHeight: 200,
                layout: 'fit',
                header: false,
                resizable: true,
                items: {
                    xtype: 'colorselector',
                    reference: 'selector',
                    showPreviousColor: true,
                    showOkCancelButtons: true
                }
            }
        }
    },

    /**
     * @event change
     * Fires when a color is selected or if the field value is updated (if {@link #editable}).
     * @param {Ext.ux.colorpick.Field} this
     * @param {String} color The value of the selected color as per specified {@link #format}.
     * @param {String} previousColor The previous color value.
     */

    initComponent: function () {
        var me = this;

        me.callParent();
        me.on('change', me.onHexChange);
    },

    // NOTE: Since much of the logic of a picker class is overriding methods from the
    // base class, we don't bother to split out the small remainder as a controller.

    afterRender: function () {
        this.callParent();

        this.updateValue(this.value);
    },

    // override as required by parent pickerfield
    createPicker: function () {
        var me = this,
            popup = me.getPopup(),
            picker;

        // the window will actually be shown and will house the picker
        me.colorPickerWindow = popup = Ext.create(popup);
        me.colorPicker = picker = popup.lookupReference('selector');

        picker.setFormat(me.getFormat());
        picker.setColor(me.getColor());
        picker.setHexReadOnly(!me.editable);

        picker.on({
            ok: 'onColorPickerOK',
            cancel: 'onColorPickerCancel',
            scope: me
        });

        popup.on({
            close: 'onColorPickerCancel',
            scope: me
        });

        return me.colorPickerWindow;
    },

    // When the Ok button is clicked on color picker, preserve the previous value
    onColorPickerOK: function (colorPicker) {
        this.setColor(colorPicker.getColor());

        this.collapse();
    },

    onColorPickerCancel: function () {
        this.collapse();
    },

    onExpand: function () {
        var color = this.getColor();

        this.colorPicker.setPreviousColor(color);
    },

    onHexChange: function (field) {
        if (field.validate()) {
            this.setValue(field.getValue());
        }
    },

    // Expects value formatted as per "format" config
    setValue: function (color) {
        var me = this;

        if (UXApp.colorpick.ColorUtils.isValid(color)) {
            color = me.applyValue(color);

            me.callParent([color]);

            // always update in case opacity changes, even if value doesn't have it
            // to handle "hex6" non-opacity type of format
            me.updateValue(color);
        }
    },

    // Sets this.format and color picker's setFormat()
    updateFormat: function (format) {
        var cp = this.colorPicker;

        if (cp) {
            cp.setFormat(format);
        }
    },

    updateValue: function (color) {
        var me = this,
            c;

        // If the "value" is changed, update "color" as well. Since these are always
        // tracking each other, we guard against the case where we are being updated
        // *because* "color" is being set.
        if (!me.syncing) {
            me.syncing = true;
            me.setColor(color);
            me.syncing = false;
        }

        c = me.getColor();

        if (c) {
            UXApp.colorpick.ColorUtils.setBackground(me.swatchEl, c);

            if (me.colorPicker) {
                me.colorPicker.setColor(c);
            }
        }
    },

    validator: function (val) {
        if (!UXApp.colorpick.ColorUtils.isValid(val)) {
            return this.invalidText;
        }

        return true;
    }
});