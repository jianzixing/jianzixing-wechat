Ext.define('UXApp.grid.CustomerGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.customergrid',

    requires: [
        'UXApp.grid.SubTable'
    ],

    width: 700,
    height: 400,

    themes: {
        classic: {
            headerWidth: 24
        },
        neptune: {
            headerWidth: 24
        },
        "neptune-touch": {
            headerWidth: 30
        }
    },
    //</example>

    constructor: function (config) {
        var childColumns = this.childColumns,
            childField = this.childField;
        if (childColumns) {
            config = Ext.apply({
                plugins: {
                    ptype: "subtable",
                    association: childField,
                    headerWidth: this.themeInfo.headerWidth,
                    columns: childColumns
                }
            }, config);
        }
        this.callParent([config]);
    },

    /**
     * store :
     * {
     *          autoLoad: true,
     *          proxy: {
     *              type: 'memory',
     *              data: [{
     *                  "id": 1,
     *                  "name": "Bread Barn",
     *                  "phone": "8436-365-256",
     *                  "orders": [{
     *                      "id": 1,
     *                      "date": "2010-08-13",
     *                      "customerId": 1
     *                  }, {
     *                      "id": 2,
     *                      "date": "2010-07-14",
     *                      "customerId": 1
     *                  }]
     *              }, {
     *                  "id": 2,
     *                  "name": "Icecream Island",
     *                  "phone": "8452-389-719",
     *                  "orders": [{
     *                      "id": 3,
     *                      "date": "2010-01-22",
     *                      "customerId": 2
     *                  }, {
     *                      "id": 4,
     *                      "date": "2010-11-06",
     *                      "customerId": 2
     *                  }]
     *              }, {
     *                  "id": 3,
     *                  "name": "Pizza Palace",
     *                  "phone": "9378-255-743",
     *                  "orders": [{
     *                      "id": 5,
     *                      "date": "2010-12-29",
     *                      "customerId": 3
     *                  }, {
     *                      "id": 6,
     *                      "date": "2010-03-03",
     *                      "customerId": 3
     *                  }]
     *              }]
     *          },
     *          model: 'KitchenSink.model.Customer'
     *      }
     */
    initComponent: function () {
        this.callParent();
    }
});
