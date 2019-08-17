package com.jianzixing.webapp.service.aftersales;

/**
 * 退货：
 * INIT -> START -> START_SUCCESS | START_REFUSED -> SEND | SEND_REFUSED
 * -> GET | GET_REFUSED | GET_SUCCESS -> REFUND -> REFUND_FAILURE | FINISH
 * <p>
 * 换货：
 * INIT -> START -> START_SUCCESS | START_REFUSED -> SEND | SEND_REFUSED
 * -> GET | GET_REFUSED | GET_SUCCESS -> SEND_BACK -> SEND_BACK_REFUSED | FINISH
 * <p>
 * 维修：
 * INIT -> START -> START_SUCCESS | START_REFUSED -> SEND | SEND_REFUSED
 * -> GET | GET_REFUSED | GET_SUCCESS -> START_REPAIR | REPAIR_FAILURE | REPAIR_SUCCESS
 * -> SEND_BACK -> SEND_BACK_REFUSED | FINISH
 */
public enum AfterSalesStatus {
    INIT(0, "订单新建"),

    START(10, "订单发起售后"),
    // 客服审核，这个时候用户可以寄送商品
    START_SUCCESS(20, "发起售后申请成功"),
    // 客服审核失败
    START_REFUSED(21, "发起售后申请拒绝"),

    // 必须填写快递单号
    SEND(30, "用户已经寄送"),
    // 如果自己上门取货或者自建物流，快递员可拒收，拒收后客服可以看见重新审核到START_SUCCESS
    SEND_REFUSED(31, "用户已经寄送但物流拒绝"),

    GET(40, "卖家已经收货"),
    // 卖家检验不通过拒收,客服介入
    GET_REFUSED(41, "验货有问题"),
    // 卖家通过检查或者开始维修，如果是换货则是验货通过
    GET_SUCCESS(42, "验货通过"),

    // 确认开始维修
    START_REPAIR(50, "正在维修中"),
    // 维修失败，客服介入退回或者放弃
    REPAIR_FAILURE(51, "维修失败"),
    // 维修成功后可以交给快递送回
    REPAIR_SUCCESS(52, "维修成功"),

    // 寄回时填写快递和配送单号
    SEND_BACK(60, "正在寄回给用户"),
    // 寄回快递退回或者拒收，这时客服介入
    SEND_BACK_REFUSED(61, "开始寄回失败或者拒收"),

    REFUND(80, "正在退款"),
    REFUND_FAILURE(81, "退款失败"),
    CANCEL(90, "取消售后申请单"),
    // 售后完成，
    // 如果是退货则表示已经生成退款单，
    // 如果是换货则表示买家已收货，
    // 如果是维修则表示买家已收货并认同维修成功
    FINISH(100, "售后完成");

    private int code;
    private String msg;

    AfterSalesStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
