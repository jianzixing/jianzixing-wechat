Ext.define('App.system.LoginAdminWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio'
    ],

    height: 650,
    width: 800,
    layout: 'fit',
    title: '修改个人信息',
    defaultListenerScope: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            items: [
                '->',
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'menu-saveall'),
                    text: '保存修改',
                    listeners: {
                        click: 'onSaveClick'
                    }
                },
                {
                    xtype: 'button',
                    icon: Resource.png('jet', 'cancel'),
                    text: '取消关闭',
                    listeners: {
                        click: 'onCancelClick'
                    }
                },
                '->'
            ]
        }
    ],
    items: [
        {
            xtype: 'form',
            name: 'form',
            border: false,
            bodyPadding: 10,
            header: false,
            items: [
                {
                    xtype: 'textfield',
                    name: 'userName',
                    anchor: '100%',
                    disabled: true,
                    fieldLabel: '用户名*'
                },
                {
                    xtype: 'textfield',
                    name: 'realName',
                    anchor: '100%',
                    fieldLabel: '真实姓名'
                },
                {
                    xtype: 'textfield',
                    name: 'phoneNumber',
                    anchor: '100%',
                    fieldLabel: '移动电话'
                },
                {
                    xtype: 'fieldcontainer',
                    anchor: '100%',
                    layout: 'table',
                    fieldLabel: '头像',
                    items: [
                        {
                            xtype: 'image',
                            name: 'image',
                            style: {
                                maxWidth: '35px',
                                maxHeight: '35px'
                            },
                            src: 'image/head_logo.jpg',
                        },
                        {
                            xtype: 'button',
                            margin: 'auto auto auto 30px',
                            text: '选择图片文件',
                            listeners: {
                                click: 'onSelectImageClick'
                            }
                        },
                        {
                            xtype: 'hiddenfield',
                            name: 'logo'
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    name: 'extension',
                    anchor: '100%',
                    fieldLabel: '分机号',
                    value: '',
                    regex: /^[0-9]+$/,
                    regexText: '请输入整数'
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '性别',
                    items: [
                        {
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
                        },
                        {
                            xtype: 'radiofield',
                            name: 'gender',
                            inputValue: 0,
                            boxLabel: '保密'
                        }
                    ]
                },
                {
                    xtype: 'datefield',
                    name: 'birthday',
                    anchor: '100%',
                    fieldLabel: '出生日期',
                    format: 'Y-m-d'
                },
                {
                    xtype: 'textfield',
                    name: 'homeNumber',
                    anchor: '100%',
                    fieldLabel: '办公电话'
                },
                {
                    xtype: 'textfield',
                    name: 'email',
                    anchor: '100%',
                    fieldLabel: '电子邮箱'
                },
                {
                    xtype: 'textfield',
                    name: 'education',
                    anchor: '100%',
                    fieldLabel: '教育程度'
                },
                {
                    xtype: 'textareafield',
                    name: 'description',
                    anchor: '100%',
                    height: 90,
                    fieldLabel: '简介'
                }
            ]
        }
    ],

    apis: {
        Admin: {
            updateLoginAdmin: {},
            getLoginAdmin: {}
        }
    },

    onSaveClick: function (button, e, eOpts) {
        var self = this;
        var form = this.find('form').getForm();
        var data = form.getValues();
        Dialog.confirm('提示', '确定修改个人信息吗？', function (btn) {
            if (btn == 'yes') {
                self.apis.Admin.updateLoginAdmin
                    .wait(self, '正在修改个人信息...')
                    .call({object: data}, function () {
                        self.close();
                        Dialog.alert('修改个人信息成功!下次登录后可显示最新信息');
                    })
            }
        });
    },

    onCancelClick: function (button, e, eOpts) {
        this.close();
    },

    onSelectImageClick: function (button, e, eOpts) {
        var self = this;
        var field = this.find('logo');
        var image = this.find('image');
        var win = Dialog.openWindow('App.file.SelectFileWindow', {selectOne: true});
        win.setSelectionCallback(function (files) {
            image.setSrc(Resource.image(files.fileName));
            field.setValue(files.fileName);
        }, true);
    },

    onShow: function () {
        this.callParent(arguments);
        var self = this;
        this.apis.Admin.getLoginAdmin
            .wait(self, '正在加载用户信息...')
            .call({}, function (data) {
                if (data['birthday']) {
                    data['birthday'] = new Date(data['birthday']);
                }
                self.find('form').getForm().setValues(data);
                if (data['logo']) {
                    self.find('image').setSrc(Resource.image(data['logo']));
                }
            })
    }
});