Ext.define('App.user.OperationMember', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.form.Panel',
        'Ext.form.Label',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.Date',
        'Ext.form.field.ComboBox',
        'Ext.form.field.TextArea',
        'Ext.button.Button',
        'Ext.toolbar.Toolbar'
    ],

    border: false,
    layout: 'border',
    header: false,
    defaultListenerScope: true,

    items: [
        {
            xtype: 'form',
            name: 'member_form',
            region: 'center',
            autoScroll: true,
            border: false,
            layout: 'auto',
            bodyPadding: 10,
            frameHeader: false,
            header: false,
            items: [
                {
                    xtype: 'label',
                    html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/type.png"> </div> <div class="text">基本信息</div> </div>',
                    text: ''
                },
                {
                    xtype: 'textfield',
                    name: 'userName',
                    margin: 10,
                    width: 600,
                    fieldLabel: '用户名*',
                    allowBlank: false,
                    maxLength: 32,
                    maxLengthText: '用户名最大长度为 {0} 个字符',
                    minLength: 6,
                    minLengthText: '用户名最小长度为 {0} 个字符',
                    regex: /^[a-zA-Z]\w{5,31}$/,
                    regexText: '用户必须以字母开头并且6-32位数字加字母',
                    blankText: '用户名不能为空'
                },
                {
                    xtype: 'textfield',
                    name: 'password',
                    margin: 10,
                    width: 600,
                    allowBlank: false,
                    fieldLabel: '密码*',
                    inputType: 'password',
                    maxLength: 128,
                    maxLengthText: '密码最大响度为 {0} 个字符',
                    minLength: 6,
                    minLengthText: '密码最小长度为 {0} 个字符',
                    regex: /^[a-zA-Z]\w{5,31}$/,
                    regexText: '密码必须以字母开头并且6-128位的数字加字母',
                    blankText: '密码不能为空'
                },
                {
                    xtype: 'textfield',
                    name: 'repassword',
                    margin: 10,
                    width: 600,
                    allowBlank: false,
                    fieldLabel: '确认密码*',
                    inputType: 'password',
                    maxLength: 128,
                    maxLengthText: '密码最大响度为 {0} 个字符',
                    minLength: 6,
                    minLengthText: '密码最小长度为 {0} 个字符',
                    blankText: '密码不能为空'
                },
                {
                    xtype: 'textfield',
                    name: 'nick',
                    margin: 10,
                    width: 600,
                    fieldLabel: '昵称'
                },
                {
                    xtype: 'radiogroup',
                    margin: 10,
                    width: 600,
                    fieldLabel: '性别',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'gender',
                            inputValue: 0,
                            checked: true,
                            boxLabel: '保密'
                        }, {
                            xtype: 'radiofield',
                            name: 'gender',
                            inputValue: 1,
                            boxLabel: '男'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'gender',
                            inputValue: 2,
                            boxLabel: '女'
                        }
                    ]
                },
                {
                    xtype: 'datefield',
                    name: 'birthday',
                    margin: 10,
                    width: 600,
                    fieldLabel: '出生日期',
                    format: 'Y-m-d'
                },
                {
                    xtype: 'radiogroup',
                    margin: 10,
                    width: 600,
                    fieldLabel: '是否激活',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            checked: true,
                            inputValue: '1',
                            boxLabel: '激活'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'enable',
                            inputValue: '0',
                            boxLabel: '冻结'
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    margin: 10,
                    width: 600,
                    fieldLabel: '标签',
                    allowBlank: true,
                    hidden: true,
                    name: 'tags'
                },
                {
                    xtype: 'label',
                    html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/type.png"> </div> <div class="text">联系信息</div> </div>'
                },
                {
                    xtype: 'textfield',
                    name: 'email',
                    margin: 10,
                    width: 600,
                    fieldLabel: 'E-mail',
                    vtype: 'email'
                },
                {
                    xtype: 'textfield',
                    name: 'phone',
                    margin: 10,
                    width: 600,
                    fieldLabel: '移动电话'
                },
                {
                    xtype: 'label',
                    html: '<div class="basetitle"> <div class="img"> <img border="0" src="/admin/image/icon/type.png"> </div> <div class="text">其他信息</div> </div>'
                },
                {
                    xtype: 'radiogroup',
                    margin: 10,
                    width: 600,
                    fieldLabel: '是否结婚',
                    items: [
                        {
                            xtype: 'radiofield',
                            name: 'isMarried',
                            inputValue: '0',
                            boxLabel: '已结婚'
                        },
                        {
                            xtype: 'radiofield',
                            name: 'isMarried',
                            inputValue: '1',
                            boxLabel: '未结婚'
                        }
                    ]
                },
                {
                    xtype: 'container',
                    margin: '30 10 10 10',
                    items: [
                        {
                            xtype: 'button',
                            text: '保存',
                            listeners: {
                                click: 'onAddUserClick'
                            }
                        },
                        {
                            xtype: 'button',
                            margin: '0 0 0 20',
                            text: '返回',
                            listeners: {
                                click: 'onBackClick'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'panel',
            region: 'west',
            split: true,
            border: false,
            width: 183,
            header: false,
            items: [
                {
                    xtype: 'container',
                    margin: '10 5 10 5',
                    style: {
                        borderBottom: '1px solid #889899'
                    },
                    layout: 'anchor',
                    items: [
                        {
                            xtype: 'image',
                            name: 'member_head',
                            margin: '12',
                            style: {
                                maxWidth: '150px',
                                maxHeight: '150px'
                            },
                            src: Resource.create('/admin/image/exicon/nopic_150.gif')
                        },
                        {
                            xtype: 'button',
                            margin: '10 auto 10 50',
                            text: '选择头像',
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        }
                    ]
                },
                {
                    xtype: 'container',
                    margin: '10 0 0 5',
                    items: [
                        {
                            xtype: 'label',
                            text: '注：添加会员信息，其中*为必填信息'
                        }
                    ]
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    text: '返回列表',
                    icon: Resource.png('jet', 'back'),
                    listeners: {
                        click: 'onListMemberClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'reset_btn',
                    text: '重新填写',
                    icon: Resource.png('jet', 'resetStrip_dark'),
                    listeners: {
                        click: 'onAddMemberClick'
                    }
                },
                {
                    xtype: 'button',
                    name: 'edit_btn',
                    text: '修改密码',
                    hidden: true,
                    icon: Resource.png('', 'edit_pwd'),
                    listeners: {
                        click: 'onEditPwdClick'
                    }
                }
            ]
        }
    ],

    onAddUserClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('member_form').getForm();
        var view = this.find('member_head');
        var values = form.getValues();
        if (form.isValid()) {
            var logo = view.file;
            if (logo) {
                values['avatar'] = logo['fileName'];
            }
            if (values['password'] != values['repassword']) {
                Dialog.alert('提示', '两次密码输入不一致');
                return false;
            }

            var userId = this.userId;
            Dialog.confirm('提示', '确定保存当前用户信息？', function (btn) {
                if (btn == 'yes') {
                    if (userId) {
                        values['id'] = userId;
                        self.apis.User.updateUser
                            .wait(self, '正在保存用户...')
                            .call({object: values}, function () {
                                self.parent.back();
                                if (self._callback) {
                                    self._callback();
                                }
                            })
                    } else {
                        self.apis.User.addUser
                            .wait(self, '正在保存用户...')
                            .call({object: values}, function () {
                                Dialog.confirm('提示', '添加会员成功，是否继续添加', function (btn) {
                                    if (btn == Global.YES) {
                                        self.parent.redraw();
                                    } else {
                                        self.parent.back();
                                        if (self._callback) {
                                            self._callback();
                                        }
                                    }
                                })
                            }, function () {
                                Dialog.alert('提示', '添加会员失败', Dialog.ERROR);
                            });
                    }
                }
            });
        }
    },

    onBackClick: function (button, e, eOpts) {
        this.parent.back();
    },

    onSelectImageClick: function (button, e, eOpts) {
        var me = this;
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            var view = me.find('member_head');
            view.setSrc(Resource.image(files.fileName));
            view.file = files;
        }, true);
    },

    onListMemberClick: function (button, e, eOpts) {
        this.parent.back();
    },

    onAddMemberClick: function (button, e, eOpts) {
        this.parent.redraw();
    },

    onEditPwdClick: function () {
        var self = this;
        Dialog.openFormWindow({
            title: '修改用户密码',
            width: 500,
            height: 300,
            items: [
                {
                    xtype: 'textfield',
                    name: 'password',
                    anchor: '100%',
                    fieldLabel: '请输入管理员密码',
                    inputType: 'password',
                    allowBlank: false,
                    emptyText: '需要当前登录的管理员密码'
                },
                {
                    xtype: 'textfield',
                    name: 'password2',
                    anchor: '100%',
                    fieldLabel: '用户重置密码',
                    inputType: 'password',
                    allowBlank: false,
                    emptyText: '您要重置的用户新密码'
                }
            ],
            success: function (json, win) {
                var form = win.find('form').getForm();

                if (form.isValid()) {
                    self.apis.User.resetUserPwd
                        .wait(self, '正在修改用户密码...')
                        .call({
                            uid: self.userId,
                            adminPwd: json['password'],
                            userPwd: json['password2']
                        }, function () {
                            win.close();
                            Dialog.alert('用户密码修改成功!');
                        });
                }
            }
        });
    },

    onAfterApply: function () {

    },

    setValues: function (user) {
        if (user['birthday']) {
            user['birthday'] = new Date(user['birthday']);
        }
        if (user['nick']) {
            user['nick'] = decodeURIComponent(user['nick']);
        }
        this.find('member_form').getForm().setValues(user);
        this.userId = user.id;
        this.find('password').destroy();
        this.find('repassword').destroy();
        this.find('userName').setEditable(false);
        if (user.avatar) {
            this.find('member_head').setSrc(Resource.image(user.avatar));
        }
        this.find('reset_btn').hide();
        this.find('edit_btn').show();
    }
});
