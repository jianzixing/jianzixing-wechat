/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 *
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.jianzixing.webapp.service.wechat.aes;

import java.security.MessageDigest;
import java.util.*;

/**
 * SHA1 class
 * <p>
 * 计算公众平台的消息签名接口.
 */
public class WeChatSHA1 {

    /**
     * 用SHA1算法生成安全签名
     *
     * @param token     票据
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param encrypt   密文
     * @return 安全签名
     * @throws AesException
     */
    public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException {
        String[] array = new String[]{token, timestamp, nonce, encrypt};
        return getSHA1(array);
    }

    public static String getSHA1(String... array) throws AesException {
        try {
            StringBuffer sb = new StringBuffer();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < array.length; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1签名生成
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.ComputeSignatureError);
        }
    }

    public static String getSHA1(Map<String, String> map) throws AesException {
        if (map != null) {
            List<String> list = new ArrayList<>();
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                list.add(entry.getValue());
            }
            String[] array = list.toArray(new String[]{});
            return getSHA1(array);
        }
        return null;
    }

    public static void main(String[] args) throws AesException {
        System.out.println(getSHA1(
                "abc",
                "1560333501",
                "1107886625"));
    }
}
