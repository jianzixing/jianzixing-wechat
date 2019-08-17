Ext.define('App.BaseAppDashboardView', {
    extend: 'Ext.panel.Panel',

    border: false,
    defaultListenerScope: true,
    autoScroll: true,
    apis: Global.API_INIT_LIST,
    bodyStyle: {
        // backgroundColor: '#F6F6F6'
    },

    items: [
        {
            xtype: 'panel',
            border: false,
            style: {
                marginTop: '20px'
            },
            width: '100%',
            height: 320,
            items: [
                {
                    xtype: 'panel',
                    border: false,
                    width: '30%',
                    style: {
                        margin: '10px 3% auto 2%'
                    },
                    height: 300,
                    layout: 'fit',
                    cls: 'shadow',
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'top',
                            items: [
                                {
                                    xtype: 'label',
                                    html: '<div>' +
                                        '<div style="float: left;font-size: 16px;line-height: 36px;margin-left: 10px">当前用户数量</div>' +
                                        '<div style="float: left;width: 13px;height:13px;border:1px #E08031 solid;border-radius: 13px;margin: 13px 0px 0px 6px"></div>' +
                                        '</div>'
                                },
                                '->',
                                {
                                    xtype: 'label',
                                    name: 'label_box_pv_btn',
                                    margin: '0 10px 0px 10px',
                                    html: '<div class="label_box_pv_btn" style="cursor:pointer;width: 60px;height: 28px;border-radius: 28px;border:1px solid #909090;' +
                                        'text-align: center;line-height: 28px;font-size: 12px">' +
                                        '刷新' +
                                        '</div>'
                                }
                            ]
                        }
                    ],
                    items: [
                        {
                            xtype: 'container',
                            name: 'label_box_pv_cnt',
                            html: '<div class="label_box_pv_cnt" style="width: 100%;height: 100%;text-align: center;line-height: 240px;font-size: 60px">0</div>'
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    border: false,
                    width: '30%',
                    style: {
                        margin: '10px 3% auto 0px'
                    },
                    height: 300,
                    layout: 'fit',
                    cls: 'shadow',
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'top',
                            items: [
                                {
                                    xtype: 'label',
                                    html: '<div>' +
                                        '<div style="float: left;font-size: 16px;line-height: 36px;margin-left: 10px">上架商品数量</div>' +
                                        '<div style="float: left;width: 13px;height:13px;border:1px #FF5983 solid;border-radius: 13px;margin: 13px 0px 0px 6px"></div>' +
                                        '</div>'
                                },
                                '->',
                                {
                                    xtype: 'label',
                                    name: 'label_box_uv_btn',
                                    margin: '0 10px 0px 10px',
                                    html: '<div class="label_box_uv_btn" style="cursor:pointer;width: 60px;height: 28px;border-radius: 28px;border:1px solid #909090;text-align: center;line-height: 28px;font-size: 12px">' +
                                        '刷新' +
                                        '</div>'
                                }
                            ]
                        }
                    ],
                    items: [
                        {
                            xtype: 'container',
                            name: 'label_box_uv_cnt',
                            html: '<div class="label_box_uv_cnt" style="width: 100%;height: 100%;text-align: center;line-height: 240px;font-size: 60px">0</div>'
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    border: false,
                    width: '30%',
                    style: {
                        margin: '10px 2% auto 0px'
                    },
                    height: 300,
                    layout: 'fit',
                    cls: 'shadow',
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'top',
                            items: [
                                {
                                    xtype: 'label',
                                    html: '<div>' +
                                        '<div style="float: left;font-size: 16px;line-height: 36px;margin-left: 10px">未发货订单数量</div>' +
                                        '<div style="float: left;width: 13px;height:13px;border:1px #199475 solid;border-radius: 13px;margin: 13px 0px 0px 6px"></div>' +
                                        '</div>'
                                },
                                '->',
                                {
                                    xtype: 'label',
                                    name: 'label_box_iv_btn',
                                    margin: '0 10px 0px 10px',
                                    html: '<div class="label_box_iv_btn" style="cursor:pointer;width: 60px;height: 28px;border-radius: 28px;border:1px solid #909090;text-align: center;line-height: 28px;font-size: 12px">' +
                                        '刷新' +
                                        '</div>'
                                }
                            ]
                        }
                    ],
                    items: [
                        {
                            xtype: 'container',
                            name: 'label_box_iv_cnt',
                            html: '<div class="label_box_iv_cnt" style="width: 100%;height: 100%;text-align: center;line-height: 240px;font-size: 60px">0</div>'
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'panel',
            border: false,
            style: {
                margin: '20px 2% 30px 2%'
            },
            width: '96%',
            height: 450,
            layout: 'fit',
            cls: 'shadow',
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'label',
                            html: '<div>' +
                                '<div style="float: left;font-size: 16px;line-height: 36px;margin-left: 10px">访问统计</div>' +
                                '<div style="float: left;width: 13px;height:13px;border:1px #6ce26c solid;border-radius: 13px;margin: 13px 0px 0px 6px"></div>' +
                                '</div>'
                        },
                        '->',
                        {
                            xtype: 'label',
                            name: 'label_box_chart_today',
                            html: '<div class="label_box_chart_today" style="cursor:pointer;width: 80px;height: 28px;border-radius: 28px;border:1px solid #909090;text-align: center;line-height: 28px;font-size: 12px">' +
                                '今日统计' +
                                '</div>'
                        },
                        {
                            xtype: 'label',
                            name: 'label_box_chart_seven',
                            margin: '0 30px 0px 10px',
                            html: '<div class="label_box_chart_seven" style="cursor:pointer;width: 80px;height: 28px;border-radius: 28px;border:1px solid #909090;text-align: center;line-height: 28px;font-size: 12px">' +
                                '七日统计' +
                                '</div>'
                        }
                    ]
                }
            ],
            items: [
                {
                    xtype: 'container',
                    name: 'view'
                }
            ]
        }
    ],
    listeners: {
        afterrender: 'onPanelAfterRender',
        resize: 'onViewResize'
    },

    onPanelAfterRender: function (component, eOpts) {
        self._loadSevenDayFlag = false;
        this.initViewChart();
        this.initEvent();
    },

    initEvent: function () {
        var self = this;
        var pvBtn = this.find('label_box_pv_btn');
        var uvBtn = this.find('label_box_uv_btn');
        var ivBtn = this.find('label_box_iv_btn');
        var chartToday = this.find('label_box_chart_today');
        var chartSeven = this.find('label_box_chart_seven');

        pvBtn.getEl().selectNode('.label_box_pv_btn', false).on('click', function () {
            self.reloadVNumber();
        });
        uvBtn.getEl().selectNode('.label_box_uv_btn', false).on('click', function () {
            self.reloadVNumber();
        });
        ivBtn.getEl().selectNode('.label_box_iv_btn', false).on('click', function () {
            self.reloadVNumber();
        });
        chartToday.getEl().selectNode('.label_box_chart_today', false).on('click', function () {
            self._loadSevenDayFlag = false;
            self.apis.Statistics.getTodayDayHours.call({}, function (data) {
                self.setTodayHoursViews(data);
            });
        });
        chartSeven.getEl().selectNode('.label_box_chart_seven', false).on('click', function () {
            self._loadSevenDayFlag = true;
            self.apis.Statistics.getSevenDay.call({}, function (data) {
                self.setSevenDayViews(data);
            });
        });
    },

    reloadVNumber: function () {
        var self = this;
        self.apis.Admin.getTimerTaskData.call({}, function (response) {
            if (response) {
                var todayViews = response['todayViews'];
                var todayHoursViews = response['todayHoursViews'];

                var userCount = response['userCount'];
                var goodsCount = response['goodsCount'];
                var orderCount = response['orderCount'];

                self.setTodayViews(todayViews);
                self.setTodayHoursViews(todayHoursViews);
            }
        });
    },

    setPVNumber: function (count) {
        var pvCnt = this.find('label_box_pv_cnt');
        pvCnt.getEl().selectNode('.label_box_pv_cnt', false).setHtml("" + count);
    },

    setUVNumber: function (count) {
        var pvCnt = this.find('label_box_uv_cnt');
        pvCnt.getEl().selectNode('.label_box_uv_cnt', false).setHtml("" + count);
    },

    setIVNumber: function (count) {
        var pvCnt = this.find('label_box_iv_cnt');
        pvCnt.getEl().selectNode('.label_box_iv_cnt', false).setHtml("" + count);
    },

    initViewChart: function () {
        var option = {
            title: {
                text: '堆叠区域图',
                show: false
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross',
                    label: {
                        backgroundColor: '#6a7985'
                    }
                }
            },
            legend: {
                itemGap: 70,
                data: ['PV', 'UV', 'IV']
            },
            grid: {
                left: '0%',
                right: '30px',
                bottom: '18px',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    boundaryGap: false,
                    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    show: false,
                    splitLine: {
                        show: false
                    }
                }
            ],
            series: [
                {
                    name: 'PV',
                    type: 'line',
                    stack: '总量',
                    smooth: true,
                    areaStyle: {normal: {}},
                    data: [0, 0, 0, 0, 0, 0, 0]
                },
                {
                    name: 'UV',
                    type: 'line',
                    stack: '总量',
                    smooth: true,
                    areaStyle: {normal: {}},
                    data: [0, 0, 0, 0, 0, 0, 0]
                },
                {
                    name: 'IV',
                    type: 'line',
                    stack: '总量',
                    smooth: true,
                    areaStyle: {normal: {}},
                    data: [0, 0, 0, 0, 0, 0, 0]
                }
            ]
        };

        var view = this.find("view");
        var id = view.id + "-innerCt";
        var chart = echarts.init(document.getElementById(id));
        chart.setOption(option);
        this._viewChart = chart;
    },

    onViewResize: function (me, width, height, oldWidth, oldHeight, eOpts) {
        this._viewChart.resize()
    },

    updateTimerData: function (data) {
        var todayViews = data['todayViews'];
        var todayHoursViews = data['todayHoursViews'];
        this.setTodayViews(todayViews);
        this.setTodayHoursViews(todayHoursViews);
    },

    setTodayViews: function (data) {
        var pv = data['userCount'];
        var uv = data['goodsCount'];
        var iv = data['orderCount'];
        this.setPVNumber(pv);
        this.setUVNumber(uv);
        this.setIVNumber(iv);
    },

    setSevenDayViews: function (data) {
        if (data) {
            var pvData = [];
            var uvData = [];
            var ivData = [];
            var xAxis = [];
            for (var i = 6; i >= 0; i--) {
                var d = data[i];
                if (d) {
                    xAxis.push((new Date(d['time'])).format("dd日"));
                    pvData.push(d['pv'] || 0);
                    uvData.push(d['uv'] || 0);
                    ivData.push(d['iv'] || 0);
                } else {
                    xAxis.push('无数据');
                    pvData.push(0);
                    uvData.push(0);
                    ivData.push(0);
                }
            }
            this._viewChart.setOption({
                xAxis: [
                    {
                        type: 'category',
                        boundaryGap: false,
                        data: xAxis
                    }
                ],
                series: [
                    {
                        name: 'PV',
                        type: 'line',
                        stack: '总量',
                        smooth: true,
                        areaStyle: {normal: {}},
                        data: pvData
                    },
                    {
                        name: 'UV',
                        type: 'line',
                        stack: '总量',
                        smooth: true,
                        areaStyle: {normal: {}},
                        data: uvData
                    },
                    {
                        name: 'IV',
                        type: 'line',
                        stack: '总量',
                        smooth: true,
                        areaStyle: {normal: {}},
                        data: ivData
                    }
                ]
            });
        }
    },

    setTodayHoursViews: function (data) {
        if (this._loadSevenDayFlag) {
            return;
        }
        if (data) {
            var pvData = [];
            var uvData = [];
            var ivData = [];
            var xAxis = [];
            for (var i = 23; i >= 0; i--) {
                var d = data[i];
                if (d) {
                    xAxis.push((new Date(d['time'])).format("HH时"));
                    pvData.push(d['pv'] || 0);
                    uvData.push(d['uv'] || 0);
                    ivData.push(d['iv'] || 0);
                } else {
                    xAxis.push('无数据');
                    pvData.push(0);
                    uvData.push(0);
                    ivData.push(0);
                }
            }
            this._viewChart.setOption({
                xAxis: [
                    {
                        type: 'category',
                        boundaryGap: false,
                        data: xAxis
                    }
                ],
                series: [
                    {
                        name: 'PV',
                        type: 'line',
                        stack: '总量',
                        smooth: true,
                        areaStyle: {normal: {}},
                        data: pvData
                    },
                    {
                        name: 'UV',
                        type: 'line',
                        stack: '总量',
                        smooth: true,
                        areaStyle: {normal: {}},
                        data: uvData
                    },
                    {
                        name: 'IV',
                        type: 'line',
                        stack: '总量',
                        smooth: true,
                        areaStyle: {normal: {}},
                        data: ivData
                    }
                ]
            });
        } else {
            var data = [];
            var xAxis = [];
            var time = (new Date()).getTime();
            for (var i = 0; i < 24; i++) {
                xAxis.push((new Date(time - i * 1 * 60 * 60 * 1000)).format('HH时'));
                data.push(0);
            }

            this._viewChart.setOption({
                xAxis: [
                    {
                        type: 'category',
                        boundaryGap: false,
                        data: xAxis
                    }
                ],
                series: [
                    {
                        name: 'PV',
                        type: 'line',
                        stack: '总量',
                        smooth: true,
                        areaStyle: {normal: {}},
                        data: data
                    },
                    {
                        name: 'UV',
                        type: 'line',
                        stack: '总量',
                        smooth: true,
                        areaStyle: {normal: {}},
                        data: data
                    },
                    {
                        name: 'IV',
                        type: 'line',
                        stack: '总量',
                        smooth: true,
                        areaStyle: {normal: {}},
                        data: data
                    }
                ]
            });
        }
    }
});
