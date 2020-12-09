package com.yoyo.base.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


public class SignUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignUtil.class);

    public static final String KEY_SHA = "SHA-256";
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String PUBLIC_KEY = "RSAPublicKey";
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    public static final String[] responseFieldsToSign = {"code", "data", "msg"};

    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decodeBase64(key);
    }

    /**
     * BASE64加密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return Base64.encodeBase64String(key);
    }

    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, String> initKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        //公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, String> keyMap = new HashMap<String, String>(2);
        keyMap.put(PUBLIC_KEY, encryptBASE64(publicKey.getEncoded()));
        keyMap.put(PRIVATE_KEY, encryptBASE64(privateKey.getEncoded()));

        return keyMap;
    }

    /**
     * RSA签名
     *
     * @param content    待签名数据
     * @param privateKey 私钥
     * @return 签名值
     */
    public static String sign(String content, String privateKey) {
        try {
            //解密私钥
            byte[] keyBytes = decryptBASE64(privateKey);
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(keyBytes);
            //指定加密算法
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            //取私钥匙对象
            PrivateKey privateKey2 = keyFactory.generatePrivate(priPKCS8);
            //用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey2);
            signature.update(content.getBytes("utf-8"));

            return encryptBASE64(signature.sign()).replaceAll("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean checkSign(String data, String publicKey, String sign) throws Exception {
        //解密公钥
        byte[] keyBytes = decryptBASE64(publicKey);
        //构造X509EncodedKeySpec对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //取公钥匙对象
        PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey2);
        signature.update(data.getBytes("utf-8"));

        //验证签名是否正常
        return signature.verify(decryptBASE64(sign));

    }

//    public static String signResponse(RespData response, String signPrivateKey) throws Exception {
//        //BasePpBizResponse bizResponse = ppResponse.getBizResponse();
//        String dataStrToSign = SignUtil.getResponseSignData(response);//2
//
//        //todo sign
//        LOGGER.info("Response:" + response + ",sign content:" + dataStrToSign);
//        String sign = sign(dataStrToSign, signPrivateKey);
//        return sign;
//    }
//
//    public static String getResponseSignData(RespData response) {
//        return getSignData(response, responseFieldsToSign);
//    }

    public static void main(String[] args) throws Exception {
//        Map map = initKey();
//        System.out.println(map);
        String text = "hello123";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALqQPiEW9VIaUfgzk2jVKvCPU6nJoTNRw1SqqbSc2COMWAtq6S2sf7DL6NaWRsLsoQIjIyJgx97DCk+qDxxcJtDXb29FRxt+tOEJuewgvJRq+76U9GX/Ig6DvYQjC4bCfL/w7qk/cWEesiIGvqmx2t93Y2Hm4uFwG5ViA+sJFy59AgMBAAECgYEAoWvm5fiYYOa3H9t4Y092LNjGmfUEzoOjT+GunsDK3s8y6wYhJczIWy/DkdBK/6OAmHnQj5FPvrXheRZ6pp4xUi0qwZ2wKPJ7kq4Ug6Hg5OTRYwxdKIATLffZJWfBFi6Omv76PmActgZWZjjSbWtrGDrDMiF6IEXrE98QyIxigSECQQDmljTXllbXcFDyBJPCw1r+dQ9bNDOVE2c90gQcu2oCLsqhOlo0tQd6ttRk21HAunDBEZ/aooYVVE8LdBcOzXbpAkEAzx/1l6w27/o+LnBAKZ60By0/GQcvXIKpAtiZ0k13kFeqqPBOXPbSkp8CRz7ihJ26QWl8t/6P8wyu3SXeJfdmdQJBAMGNRc/NBxoR0jBEPU+XsbKzye8Rk1bIEbonpoIDoskwQ7AwDfX+Gsgb3Y7HNgljti+pvpfEIm6W9T609IHdX7kCQDNlFMXL/93QVHmldOZe8QEO4ydMtx3XdiS0poaenlp8xmYhKvC6dknXlvMi0YZitQLiMAZf7kw7C3DoopT9LLkCQFuxaDQ6sxmmw4ue2ol7RZatm2IPE9gc2SwkLNwOWhLUaaBsuUdVPae5ohAYNHCFoetbam6eBPAb0z5KHQpJs48=";
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6kD4hFvVSGlH4M5No1Srwj1OpyaEzUcNUqqm0nNgjjFgLauktrH+wy+jWlkbC7KECIyMiYMfewwpPqg8cXCbQ129vRUcbfrThCbnsILyUavu+lPRl/yIOg72EIwuGwny/8O6pP3FhHrIiBr6psdrfd2Nh5uLhcBuVYgPrCRcufQIDAQAB";
        String sign = sign(text, privateKey);
        System.out.println("checkSign = [" + checkSign(text, pubKey, sign) + "]");


    }


}
