Ext.define('App.system.AdminWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'UXApp.window.SelectorTextField',
        'Ext.form.Panel',
        'Ext.form.FieldSet',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 640,
    width: 660,
    layout: 'fit',
    title: '添加员工',
    defaultListenerScope: true,
    items: [
        {
            xtype: 'form',
            border: false,
            layout: 'border',
            header: false,
            items: [
                {
                    xtype: 'form',
                    name: 'other_info',
                    flex: 1,
                    region: 'center',
                    bodyStyle: 'padding:10px 10px 0px 5px',
                    layout: 'fit',
                    header: false,
                    border: false,
                    items: [
                        {
                            xtype: 'fieldset',
                            height: 238,
                            width: 239,
                            title: '其他信息',
                            items: [
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
                                    value: ''
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
                    ]
                },
                {
                    xtype: 'form',
                    name: 'base_info',
                    bodyStyle: 'padding:10px 5px 0px 10px',
                    flex: 1,
                    region: 'west',
                    width: 150,
                    border: false,
                    layout: 'fit',
                    header: false,
                    items: [
                        {
                            xtype: 'fieldset',
                            width: 265,
                            title: '基本信息',
                            items: [
                                {
                                    xtype: 'textfield',
                                    name: 'userName',
                                    anchor: '100%',
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
                                    inputType: 'password',
                                    anchor: '100%',
                                    fieldLabel: '密码*',
                                    allowBlank: false,
                                    maxLength: 128,
                                    maxLengthText: '密码最大响度为 {0} 个字符',
                                    minLength: 6,
                                    minLengthText: '密码最小长度为 {0} 个字符',
                                    blankText: '密码不能为空'
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'repassword',
                                    inputType: 'password',
                                    anchor: '100%',
                                    fieldLabel: '重复密码*',
                                    allowBlank: false,
                                    maxLength: 128,
                                    maxLengthText: '密码最大响度为 {0} 个字符',
                                    minLength: 6,
                                    minLengthText: '密码最小长度为 {0} 个字符',
                                    blankText: '密码不能为空'
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'realName',
                                    anchor: '100%',
                                    fieldLabel: '姓名',
                                    maxLength: 128,
                                    blankText: '姓名不能为空'
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'phoneNumber',
                                    anchor: '100%',
                                    fieldLabel: '移动电话'
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'jobNumber',
                                    anchor: '100%',
                                    fieldLabel: '工号'
                                },
                                {
                                    xtype: 'combobox',
                                    name: 'departmentId',
                                    fieldLabel: '部门',
                                    anchor: '100%',
                                    queryMode: 'remote',
                                    valueField: 'id',
                                    editable: false,
                                    displayField: 'name'
                                },
                                {
                                    xtype: 'combobox',
                                    name: 'positionId',
                                    anchor: '100%',
                                    fieldLabel: '职位',
                                    queryMode: 'remote',
                                    valueField: 'id',
                                    displayField: 'name',
                                    editable: false
                                },
                                {
                                    xtype: 'radiogroup',
                                    anchor: '100%',
                                    fieldLabel: '员工状态',
                                    items: [
                                        {
                                            xtype: 'radiofield',
                                            name: 'status',
                                            inputValue: 1,
                                            checked: true,
                                            boxLabel: '正常'
                                        },
                                        {
                                            xtype: 'radiofield',
                                            name: 'status',
                                            inputValue: 2,
                                            boxLabel: '离职'
                                        },
                                        {
                                            xtype: 'radiofield',
                                            name: 'status',
                                            inputValue: 0,
                                            boxLabel: '试用'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'datefield',
                                    name: 'checkInTime',
                                    anchor: '100%',
                                    fieldLabel: '入职时间',
                                    format: 'Y-m-d'
                                },
                                {
                                    xtype: 'combobox',
                                    name: 'roleId',
                                    anchor: '100%',
                                    fieldLabel: '角色',
                                    queryMode: 'remote',
                                    valueField: 'id',
                                    displayField: 'roleName',
                                    editable: false
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            border: false,
            items: [
                '->',
                {
                    xtype: 'button',
                    text: '保存用户',
                    icon: Resource.png('jet', 'menu-saveall'),
                    listeners: {
                        click: 'onSave'
                    }
                },
                {
                    xtype: 'button',
                    text: '重新填写',
                    icon: Resource.png('jet', 'redo'),
                    listeners: {
                        click: 'onClear'
                    }
                },
                '->'
            ]
        }
    ],

    onSave: function (button, e, eOpts) {
        var baseForm = this.find('base_info').getForm();
        var otherForm = this.find('other_info').getForm();

        if (baseForm.isValid() && otherForm.isValid()) {
            var jsonBase = this.find('base_info').getValues(),
                jsonOther = this.find('other_info').getValues(),
                json = {},
                me = this;
            if (jsonBase) for (var i in jsonBase) json[i] = jsonBase[i];
            if (jsonOther) for (var i in jsonOther) json[i] = jsonOther[i];


            if (json) {
                if (json.password != json['repassword']) {
                    Dialog.alert('两次输入的密码不一致');
                    return false;
                }
                if (this.adminId) {
                    json.id = this.adminId;
                    me.apis.Admin.updateAdmin
                        .wait(me, '正在修改管理员...')
                        .call({object: json}, function (value) {
                            me.close();
                            if (me.callback) {
                                me.callback();
                            }
                        });
                } else {
                    me.apis.Admin.addAdmin
                        .wait(me, '正在添加管理员...')
                        .call({object: json}, function (value) {
                            me.close();
                            if (me.callback) {
                                me.callback();
                            }
                        });
                }
            }
        }
    },

    onClear: function (button, e, eOpts) {
        this.find('base_info').getForm().reset();
        this.find('other_info').getForm().reset();
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

    setValues: function (json) {
        this.adminId = json.id;
        if (json['checkInTime']) {
            json['checkInTime'] = new Date(json['checkInTime']);
        }
        if (json['birthday']) {
            json['birthday'] = new Date(json['birthday']);
        }
        this.setTitle('修改员工');
        this.find('base_info').getForm().setValues(json);
        this.find('other_info').getForm().setValues(json);
        this.find('userName').setDisabled(true);
        this.find('password').destroy();
        this.find('repassword').destroy();
        if (json['logo']) {
            this.find('image').setSrc(Resource.image(json['logo']));
        }
    },

    initWindow: function () {

        var departmentView = this.find('departmentId');
        var store = this.apis.Admin.getDepartment.createListStore();
        departmentView.setStore(store);
        store.load();

        var positionView = this.find('positionId');
        var store = this.apis.Admin.getPosition.createListStore();
        positionView.setStore(store);
        store.load();

        var roleView = this.find('roleId');
        var store = this.apis.System.getRoles.createListStore();
        roleView.setStore(store);
        store.load();
    }
});
