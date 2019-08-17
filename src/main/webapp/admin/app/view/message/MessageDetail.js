Ext.define('App.message.MessageDetail', {
    extend: 'Ext.container.Container',
    alias: 'widget.messagedetail',

    border: false,
    layout: 'fit',
    autoScroll: true,
    apis: {
        SystemMessage: {
            getReadMessage: {}
        }
    },

    setWindowView: function (win) {
        this._windowView = win['window'];
        this._toolbarMenuView = win['toolbarMenuView'];
    },

    setValue: function (id) {
        if (id) {
            var self = this;
            this.apis.SystemMessage.getReadMessage
                .wait(self, '正在加载消息信息...')
                .call({id: id}, function (data) {
                    var title = data['title'];
                    var admin = data['TableAdmin'];
                    var time = data['createTime'];
                    var content = data['content'];

                    var html = [];
                    html.push('<div style="width: 800px;height: auto;overflow: auto;padding: 50px;margin-left: auto;margin-right: auto;line-height: normal">');

                    html.push('<div style="width: 100%;text-align: center;font-size: 28px;font-weight:bold;margin: auto auto 30px auto;">');
                    html.push(title);
                    html.push('</div>');

                    html.push('<div style="width: 100%;overflow:hidden;color:#b3b3b3;font-size: 14px;font-weight: bold">');
                    html.push('<div style="float: left;width: 50%;text-align: center">');
                    html.push('发布人：' + (admin['realName'] || admin['userName']));
                    html.push('</div>');
                    html.push('<div style="float: right;width: 50%;text-align: center">');
                    html.push('时间：' + (new Date(time)).format());
                    html.push('</div>');
                    html.push('</div>');

                    html.push('<div style="width: 100%;overflow: hidden;font-size: 14px;color: #000000;margin-top: 30px">');
                    html.push(content);
                    html.push('</div>');

                    html.push('</div>');
                    self.setHtml(html.join(''));
                    if (self._windowView) {
                        self._windowView.setTitle('查看消息 - ' + title);
                    }
                    if (self._toolbarMenuView) {
                        self._toolbarMenuView._refreshViewFun();
                    }
                });

        }
    }
});