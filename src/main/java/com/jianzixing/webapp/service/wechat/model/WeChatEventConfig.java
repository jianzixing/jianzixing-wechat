package com.jianzixing.webapp.service.wechat.model;

public class WeChatEventConfig {
    // 开发者 微信号
    private String toUserName;
    // 发送方帐号（一个OpenID）
    private String fromUserName;
    // 消息创建时间 （整型）
    private long createTime;
    // 消息类型，event
    private String msgType;
    // 	事件类型，CLICK / VIEW / scancode_push / scancode_waitmsg / pic_sysphoto / pic_photo_or_album / pic_weixin / location_select
    private String event;
    //	事件KEY值，与自定义菜单接口中KEY值对应
    private String eventKey;


    //指菜单ID，如果是个性化菜单，则可以通过这个字段，知道是哪个规则的菜单被点击了。
    private String menuID;


    //	扫描信息
    private String scanCodeInfo;
    // 扫描类型，一般是qrcode
    private String scanType;
    // 扫描结果，即二维码对应的字符串信息
    private String scanResult;

    // 发送的图片信息
    private SendPicsInfo sendPicsInfo;

    // 	发送的位置信息
    private SendLocationInfo sendLocationInfo;
    private String scale;
    private String label;
    private String poiname;


    public static class SendPicsInfo {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class SendLocationInfo {
        private String location_X;
        private String location_Y;

        public String getLocation_X() {
            return location_X;
        }

        public void setLocation_X(String location_X) {
            this.location_X = location_X;
        }

        public String getLocation_Y() {
            return location_Y;
        }

        public void setLocation_Y(String location_Y) {
            this.location_Y = location_Y;
        }
    }
}
