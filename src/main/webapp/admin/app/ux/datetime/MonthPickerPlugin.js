/**
 * Created by qinmingtao on 2017/3/7.
 */
Ext.ux.MonthPickerPlugin = function() {
    var picker;
    var oldDateDefaults;

    this.init = function(pk) {
        picker = pk;
        picker.onTriggerClick = picker.onTriggerClick.createSequence(onClick);
        picker.getValue = picker.getValue.createInterceptor(setDefaultMonthDay).createSequence(restoreDefaultMonthDay);
        picker.beforeBlur = picker.beforeBlur.createInterceptor(setDefaultMonthDay).createSequence(restoreDefaultMonthDay);
    };

    function setDefaultMonthDay() {
        oldDateDefaults = Date.defaults.d;
        Date.defaults.d = 1;
        return true;
    }

    function restoreDefaultMonthDay(ret) {
        Date.defaults.d = oldDateDefaults;
        return ret;
    }

    function onClick(e, el, opt) {
        var p = picker.menu.picker;
        p.activeDate = p.activeDate.getFirstDateOfMonth();
        if (p.value) {
            p.value = p.value.getFirstDateOfMonth();
        }

        p.showMonthPicker();

        if (!p.disabled) {
            p.monthPicker.stopFx();
            p.monthPicker.show();
            // if you want to click,you can the dblclick event change click
            p.mun(p.monthPicker, 'click', p.onMonthClick, p);
            p.mun(p.monthPicker, 'click', p.onMonthDblClick, p);
            p.onMonthClick = p.onMonthClick.createSequence(pickerClick);
            p.onMonthDblClick = p.onMonthDblClick.createSequence(pickerDblclick);
            p.mon(p.monthPicker, 'click', p.onMonthClick, p);
            p.mon(p.monthPicker, 'click', p.onMonthDblClick, p);
        }
    }

    function pickerClick(e, t) {
        var el = new Ext.Element(t);
        if (el.is('button.x-date-mp-cancel')) {
            picker.menu.hide();
        } else if(el.is('button.x-date-mp-ok')) {
            var p = picker.menu.picker;
            p.setValue(p.activeDate);
            p.fireEvent('select', p, p.value);
        }
    }

    function pickerDblclick(e, t) {
        var el = new Ext.Element(t);
        if (el.parent()
            && (el.parent().is('td.x-date-mp-month')
            || el.parent().is('td.x-date-mp-year'))) {

            var p = picker.menu.picker;
            p.setValue(p.activeDate);
            p.fireEvent('select', p, p.value);
        }
    }
};