package com.jianzixing.webapp.service.payment.channel;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.payment.*;
import com.jianzixing.webapp.service.wechat.WeChatInterfaceUtils;
import com.jianzixing.webapp.service.wechat.aes.WXBizMsgCrypt;
import com.jianzixing.webapp.tables.file.TableFiles;
import com.jianzixing.webapp.tables.payment.TablePaymentChannel;
import com.jianzixing.webapp.tables.payment.TablePaymentResponse;
import com.jianzixing.webapp.tables.payment.TablePaymentTransaction;
import com.jianzixing.webapp.tables.refund.TableRefundOrder;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.utils.StringTools;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mimosaframework.core.encryption.Base64;
import org.mimosaframework.core.encryption.MD5Utils;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WechatPaymentChannel implements PaymentModeInterface {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final Log logger = LogFactory.getLog(WechatPaymentChannel.class);
    private static final String name = "wechat";


    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public String getPaymentName() {
        return "微信支付";
    }

    @Override
    public String getPaymentShortName() {
        return "微信账户";
    }

    @Override
    public String getPaymentCode() {
        return "wechat";
    }

    @Override
    public ParamField[] getNeedParams() {
        ParamField[] models = new ParamField[6];
        {
            ParamField m = new ParamField();
            m.setKey("appid");
            m.setMust(true);
            m.setName("公众账号ID");
            m.setDetail("微信支付分配的公众账号ID（企业号corpid即为此appId）");
            m.setLength(32);
            models[0] = m;
        }
        {
            ParamField m = new ParamField();
            m.setKey("mch_id");
            m.setMust(true);
            m.setName("商户号");
            m.setDetail("微信支付分配的商户号");
            m.setLength(32);
            models[1] = m;
        }
        {
            ParamField m = new ParamField();
            m.setKey("secret_key");
            m.setMust(true);
            m.setName("秘钥");
            m.setDetail("微信商户后台设置的秘钥");
            m.setLength(500);
            models[2] = m;
        }
        {
            ParamField m = new ParamField();
            m.setKey("trade_type");
            m.setValue("JSAPI");
            m.setMust(true);
            m.setName("交易类型");
            m.setLength(16);
            m.setDetail("JSAPI -JSAPI支付,NATIVE -Native支付,APP -APP支付");
            models[3] = m;
        }
        {
            ParamField m = new ParamField();
            m.setKey("attach");
            m.setValue("简子行科技");
            m.setMust(true);
            m.setName("店名");
            m.setLength(127);
            m.setDetail("附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。");
            models[4] = m;
        }
        {
            ParamField m = new ParamField();
            m.setKey("body");
            m.setValue("会员消费");
            m.setMust(true);
            m.setName("支付描述");
            m.setLength(127);
            m.setDetail("商品简单描述，该字段请按照规范传递");
            models[5] = m;
        }
        return models;
    }

    @Override
    public boolean isWantCertificate() {
        return true;
    }

    @Override
    public Class<? extends PaymentModeInterface> getPackageImpl() {
        return this.getClass();
    }

    /**
     * 接口文档地址：
     * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
     *
     * <xml>
     * <appid>wx2421b1c4370ec43b</appid>
     * <attach>支付测试</attach>
     * <body>JSAPI支付测试</body>
     * <mch_id>10000100</mch_id>
     * <detail><![CDATA[{ "goods_detail":[ { "goods_id":"iphone6s_16G", "wxpay_goods_id":"1001", "goods_name":"iPhone6s 16G", "quantity":1, "price":528800, "goods_category":"123456", "body":"苹果手机" }, { "goods_id":"iphone6s_32G", "wxpay_goods_id":"1002", "goods_name":"iPhone6s 32G", "quantity":1, "price":608800, "goods_category":"123789", "body":"苹果手机" } ] }]]></detail>
     * <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
     * <notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>
     * <openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid>
     * <out_trade_no>1415659990</out_trade_no>
     * <spbill_create_ip>14.23.150.211</spbill_create_ip>
     * <total_fee>1</total_fee>
     * <trade_type>JSAPI</trade_type>
     * <sign>0CB01533B8C1EF103065174F50BCA001</sign>
     * </xml>
     *
     * @param order
     * @param cnfParams  数据库配置的支付参数
     * @param pageParams 页面传入的支付参数
     * @return
     * @throws ModuleException
     */
    @Override
    public PaymentResult apply(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws Exception {

        BigDecimal payPrice = order.getOrderPrice();
        long uid = order.getUid();
        ModelObject user = GlobalService.userService.getUser(uid);
        if (user != null) {
            // trade_type=JSAPI时（即JSAPI支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识。
            String openid = user.getString(TableUser.openid);
            if (StringUtils.isBlank(openid)) {
                throw new ModuleException("wechat_pay_args_openid", "缺少用户openid");
            }
            String secretKey = cnfParams.getString("secret_key");
            if (StringUtils.isBlank(secretKey)) {
                throw new ModuleException("wechat_pay_args_secret_key", "缺少支付接口信息secret_key");
            }
            String appid = cnfParams.getString("appid");
            if (StringUtils.isBlank(appid)) {
                throw new ModuleException("wechat_pay_args_appid", "缺少支付接口信息appid");
            }
            String mchId = cnfParams.getString("mch_id");
            if (StringUtils.isBlank(mchId)) {
                throw new ModuleException("wechat_pay_args_mchId", "缺少支付接口信息mchId");
            }
            String tradeType = cnfParams.getString("trade_type");
            String attach = cnfParams.getString("attach");
            String body = attach + "-" + cnfParams.getString("body"); // 商品描述 String(128)
            String nonceStr = RandomUtils.uuid().toUpperCase();
            String signType = "MD5";
            String outTradeNo = order.getOrderNumber();
            long totalFee = CalcNumber.as(payPrice).toPennyPrice();
            String spbillCreateIp = order.getIp(); // 终端IP String(64)
            String timeStart = format.format(order.getTimeStart()); // 交易起始时间  20091225091010
            String timeExpire = format.format(order.getTimeExpire()); // 交易结束时间 20091227091010
            String notifyUrl = order.getHost() + "/payment/" + name + ".jhtml"; // 通知地址


            List<OrderPaymentProduct> goods = order.getProducts();
            ModelArray detailGoods = new ModelArray();
            if (goods != null && goods.size() > 0) {
                for (OrderPaymentProduct model : goods) {
                    long gid = model.getGid();
                    String goodsName = model.getProductName();
                    int quantity = model.getAmount();
                    BigDecimal priceBigDecimal = model.getProductPrice();
                    long price = CalcNumber.as(priceBigDecimal).toPennyPrice();

                    ModelObject goodsItem = new ModelObject();
                    goodsItem.put("goods_id", gid);
                    goodsItem.put("goods_name", goodsName);
                    goodsItem.put("quantity", quantity);
                    goodsItem.put("price", price);
                    detailGoods.add(goodsItem);
                }
            }
            String detail = detailGoods.toJSONString();

            PaymentResult result = new PaymentResult();
            result.setPayPrice(payPrice);
            result.setType(PaymentResult.TYPE.PARAMS);
            result.setPaymentNumber(nonceStr);

            Map<String, String> params = new TreeMap<>();
            params.put("appid", appid);
            params.put("mch_id", mchId);
            params.put("nonce_str", nonceStr);
            params.put("sign_type", signType);
            params.put("body", body);
            params.put("detail", detail);
            params.put("attach", attach);
            params.put("out_trade_no", outTradeNo);
            params.put("total_fee", "" + totalFee);
            params.put("spbill_create_ip", spbillCreateIp);
            params.put("notify_url", notifyUrl);
            params.put("trade_type", tradeType);
            params.put("time_start", timeStart);
            params.put("time_expire", timeExpire);
            params.put("openid", openid);

            String signStr = StringTools.map2UrlQueryString(params);
            signStr += "&key=" + secretKey.trim();
            String sign = MD5Utils.md5(signStr).toUpperCase();

            StringBuilder sb = new StringBuilder();
            sb.append("<xml>");
            sb.append("<appid><![CDATA[" + appid + "]]></appid>");
            sb.append("<mch_id><![CDATA[" + mchId + "]]></mch_id>");
            sb.append("<nonce_str><![CDATA[" + nonceStr + "]]></nonce_str>");
            sb.append("<sign_type><![CDATA[" + signType + "]]></sign_type>");
            sb.append("<body><![CDATA[" + body + "]]></body>");
            sb.append("<detail><![CDATA[" + detail + "]]></detail>");
            sb.append("<attach><![CDATA[" + attach + "]]></attach>");
            sb.append("<out_trade_no><![CDATA[" + outTradeNo + "]]></out_trade_no>");
            sb.append("<total_fee>" + totalFee + "</total_fee>");
            sb.append("<spbill_create_ip><![CDATA[" + spbillCreateIp + "]]></spbill_create_ip>");
            sb.append("<notify_url><![CDATA[" + notifyUrl + "]]></notify_url>");
            sb.append("<trade_type><![CDATA[" + tradeType + "]]></trade_type>");
            sb.append("<time_start><![CDATA[" + timeStart + "]]></time_start>");
            sb.append("<time_expire><![CDATA[" + timeExpire + "]]></time_expire>");
            sb.append("<sign><![CDATA[" + sign + "]]></sign>");
            sb.append("<openid><![CDATA[" + openid + "]]></openid>");
            sb.append("</xml>");

            String resp = HttpUtils.post("https://api.mch.weixin.qq.com/pay/unifiedorder", sb.toString());
            ModelObject respJson = WeChatInterfaceUtils.xmlToModel(resp);
            if (respJson.containsKey("return_code")
                    && respJson.getString("return_code").trim().equals("SUCCESS")
                    && respJson.containsKey("result_code")
                    && respJson.getString("result_code").trim().equals("SUCCESS")) {
                respJson.remove("sign");
                respJson.remove("appid");
                respJson.remove("mch_id");
                respJson.remove("nonce_str");

                ModelObject payParams = new ModelObject();
                payParams.put("appId", appid);
                payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                payParams.put("nonceStr", RandomUtils.uuid().toUpperCase());
                payParams.put("package", "prepay_id=" + respJson.getString("prepay_id"));
                payParams.put("signType", "MD5");

                Map treeMap = new TreeMap(payParams);
                String signJSStr = StringTools.map2UrlQueryString(treeMap) + "&key=" + secretKey.trim();
                String signCallJS = MD5Utils.md5(signJSStr).toUpperCase();
                payParams.put("paySign", signCallJS);
                if (respJson.containsKey("code_url")) {
                    payParams.put("code_url", respJson.getString("code_url"));
                }


                if (tradeType.equalsIgnoreCase("JSAPI")) {
                    result.setType(PaymentResult.TYPE.JS_API);
                    payParams.put("jsCode", "" +
                            "function onPaymentCall(){" +
                            "   WeixinJSBridge.invoke(" +
                            "       'getBrandWCPayRequest', {" +
                            "           \"appId\":\"" + appid + "\"," +
                            "           \"timeStamp\":\"" + payParams.getString("timeStamp") + "\"," +
                            "           \"nonceStr\":\"" + payParams.getString("nonceStr") + "\"," +
                            "           \"package\":\"" + payParams.getString("package") + "\"," +
                            "           \"signType\":\"MD5\"," +
                            "           \"paySign\":\"" + payParams.getString("paySign") + "\"" +
                            "       }," +
                            "       function(res){" +
                            "           if(console && console.log)console.log(res);" +
                            "           if(res.err_msg == \"get_brand_wcpay_request:ok\" ){" +
                            "               onPaymentSuccessCallback(res);" +
                            "           } " +
                            "       }); " +
                            "}");

                }
                if (tradeType.equalsIgnoreCase("Native")) {
                    result.setType(PaymentResult.TYPE.QR_CODE);
                }
                result.setParams(payParams);
                return result;
            } else {
                logger.error("创建微信支付单失败:" + respJson.toJSONString());
                throw new ModuleException("wechat_order_error", "微信统一下单错误: " + resp);
            }
        }
        return null;
    }

    @Override
    public PaymentResult trial(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws Exception {
        return null;
    }

    @Override
    public PaymentFlow getPaymentFlow() {
        return PaymentFlow.DELAY;
    }

    @Override
    public boolean isCanCallback(String name) {
        if (WechatPaymentChannel.name.equals(name)) {
            return true;
        }
        return false;
    }

    @Override
    public String paymentCallback(HttpServletRequest request, HttpServletResponse response, String body) throws Exception {
        if (StringUtils.isNotBlank(body)) {
            ModelObject respJson = WeChatInterfaceUtils.xmlToModel(body);
            logger.info("微信支付回调处理参数: " + respJson.toJSONString());
            String resultCode = respJson.getString("result_code");
            if (StringUtils.isNotBlank(resultCode) && resultCode.trim().equals("SUCCESS")) {
                String orderNumber = respJson.getString("out_trade_no");
                String paymentNumber = respJson.getString("nonce_str");
                String transactionId = respJson.getString("transaction_id");
                ModelObject channel = GlobalService.paymentService.getOrderPaymentChannel(orderNumber, paymentNumber);
                String secretKey = channel.getString("secret_key");

                Iterator iterator = respJson.entrySet().iterator();
                Map<String, String> map = new TreeMap<String, String>();
                while (iterator.hasNext()) {
                    Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) iterator.next();
                    map.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                }
                String sign = map.get("sign");
                map.remove("sign");
                iterator = map.entrySet().iterator();
                StringBuilder sb = new StringBuilder();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                    sb.append(entry.getKey() + "=" + entry.getValue());
                    if (iterator.hasNext()) {
                        sb.append("&");
                    }
                }

                String newSign = MD5Utils.md5(sb.append("&key=" + secretKey).toString()).toUpperCase();
                if (sign.equals(newSign)) {
                    ModelObject object = new ModelObject();
                    object.put(TablePaymentResponse.orderNumber, orderNumber);
                    object.put(TablePaymentResponse.paymentNumber, paymentNumber);
                    object.put(TablePaymentResponse.outPaymentNumber, transactionId);
                    object.put(TablePaymentResponse.body, body);
                    boolean success = sessionTemplate.execute(new TransactionCallback<Boolean>() {
                        @Override
                        public Boolean invoke(Transaction transaction) throws Exception {
                            GlobalService.paymentService.setOrderPaymentSuccess(orderNumber, paymentNumber, transactionId);
                            GlobalService.paymentService.addPaymentTrans(object);
                            logger.info("微信支付回调处理成功: 订单号:" + orderNumber + " , 微信支付单号:" + transactionId);
                            return true;
                        }
                    });
                    if (success) {
                        return "<xml>" +
                                "  <return_code><![CDATA[SUCCESS]]></return_code>" +
                                "  <return_msg><![CDATA[OK]]></return_msg>" +
                                "</xml>";
                    }
                } else {
                    logger.error("微信支付回调签名不正确: my " + newSign + ",from " + sign);
                }
            }
        }
        return null;
    }

    @Override
    public String refundCallback(HttpServletRequest request, HttpServletResponse response, String body) throws Exception {
        if (StringUtils.isNotBlank(body)) {
            ModelObject bodyJson = WeChatInterfaceUtils.xmlToModel(body);
            String resultCode = bodyJson.getString("return_code");
            if (StringUtils.isNotBlank(resultCode) && resultCode.trim().equals("SUCCESS")) {
                Map<String, String> values = new HashMap<>();
                String appid = bodyJson.getString("appid");
                values.put("appid", appid);
                values.put("mch_id", bodyJson.getString("mch_id"));
                ModelObject channel = GlobalService.paymentService.getPaymentChannelByArguments(values);
                String secretKey = channel.getString("secret_key");


                /**
                 * 由于微信退款消息加密过于傻逼，会降低人的智商
                 * 如果使用到微信退款回调则必须替换 jre/lib/security 下的包
                 */

                try {
                    String md5Key = MD5Utils.md5(secretKey).toLowerCase();
                    String reqInfo = bodyJson.getString("req_info");

                    Security.addProvider(new BouncyCastleProvider());
                    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(md5Key.getBytes(), "AES"));
                    reqInfo = new String(cipher.doFinal(Base64.decodeBase64(reqInfo)));


                    ModelObject respJson = WeChatInterfaceUtils.xmlToModel(reqInfo);
                    String orderNumber = respJson.getString("out_trade_no");
                    String nonce_str = respJson.getString("nonce_str");
                    String transactionId = respJson.getString("transaction_id");
                    String refundId = respJson.getString("refund_id"); //微信退款单号
                    String outRefundNo = respJson.getString("out_refund_no"); // 商户退款单号

                    logger.info("微信退款回调处理: out_refund_no:" + outRefundNo);

                    GlobalService.paymentService.setHandBackSuccess(outRefundNo, refundId);
                } catch (Exception e) {
                    logger.error("如果是解密错误则需要替换jre/lib/security下的local_policy.jar,US_export_policy.jar。" +
                            "该文件在WEB-INF的classes的libs中。如果是其它错误请忽略!", e);
                }
            }
            return "<xml>" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>" +
                    "  <return_msg><![CDATA[OK]]></return_msg>" +
                    "</xml>";
        }
        return "empty_body";
    }

    /**
     * 查询微信订单
     * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2
     *
     * @return
     */
    private ModelObject queryOrder() {
        return null;
    }

    /**
     * 申请退款
     * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_4
     * <p>
     * 1、交易时间超过一年的订单无法提交退款
     * 2、微信支付退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。申请退款总金额不能超过订单金额。
     * 一笔退款失败后重新提交，请不要更换退款单号，请使用原商户退款单号
     * 3、请求频率限制：150qps，即每秒钟正常的申请退款请求次数不超过150次
     * 错误或无效请求频率限制：6qps，即每秒钟异常或错误的退款申请请求不超过6次
     * 4、每个支付订单的部分退款次数不能超过50次
     *
     * @param refundParams
     */
    @Override
    public void handBack(PaymentRefundParams refundParams) throws ModuleException {
        long refundOrderId = refundParams.getRefundOrderId();
        ModelObject refundOrder = refundParams.getRefundOrder();
        if (refundOrder == null) {
            refundOrder = GlobalService.refundOrderService.getRefundOrder(refundOrderId);
        }
        if (refundOrder != null) {
            ModelObject channel = refundParams.getChannel();
            ModelObject trans = refundParams.getTrans();
            ModelObject cnfParams = refundParams.getCnfParams();
            BigDecimal refundPrice = refundParams.getPayPrice();
            refundPrice = new BigDecimal(CalcNumber.as(refundPrice).toPrice());

            BigDecimal payPrice = trans.getBigDecimal(TablePaymentTransaction.payPrice);
            String outPaymentNumber = trans.getString(TablePaymentTransaction.outPaymentNumber);

            String secretKey = cnfParams.getString("secret_key");
            if (StringUtils.isBlank(secretKey)) {
                throw new ModuleException("wechat_pay_args_secret_key", "缺少支付接口信息secret_key");
            }
            String appid = cnfParams.getString("appid");
            if (StringUtils.isBlank(appid)) {
                throw new ModuleException("wechat_pay_args_appid", "缺少支付接口信息appid");
            }
            String mchId = cnfParams.getString("mch_id");
            if (StringUtils.isBlank(mchId)) {
                throw new ModuleException("wechat_pay_args_mchId", "缺少支付接口信息mchId");
            }

            String certFile = channel.getString(TablePaymentChannel.certFile);
            String password = channel.getString(TablePaymentChannel.certPassword);
            if (StringUtils.isBlank(certFile)) {
                throw new ModuleException("wechat_refund_not_found_cert", "没有找到证书文件");
            }
            ModelObject realFile = GlobalService.fileService.getFileByName(certFile);
            if (realFile == null) {
                throw new ModuleException("wechat_refund_not_found_cert", "没有找到证书文件");
            }
            if (StringUtils.isBlank(password)) {
                throw new ModuleException("wechat_refund_cert_pwd", "没有找到证书文件");
            }

            String tradeType = cnfParams.getString("trade_type");
            long totalFee = payPrice.multiply(new BigDecimal(100)).longValue();
            long refundFee = refundPrice.multiply(new BigDecimal(100)).longValue();
            String notifyUrl = GlobalService.systemConfigService.getWebUrl() +
                    "/payment/refund/" + name + ".jhtml";
            String transactionId = outPaymentNumber;
            Map<String, String> params = new TreeMap<>();
            params.put("appid", appid);
            params.put("mch_id", mchId);
            params.put("nonce_str", RandomUtils.uuid().toUpperCase());
            params.put("sign_type", "MD5");
            params.put("transaction_id", transactionId);
            params.put("out_refund_no", refundOrder.getString(TableRefundOrder.number));
            params.put("total_fee", "" + totalFee);
            params.put("refund_fee", "" + refundFee);
            params.put("refund_desc", refundOrder.getString(TableRefundOrder.detail));
            params.put("notify_url", notifyUrl);

            String signStr = StringTools.map2UrlQueryString(params) + "&key=" + secretKey;
            String sign = MD5Utils.md5(signStr).toUpperCase();

            StringBuilder xml = new StringBuilder();
            xml.append("<xml>" +
                    "   <appid>" + appid + "</appid>" +
                    "   <mch_id>" + mchId + "</mch_id>" +
                    "   <nonce_str>" + params.get("nonce_str") + "</nonce_str> " +
                    "   <sign_type>MD5</sign_type> " +
                    "   <transaction_id>" + transactionId + "</transaction_id>" +
                    "   <out_refund_no>" + params.get("out_refund_no") + "</out_refund_no>" +
                    "   <total_fee>" + refundFee + "</total_fee>" +
                    "   <refund_fee>" + totalFee + "</refund_fee>" +
                    "   <refund_desc>" + params.get("refund_desc") + "</refund_desc>" +
                    "   <notify_url>" + notifyUrl + "</notify_url>" +
                    "   <sign>" + sign + "</sign>" +
                    "</xml>");

            File file = GlobalService.fileService.getSystemFile(realFile);
            InputStream fileInput = null;
            try {
                fileInput = new FileInputStream(file);
                String resp = HttpUtils.postP12SSL("https://api.mch.weixin.qq.com/secapi/pay/refund", xml.toString(),
                        fileInput, password);

                ModelObject respJson = WeChatInterfaceUtils.xmlToModel(resp);
                if (respJson.containsKey("return_code") && respJson.getString("return_code").trim().equalsIgnoreCase("SUCCESS")) {
                    String respSign = respJson.getString("sign");
                    respJson.remove("sign");
                    String newSign = MD5Utils.md5(StringTools.map2UrlQueryString(new TreeMap(respJson)) + "&key=" + secretKey).toUpperCase();
                    logger.info("微信退款接口调用成功: refund_id: "
                            + (respJson.containsKey("refund_id") ? respJson.getString("refund_id") : "")
                            + "  sign:" + newSign);
                    if (respSign.equalsIgnoreCase(newSign)) {

                    }
                } else {
                    throw new ModuleException("wechat_order_error", "微信退款请求错误: " + resp);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ModuleException("wechat_refund_error", "微信退款请求出错", e);
            } finally {
                if (fileInput != null) {
                    try {
                        fileInput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            throw new ModuleException("not_found_refund_order", "没有找到退款单(" + refundOrderId + ")信息");
        }
    }
}
