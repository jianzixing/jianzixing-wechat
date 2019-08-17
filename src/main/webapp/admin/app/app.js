Ext.onReady(function () {
    var userName = document.getElementById('login_user_name').value;
    var realName = document.getElementById('login_user_real_name').value;
    var logo = document.getElementById('login_user_logo').value;

    ApplicationLoader.setResourceIconUrl('/admin/image/{0}icon/');
    ApplicationLoader.setResourceFileUrl('/web/image/load.jhtml?f={0}');
    ApplicationLoader.setResourceNoneImageUrl('/admin/icon/nopic_{0}.gif');
    ApplicationLoader.setApiUrl('/admin/{0}/{1}.action');
    ApplicationLoader.setToolBarHeadLogo('image/head_logo.jpg');
    ApplicationLoader.setSystemLogo('image/icon/logo.png');

    ApplicationLoader.setExtLoaderConfig({enabled: true, disableCaching: true});
    ApplicationLoader.setExtLoaderPath([
        {name: 'App', path: 'app/view'},
        {name: 'UXApp', path: 'app/ux'},
        {name: 'AppRoot', path: 'app'}
    ]);

    ApplicationLoader.setGlobalParams({
        user: {
            userName: userName,
            realName: realName,
            logo: logo
        }
    });

    ApplicationLoader.setListener('loadTreeModule', function (me, apis, module, fn) {
        Ext.Ajax.request({
            url: '/admin/module/gettreemodules.action',
            params: {module: module},
            method: 'GET',
            callback: {me: me, apis: apis, module: module, fn: fn},
            success: function (response, opts) {
                if (response.responseText && response.responseText != '') {
                    var obj = Ext.decode(response.responseText);
                    opts.callback.me.modulesStore = Ext.create('Ext.data.TreeStore', {
                        root: {
                            expanded: true,
                            children: obj['data']
                        }
                    });
                    opts.callback.fn(me.modulesStore);
                }
            }
        });
    });

    ApplicationLoader.setListener("loadModules", function () {
        console.log('模块已经加载!');
    });

    ApplicationLoader.setListener("headLogoClick", function () {
        Dialog.openWindow('App.system.LoginAdminWindow', {});
    });
    ApplicationLoader.setListener("editPasswordClick", function (me) {
        Dialog.openWindow('App.EditPasswordWindow', {
            callback: function () {
                me.existCurrentSession();
            }
        });
    });
    ApplicationLoader.setListener("settingClick", function () {
        Dialog.openWindow('App.system.LoginAdminWindow', {});
    });

    var systemMessage = {
        id: 67,
        module: 'App.message.SystemMessage',
        text: '系统消息管理',
        type: 'normal',
        tabIcon: 'image/micon/icon_76.png'
    };
    ApplicationLoader.setListener("messageClick", function () {
        ApplicationLoader.addTabComponent(systemMessage, true);
    });
    ApplicationLoader.setListener("messageItemClick", function (view, json) {
        var id = json['id'];
        var win = Dialog.openWindow('App.message.ShowMessageWindow', {
            toolbarMenuView: view.toolbarMenuView
        });
        win.setValue(id);
    });
    ApplicationLoader.setListener("messageMoreClick", function () {
        ApplicationLoader.addTabComponent(systemMessage, true);
    });

    ApplicationLoader.setLoadModulesUrl('/admin/module/getmodules.action');
    ApplicationLoader.init();
    ApplicationLoader.loader();
});
