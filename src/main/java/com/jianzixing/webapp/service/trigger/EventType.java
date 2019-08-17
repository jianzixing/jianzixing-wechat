package com.jianzixing.webapp.service.trigger;

/**
 * 这里定义事件类型
 */
public enum EventType {

    PLACE_AN_ORDER(
            10,
            "用户下单后通知",
            new ProcessorField[]{
                    new ProcessorField(double.class, "userId", "用户ID", false),
                    new ProcessorField(double.class, "payPrice", "订单金额", false)
            }
    ),
    REGISTER_SEND_SMS(
            20,
            "用户注册发送验证码",
            new ProcessorField[]{
                    new ProcessorField(Object.class, "phones", "手机号码", false),
                    new ProcessorField(long.class, "code", "六位验证码", false)
            }
    ),
    FORGET_PWD_SEND_SMS(
            30,
            "忘记密码发送验证码",
            new ProcessorField[]{
                    new ProcessorField(double.class, "userId", "用户ID", false),
                    new ProcessorField(Object.class, "phones", "手机号码", false),
                    new ProcessorField(long.class, "code", "六位验证码", false)
            }
    ),
    ORDER_PAYED_SUCCESS(
            40,
            "用户下单支付成功",
            new ProcessorField[]{
                    new ProcessorField(double.class, "userId", "用户ID", false),
                    new ProcessorField(double.class, "payPrice", "订单金额", false)
            }
    ),
    ORDER_USER_CONFIRM(
            50,
            "用户确认收货",
            new ProcessorField[]{
                    new ProcessorField(double.class, "userId", "用户ID", false),
                    new ProcessorField(double.class, "number", "订单号", false),
                    new ProcessorField(double.class, "payPrice", "订单金额", false)
            }
    ),


    WECHAT_MSG_EVENT_SUBSCRIBE(
            200,
            "关注公众号",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false)
            }
    ),
    WECHAT_MSG_EVENT_SUBSCRIBE_SCAN(
            210,
            "未关注扫码二维码",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false)
            }
    ),
    WECHAT_MSG_EVENT_SCAN(
            290,
            "已关注扫码二维码",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false)
            }
    ),
    WECHAT_MSG_EVENT_UNSUBSCRIBE(
            220,
            "取消关注",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false)
            }
    ),
    WECHAT_MSG_EVENT_CLICK(
            230,
            "点击自定义菜单事件",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false)
            }
    ),
    WECHAT_MSG_EVENT_VIEW(
            240,
            "点击菜单跳转事件",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false)
            }
    ),
    WECHAT_MSG_TEXT(
            250,
            "接收到文本消息事件",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false),
                    new ProcessorField(String.class, "text", "文本消息内容", true)
            }
    ),
    WECHAT_MSG_IMAGE(
            260,
            "接收到图片消息事件",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false)
            }
    ),
    WECHAT_MSG_VOICE(
            270,
            "接收到声音消息事件",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false)
            }
    ),
    WECHAT_MSG_VIDEO(
            280,
            "接收到视频消息事件",
            new ProcessorField[]{
                    new ProcessorField(long.class, "userId", "用户ID", false)
            }
    );

    private int code;
    private String msg;
    /**
     * 事件发生时传入的参数
     */
    private ProcessorField[] params;

    /**
     * 事件处理后可接受的返回值
     */
    private ProcessorField[] returns;

    EventType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    EventType(int code, String msg, ProcessorField[] params) {
        this.code = code;
        this.msg = msg;
        this.params = params;
    }

    EventType(int code, String msg, ProcessorField[] params, ProcessorField[] returns) {
        this.code = code;
        this.msg = msg;
        this.params = params;
        this.returns = returns;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ProcessorField[] getParams() {
        return params;
    }

    public ProcessorField[] getReturns() {
        return returns;
    }
}
