Ext.define('App.wechat.WeChatMenuManager', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel'
    ],

    border: false,
    layout: 'auto',
    header: false,
    autoScroll: true,
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'saveTempConfig'),
                    text: '保存',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'refresh'),
                    text: '保存并发布',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                '->'
            ]
        }
    ],
    items: [
        {
            xtype: 'panel',
            border: false,
            header: false,
            width: 1050,
            height: 620,
            layout: 'border',
            style: {
                marginTop: '20px',
                marginBottom: '15px',
                marginLeft: 'auto',
                marginRight: 'auto'
            },
            items: [
                {
                    xtype: 'panel',
                    name: 'menu_info',
                    region: 'center',
                    border: false,
                    layout: 'fit',
                    header: false,
                    style: {
                        overflow: 'visible'
                    },
                    bodyStyle: {
                        overflow: 'visible'
                    },
                    cls: 'set-overflow-visible',
                    html: '<span style="top: 545px;left: -11px;z-index:999;position: absolute;">' +
                    '<i style="position: absolute;display: inline-block;width: 0;height: 0;' +
                    'border-width: 12px;border-style: dashed;border-color: transparent;left: 0;' +
                    'border-left-width: 0;border-right-color: #e7e7eb;border-right-style: solid;"></i>' +
                    '<i style="position: absolute;left: 1px;display: inline-block;width: 0;height: 0;border-width: 12px;' +
                    'border-style: dashed;border-color: transparent;border-right-color: #f4f5f9;border-left-width: 0;border-right-style: solid"></i>' +
                    '</span>',
                    items: [
                        {
                            xtype: 'form',
                            border: false,
                            bodyPadding: 20,
                            margin: '20px 0px 0px 0px',
                            style: {
                                border: '1px solid #e7e7eb'
                            },
                            bodyStyle: {
                                backgroundColor: '#f4f5f9',
                            },
                            header: false,
                            items: [
                                {
                                    xtype: 'container',
                                    name: 'info_title',
                                    html: [
                                        '<div style="width: 100%;height: 33px;margin-bottom: 20px;font-family: "-apple-system-font,BlinkMacSystemFont","Helvetica Neue","PingFang SC","Hiragino Sans GB","Microsoft YaHei UI","Microsoft YaHei",Arial,sans-serif">',
                                        '<div style="width: 100%;border-bottom: 1px solid #e7e7eb;height: 33px;overflow: hidden">',
                                        '<div myName="title" style="font-size: 14px;color:#353535;float: left;font-weight: 400;">菜单名称</div>',
                                        '<div myName="delete" style="color: #576b95;float: right;text-align: right;font-size: 14px;cursor: pointer;font-weight: 400">删除子菜单</div>',
                                        '</div>',
                                        '</div>'
                                    ]
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'info_menu_name',
                                    width: 390,
                                    fieldLabel: '菜单名称',
                                    listeners: {
                                        change: 'onTextfieldChange'
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: '&nbsp;',
                                    labelSeparator: ' ',
                                    value: '字数不超过8个汉字或16个字母'
                                },
                                {
                                    xtype: 'numberfield',
                                    name: 'info_menu_pos',
                                    width: 390,
                                    allowDecimals: false,
                                    hideTrigger: true,
                                    maxValue: 1000,
                                    minValue: 1,
                                    fieldLabel: '菜单排序',
                                    value: '1',
                                    listeners: {
                                        blur: 'onTextfieldBlur'
                                    }
                                },
                                {
                                    xtype: 'radiogroup',
                                    fieldLabel: '菜单内容',
                                    items: [
                                        {
                                            xtype: 'radiofield',
                                            boxLabel: '关键字'
                                        },
                                        {
                                            xtype: 'radiofield',
                                            boxLabel: '跳转网页'
                                        },
                                        {
                                            xtype: 'radiofield',
                                            boxLabel: '事件功能'
                                        },
                                        {
                                            xtype: 'radiofield',
                                            boxLabel: '小程序'
                                        }
                                    ],
                                    listeners: {
                                        change: 'onRadiogroupChange'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    name: 'mobile',
                    region: 'west',
                    border: false,
                    width: 400,
                    height: 620,
                    header: false
                }
            ]
        }
    ],

    onSaveClick: function (button, e, eOpts) {

    },

    onTextfieldBlur: function (component, event, eOpts) {
        var field = this.find('info_menu_pos');
        var value = field.getValue();
        if (value && value != '' && parseInt(value) >= 1 && parseInt(value) <= 1000) {
            var menu = this._currentSelectMenu;

            if (this._currentSelectMenu._menuData) {
                this._currentSelectMenu._menuData['pos'] = value;
            } else {
                this._currentSelectMenu._menuData = {pos: value};
            }

            var parent = menu.parentNode;
            this.resetMenuSort(parent);
        } else {
            Dialog.alert('排序数字只允许1到1000之间的数字');
        }
    },

    resetMenuSort: function (parent) {
        var children = parent.children;

        var sort = [];
        for (var i in children) {
            if (children[i].parentNode) {
                var dt = children[i]['_menuData'];
                if (!dt) {
                    children[i]['_menuData'] = {pos: 1};
                    dt = children[i]['_menuData'];
                }
                sort.push({dt: dt, node: children[i]});
            }
        }
        sort.sort(function (a, b) {
            if (parseInt(a.dt.pos) > parseInt(b.dt.pos)) {
                return 1;
            } else if (parseInt(a.dt.pos) < parseInt(b.dt.pos)) {
                return -1;
            } else {
                return 0;
            }
        });

        for (var i in sort) {
            parent.removeChild(sort[i]['node']);
        }
        for (var i in sort) {
            parent.appendChild(sort[i]['node']);
        }
    },

    onTextfieldChange: function (field, newValue, oldValue, eOpts) {
        var title = this.find('info_title');
        var titleDom = title.getEl().selectNode('[myName=title]', false);
        if (!newValue || newValue == '') {
            if (this._currentSelectMenu._menuType == 'sub') {
                titleDom.setHtml('子菜单名称');
            } else {
                titleDom.setHtml('菜单名称');
            }
        } else {
            var menu = this._currentSelectMenu;
            menu.setMenuName(newValue);
            titleDom.setHtml(newValue);
        }
    },

    onRadiogroupChange: function (field, newValue, oldValue, eOpts) {

    },

    onMenuSelected: function (dom) {
        if (dom) {
            var title = this.find('info_title');
            var pos = this.find('info_menu_pos');
            var titleDom = title.getEl().selectNode('[myName=title]', false);
            var name = this.find('info_menu_name');
            var menuData = dom._menuData;
            if (!menuData) {
                dom._menuData = {pos: 1};
                menuData = {};
            }

            var menuTypeName = dom._menuType == 'sub' ? '子菜单名称' : '菜单名称';
            var menuName = menuData['name'] || menuTypeName;
            var menuPos = menuData['pos'] || 1;

            name.setFieldLabel(menuTypeName);
            titleDom.setHtml(menuName);
            name.setValue(menuName);
            pos.setValue(menuPos);
        }
    },

    initPanelEvent: function () {
        var self = this;
        var title = this.find('info_title');
        var delDom = title.getEl().selectNode('[myName=delete]', false);
        delDom.on('click', function () {
            Dialog.confirm('提示', '确定删除当前菜单？删除后点击保存并发布即为生效！只保存不会同步到公众号上！', function (btn) {
                if (btn == 'yes') {
                    if (self._currentSelectMenu && self._currentSelectMenu._menuType == 'tool') {
                        self.setRemoveToolMenu(self._currentSelectMenu);
                    }
                    if (self._currentSelectMenu && self._currentSelectMenu._menuType == 'sub') {
                        self.setRemoveSubMenu(self._currentSelectMenu);
                    }
                }
            })
        });
    },

    initMobileView: function () {
        var mobileView = this.find('mobile');
        var html = [];
        var data = [];
        var self = this;

        var funAddToolItem = function (opt) {
            var opt = opt || {};
            var isIcon = opt['icon'];
            var isSelected = opt['selected'];
            var isShowMenu = opt['menu'];
            var subItems = opt['subs'];

            var toolElements = mobileView.getEl().selectNode('[myName=tools]', true);
            var itemHtml = document.createElement('li');
            itemHtml._menuType = 'tool';
            var ihBox = document.createElement('div');
            ihBox.style.width = '100%';
            ihBox.style.height = '100%';
            ihBox.style.textOverflow = 'ellipsis';
            ihBox.style.whiteSpace = 'nowrap';
            ihBox.style.wordWrap = 'normal';
            ihBox.style.overflow = 'hidden';
            itemHtml.appendChild(ihBox);
            var width = 100 / (toolElements.children.length + 1) + '%';
            var tools = mobileView.getEl().selectNode('[myName=tools]', false);
            itemHtml.style.position = 'relative';
            itemHtml.style.width = width;
            itemHtml.style.height = '48px';
            itemHtml.style.float = 'left';
            itemHtml.style.textAlign = 'center';
            itemHtml.style.cursor = 'pointer';

            itemHtml.style.color = '#616161';
            itemHtml.style.textDecoration = 'none';
            itemHtml.style.fontSize = '14px';
            itemHtml.style.fontWeight = '500';
            itemHtml.style.fontFamily = '-apple-system-font,BlinkMacSystemFont,"Helvetica Neue","PingFang SC","Hiragino Sans GB","Microsoft YaHei UI","Microsoft YaHei",Arial,sans-serif';
            itemHtml.style.lineHeight = '50px';
            for (var i in toolElements.children) {
                if (toolElements.children[i].parentNode) {
                    toolElements.children[i].style.width = width;
                    if (!!!toolElements.children[i]._is_selected) {
                        toolElements.children[i].style.borderRight = '1px solid #e7e7eb';
                    }
                }
            }

            if (isIcon) {
                var i = document.createElement("i");
                i.style.width = '7px';
                i.style.height = '7px';
                i.style.background = 'url(image/wechat/add.png) 0 -36px no-repeat';
                i.style.display = 'inline-block';
                ihBox.appendChild(i);
            }

            var span = document.createElement('span');
            span.innerHTML = "菜单名称";
            span.style.marginLeft = '5px';
            ihBox.appendChild(span);
            tools.appendChild(itemHtml);
            itemHtml._textSpan = span;
            itemHtml.setMenuName = function (v) {
                if (v) {
                    this._textSpan.innerHTML = v;
                    if (!this._menuData) this._menuData = {pos: 1, name: v};
                    else this._menuData.name = v;
                }
            };

            var subMenu = document.createElement('div');
            subMenu.style.position = 'absolute';
            subMenu.style.bottom = '60px';
            subMenu.style.backgroundColor = '#fafafa';
            subMenu.style.width = '100%';
            subMenu.style.minHeight = '50px';
            subMenu.style.display = 'none';
            subMenu.style.border = '1px solid #d0d0d0';

            var ul = document.createElement('ul');
            ul.style.position = 'relative';
            ul.style.margin = '0px';
            ul.style.padding = '0px';
            ul.style.listStyleType = 'none';
            ul.style.width = '100%';
            ul.style.border = '1px solid #d0d0d0;';
            ul.style.backgroundColor = '#fafafa';
            subMenu.appendChild(ul);
            itemHtml._sub_menu_list = ul;


            var arrowOut = document.createElement('i');
            arrowOut.style.position = 'absolute';
            arrowOut.style.display = 'inline-block';
            arrowOut.style.bottom = '-6px';
            arrowOut.style.left = '50%';
            arrowOut.style.marginLeft = '-6px';
            arrowOut.style.width = '0';
            arrowOut.style.height = '0';
            arrowOut.style.borderWidth = '6px';
            arrowOut.style.borderStyle = 'dashed';
            arrowOut.style.borderColor = 'transparent';
            arrowOut.style.borderBottomWidth = '0px';
            arrowOut.style.borderTopColor = '#d0d0d0';
            arrowOut.style.borderTopStyle = 'solid';
            subMenu.appendChild(arrowOut);
            itemHtml.appendChild(subMenu);
            itemHtml._sub_menu = subMenu;

            var arrowIn = document.createElement('i');
            arrowIn.style.position = 'absolute';
            arrowIn.style.display = 'inline-block';
            arrowIn.style.bottom = '-5px';
            arrowIn.style.left = '50%';
            arrowIn.style.marginLeft = '-6px';
            arrowIn.style.width = '0';
            arrowIn.style.height = '0';
            arrowIn.style.borderWidth = '6px';
            arrowIn.style.borderStyle = 'dashed';
            arrowIn.style.borderColor = 'transparent';
            arrowIn.style.borderBottomWidth = '0px';
            arrowIn.style.borderTopColor = '#fafafa';
            arrowIn.style.borderTopStyle = 'solid';
            subMenu.appendChild(arrowIn);

            itemHtml.onmouseover = function (ev) {
                ev.stopPropagation();
                if (!this._is_selected) {
                    this.style.color = '#333333';
                }
            };

            itemHtml.onmouseout = function (ev) {
                ev.stopPropagation();
                if (!this._is_selected) {
                    this.style.color = '#616161';
                }
            };

            if (isSelected) {
                setItemsUnSelect();
                funSetSelect(itemHtml);
            }

            setItemMenusHidden(itemHtml);

            if (isShowMenu) {
                if (subItems) {
                    for (var k in subItems) {
                        funAddSubMenu(ul, subItems[k]);
                    }
                    if (subItems.length < 5) {
                        funAddSubMenu(ul, {isAddButton: true, onclick: opt['subAddOnClick']});
                    }
                } else {
                    funAddSubMenu(ul, {isAddButton: true, onclick: opt['subAddOnClick']});
                }
                setItemMenuShow(itemHtml);
            }
            itemHtml.onclick = function (ev) {
                setItemMenusHidden(this);
                setItemMenuShow(this);
                setItemsUnSelect();
                funSetSelect(this);
            };

            self.resetMenuSort(toolElements);
        };

        var setItemsUnSelect = function () {
            setSubItemUnSelect();
            var toolElements = mobileView.getEl().selectNode('[myName=tools]', true);
            if (toolElements) {
                for (var i in toolElements.children) {
                    if (toolElements.children[i].parentNode) {
                        funSetSelect(toolElements.children[i], true);
                    }
                }
            }
        };

        var setItemMenusHidden = function (dom) {
            var toolElements = mobileView.getEl().selectNode('[myName=tools]', true);
            if (toolElements) {
                for (var i in toolElements.children) {
                    if (toolElements.children[i] != dom) {
                        if (toolElements.children[i]._sub_menu) {
                            toolElements.children[i]._sub_menu.style.display = 'none';
                        }
                    }
                }
            }
        };

        var setItemMenuShow = function (dom) {
            if (dom._sub_menu) {
                dom._sub_menu.style.display = 'block';
            }
        };

        var funAddButton = function () {
            var toolElements = mobileView.getEl().selectNode('[myName=tools]', true);

            var addButton = document.createElement('li');
            addButton._menuType = 'toolButton';
            addButton._menuData = {pos: 2147483647};
            var img = document.createElement('i');
            var width = 100 / (toolElements.children.length + 1) + '%';
            var tools = mobileView.getEl().selectNode('[myName=tools]', false);
            addButton.style.width = width;
            addButton.style.position = 'relative';
            addButton.style.height = '48px';
            addButton.style.float = 'left';
            addButton.style.textAlign = 'center';
            addButton.style.padding = '16px';
            addButton.style.cursor = 'pointer';
            for (var i in toolElements.children) {
                if (toolElements.children[i].parentNode) {
                    toolElements.children[i].style.width = width;
                    if (!!!toolElements.children[i]._is_selected) {
                        toolElements.children[i].style.borderRight = '1px solid #e7e7eb';
                    }
                }
            }

            img.style.width = '14px';
            img.style.height = '14px';
            img.style.background = 'url(image/wechat/add.png) 0 0 no-repeat';
            img.style.display = 'inline-block';
            img.style.verticalAlign = 'middle';
            img.style.marginTop = '-4px';
            addButton.appendChild(img);
            addButton._img_el = img;

            if (toolElements.children.length == 0) {
                addButton.style.color = '#44b549';
                img.style.background = 'url(image/wechat/add.png) 0 -18px no-repeat';
                var span = document.createElement('span');
                span.innerHTML = '添加菜单';
                span.style.marginLeft = '10px';
                span.style.fontSize = '15px';
                span.style.fontWeight = 'bold';
                addButton.appendChild(span);
                addButton._text_el = span;
            }

            tools.appendChild(addButton);

            addButton.onclick = function (ev) {
                if (this._text_el) {
                    this.removeChild(this._text_el);
                    this._text_el = null;
                }
                this._img_el.style.background = 'url(image/wechat/add.png) 0 0 no-repeat';
                this.style.color = '#616161';
                if (toolElements.children.length > 3) {
                    Dialog.alert('菜单只允许最多3个');
                } else {
                    var isRemoveAddButton = false;
                    if (toolElements.children.length == 3) {
                        var tools = mobileView.getEl().selectNode('[myName=tools]', true);
                        tools.removeChild(this);
                        isRemoveAddButton = true;
                    }

                    funAddToolItem({menu: false, selected: true, menu: true});
                    if (toolElements.children.length >= 2 && !isRemoveAddButton) {
                        var tools = mobileView.getEl().selectNode('[myName=tools]', true);
                        var toolButton = null;
                        for (var m in toolElements.children) {
                            if (toolElements.children[m].parentNode
                                && toolElements.children[m]['_menuType'] == 'toolButton') {
                                toolButton = toolElements.children[m]
                                tools.removeChild(toolElements.children[m]);
                            }
                        }
                        tools.appendChild(toolButton);
                    }
                }
            }
        };

        var funSetSelect = function (item, isUnSelect) {
            if (item) {
                var toolElements = mobileView.getEl().selectNode('[myName=tools]', true);
                if (!isUnSelect) {
                    for (var i in toolElements.children) {
                        toolElements.children[i]._is_selected = false;
                    }
                    item._is_selected = true;
                    item.style.border = '1px solid #44b549';
                    item.style.color = '#44b549';
                    item.style.background = '#fff';

                    self._currentSelectMenu = item;
                    self.onMenuSelected(self._currentSelectMenu);
                } else {
                    item._is_selected = false;
                    item.style.border = null;
                    item.style.borderRight = '1px solid #e7e7eb';
                    item.style.color = '#616161';
                    item.style.background = '#fafafa';
                }
            }
        };

        var funAddSubMenu = function (item, opt) {
            var opt = opt || {};
            var isAddButton = opt['isAddButton'];
            var text = opt['text'] || '子菜单名称';

            var sub = document.createElement('li');
            // sub.style.width = '100%';
            sub.style.height = '48px';
            sub.style.textOverflow = 'ellipsis';
            sub.style.whiteSpace = 'nowrap';
            sub.style.wordWrap = 'normal';
            sub.style.overflow = 'hidden';
            sub.style.padding = '0 6px';
            sub.style.fontWeight = 'normal';
            sub.style.fontSize = '14px';
            sub.style.color = '#616161';
            // sub.style.margin = '0 -1px';
            // sub.style.position = 'relative';
            // sub.style.zIndex = '10';

            var border = document.createElement('span');
            border.style.display = 'block';
            border.style.borderTop = '0px solid #e7e7eb';
            border.style.width = 'auto';
            border.style.overflow = 'hidden';
            border.style.textOverflow = "ellipsis";
            border.style.whiteSpace = "nowrap";
            border.style.wordWrap = "normal";
            border.style.cursor = "pointer";
            if (isAddButton) {
                sub._menuType = 'subButton';
                sub._menuData = {pos: 2147483647};
                var img = document.createElement('i');
                img.style.width = '14px';
                img.style.height = '14px';
                img.style.background = 'url(image/wechat/add.png) 0 0 no-repeat';
                img.style.display = 'inline-block';
                img.style.verticalAlign = 'middle';
                img.style.marginTop = '17px';
                border.appendChild(img);
            } else {
                sub._menuType = 'sub';
                var span = document.createElement('span');
                span.innerHTML = text;
                border.appendChild(span);
                sub._textSpan = span;
                sub.setMenuName = function (v) {
                    if (v) {
                        this._textSpan.innerHTML = v;
                        if (!this._menuData) this._menuData = {pos: 1, name: v};
                        else this._menuData.name = v;
                    }
                }
            }
            sub._parentEl = item;
            sub._boderEl = border;
            sub.appendChild(border);
            item.appendChild(sub);

            sub.onmouseover = function (ev) {
                ev.stopPropagation();
                this.style.backgroundColor = '#eee';
            };

            sub.onmouseout = function (ev) {
                ev.stopPropagation();
                this.style.backgroundColor = '#fafafa';
            };

            if (isAddButton) {
                sub.onclick = function (ev) {
                    ev.stopPropagation();
                    var childrens = this._parentEl.children;
                    var isRemovedFromEl = false;
                    if (childrens.length == 5) {
                        this._parentEl.removeChild(this);
                        isRemovedFromEl = true;
                    }
                    funAddSubMenu(this._parentEl);
                    if (!isRemovedFromEl) {
                        var childrens = this._parentEl.children;
                        if (childrens) {
                            for (var j in childrens) {
                                var ch = childrens[j];
                                if (ch._boderEl && j == 0) {
                                    ch._boderEl.style.borderTop = '0px solid #e7e7eb';
                                }
                                if (ch._boderEl && j != 0) {
                                    ch._boderEl.style.borderTop = '1px solid #e7e7eb';
                                }
                            }
                        }
                    }
                }
            } else {
                sub.onclick = function (ev) {
                    ev.stopPropagation();
                    setSubItemSelect(this);
                }
            }

            self.resetMenuSort(item);
        };

        var setSubItemSelect = function (item) {
            var toolElements = mobileView.getEl().selectNode('[myName=tools]', true);
            if (toolElements) {
                setItemsUnSelect();
                for (var i in toolElements.children) {
                    var parentEl = toolElements.children[i]['_sub_menu_list'];
                    if (parentEl) {
                        var childrens = parentEl.children;
                        if (childrens) {
                            for (var j in childrens) {
                                var ch = childrens[j];
                                if (ch._boderEl) {
                                    ch.style.border = null;
                                    ch.style.color = '#616161';
                                    ch.style.background = '#fafafa';
                                }
                            }
                        }
                    }
                }
                item.style.border = '1px solid #44b549';
                item.style.color = '#44b549';
                item.style.background = '#fff';
                self._currentSelectMenu = item;
                self.onMenuSelected(self._currentSelectMenu);
            }
        };

        var setSubItemUnSelect = function () {
            var toolElements = mobileView.getEl().selectNode('[myName=tools]', true);
            if (toolElements) {
                for (var i in toolElements.children) {
                    var parentEl = toolElements.children[i]['_sub_menu_list'];
                    if (parentEl) {
                        var childrens = parentEl.children;
                        if (childrens) {
                            for (var j in childrens) {
                                var ch = childrens[j];
                                if (ch._boderEl) {
                                    ch.style.border = null;
                                    ch.style.color = '#616161';
                                    ch.style.background = '#fafafa';
                                }
                            }
                        }
                    }
                }
            }
        };

        self.setRemoveToolMenu = function (item) {
            if (item) {
                try {
                    var toolElements = mobileView.getEl().selectNode('[myName=tools]', true);
                    var children = item.parentNode.children;
                    var len = 0;
                    var hasButton = false;
                    for (var i = 0; i < children.length; i++) {
                        if (children[i]['_menuType'] == 'toolButton') {
                            hasButton = true;
                        }
                        if (children[i]['_menuType'] == 'tool') {
                            len++;
                        }
                    }

                    var p = item.parentNode;
                    p.removeChild(item);

                    if (!hasButton) {
                        funAddButton();
                    }
                    var width = 100 / toolElements.children.length + '%';
                    for (var i in toolElements.children) {
                        if (toolElements.children[i].parentNode) {
                            toolElements.children[i].style.width = width;
                        }
                    }
                    if (toolElements.children.length == 1 && toolElements.children[0]['_menuType'] == 'toolButton') {
                        var img = toolElements.children[0]['_img_el'];
                        toolElements.children[0].style.color = '#44b549';
                        img.style.background = 'url(image/wechat/add.png) 0 -18px no-repeat';
                        var span = document.createElement('span');
                        span.innerHTML = '添加菜单';
                        span.style.marginLeft = '10px';
                        span.style.fontSize = '15px';
                        span.style.fontWeight = 'bold';
                        toolElements.children[0].appendChild(span);
                        toolElements.children[0]._text_el = span;
                    }
                } catch (e) {
                }
            }
        };

        self.setRemoveSubMenu = function (item) {
            if (item) {
                try {
                    var children = item.parentNode.children;
                    var len = 0;
                    var hasButton = false;
                    for (var i = 0; i < children.length; i++) {
                        if (children[i]['_menuType'] == 'subButton') {
                            hasButton = true;
                        }
                        if (children[i]['_menuType'] == 'sub') {
                            len++;
                        }
                    }
                    var p = item.parentNode;
                    p.removeChild(item);
                    if (!hasButton) {
                        funAddSubMenu(item._parentEl, {isAddButton: true});
                    }
                } catch (e) {
                }
            }
        };

        html.push('<div style="width: 100%;height: 100%;overflow: auto;user-select: none">');

        html.push('<div style="width: 300px;height:580px;margin-left: auto;margin-right: auto;margin-top: 20px;border: 1px solid #e7e7eb;">');

        html.push('<div style="width: 300px;height: 60px;background:transparent url(image/wechat/bg_mobile_head.png) no-repeat;background-size: 300px 60px;text-align: center;line-height: 80px">');
        html.push('<span style="color: #ffffff;font-size: 15px;font-weight: bold">简子行</span>');
        html.push('</div>');

        html.push('<div style="width: 100%;height: 469px"></div>');

        html.push('<div style="width: 100%;height: 50px;border-top: 1px solid #e7e7eb;border-bottom: 1px solid #e7e7eb;background: transparent url(image/wechat/bg_mobile_foot.png)">');
        html.push('<ul myName="tools" style="list-style-type:none;padding:0px;margin:0px;margin-left: 44px;width: 256px;height: 50px">');
        html.push('</ul>');
        html.push('</div>');

        html.push('</div>');
        html.push('</div>');
        mobileView.setHtml(html.join(''));

        // funAddToolItem({menu: true, selected: true, showMenu: true});
        // funAddToolItem();

        funAddButton();
    },

    onAfterApply: function () {
        this.initMobileView();
        this.initPanelEvent();
    }

});