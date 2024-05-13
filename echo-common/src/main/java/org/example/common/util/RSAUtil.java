package org.example.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAUtil {

    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA"; // ALGORITHM ['ælgərɪð(ə)m] 算法的意思

    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI7/72P0DPBO+dkO7KIk9F4nFtyh/9W7jXNUXeZMrb9Y0w0aV2dUdowF4WUkrYOXRHRJxsoZuAJ8J597plIXwUH8VvxX1+/+8f+QgJD6CuNjv0cuup6vKCJTU5pYe5xtHgQCY7xx8Fhz9OlSqIP2G4+u13U4xfiM9nIuByv7dyC3AgMBAAECgYAJn2mPQW593ORAoEz5kkc+O+vBa0aJR7nHVeXygYMI08pyYMa0UnSejCZa0iK5yEjLRhje8IEd7Bd8fdHqen4lWVSsGbns24BjIIVKc/rerH1tsak7Pes1A8Ce2dimDDSl7DfQ7/P/ipf7myc5X+TJpZ9U6Ba9OkpBZdNu/S/kRQJBALtxi/DxEkvH5xIAHhDUuqoRYxT8xh0LbkizAr+Gk6lT1YfeXfQqOw/x3Qkfx92Ol6Yf0XmEdjRP7YOwAPQeONUCQQDDTRa6hmzRKsplKB4t3r2ZWJhq3OzNtqtcG6knPu+sTbZQcHZtL/X83qtByGsQmyLcmIqU9AfhKJCtPbl6i7lbAkEAqLcqW+PRMQ6sqOIcLdAceCmQQnrHI6t/R8aA6b2LOXUxFMuNS9GbOd3cs3K8pmrLiwbGuJcHAk0TH14TbaeqRQJAa1ZL/pnw5ojBEhWZeJZp2ATUSC9BytHxnj7pmdNCQI25fL5CqEHqzV7H+vCev1K0N09zIuh4CCyWT1CURyDR7wJAYWSA1prjtMDn710XS4wZqEAh5sIwgZLHXuLofYAMSyj/BF03bNdmERDLAPz9rJ2TNJuZdL1OuQSBOiVI5Q4lBw==";

    public static Map<String, String> createKeys(int keySize) {
        // 为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        // 初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        // 生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        // 得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        // 得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
        // map装载公钥和私钥
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        // 返回map
        return keyPairMap;
    }

    /**
     * 得到公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) {
        try {
            // 通过X509编码的Key指令获得公钥对象
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
            return key;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 得到私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) {
        try {
            // 通过PKCS#8编码的Key指令获得私钥对象
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
            return key;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String privateDecrypt(String encrypted) {
        final RSAPrivateKey privateKey = getPrivateKey(PRIVATE_KEY);
        return privateDecrypt(encrypted, privateKey);
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     */
    public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     *
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            //每个Cipher初始化方法使用一个模式参数opmod，并用此模式初始化Cipher对象。此外还有其他参数，包括密钥key、包含密钥的证书certificate、算法参数params和随机源random。
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    //rsa切割解码  , ENCRYPT_MODE,加密数据   ,DECRYPT_MODE,解密数据
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;  //最大块
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    //可以调用以下的doFinal（）方法完成加密或解密数据：
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

    public static void main(String[] args) {
        // 创建密钥对
        Map<String, String> keys = createKeys(1024);
        // 从Map中获取密钥对
        String publicKey = keys.get("publicKey");
        String privateKey = keys.get("privateKey");
        // 获取公钥
        System.out.println("publicKey:" + publicKey);
        // 获取私钥
        System.out.println("privateKey:" + privateKey);

//        RSAPublicKey publicKey = getPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkF5XXOuBTf6QCMLo0TK1NBw4MTMoGIwCVbZe5h0mLwjgQlvTE07OMQkMo5n7HppTbD84RPRGFx2y0cCV1dUz8urBRHUUeod7OwgIVUjeiW4lRASzhsb6y7LecaDrxJCgoLSz2DeGoeDP2bOQAsYhh8GLAy0YWIe5jNT5HuTlp0QIDAQAB");
//        final String ecrypted = publicEncrypt("123456", publicKey);
//        final RSAPrivateKey privateKey = getPrivateKey("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKQXldc64FN/pAIwujRMrU0HDgxMygYjAJVtl7mHSYvCOBCW9MTTs4xCQyjmfsemlNsPzhE9EYXHbLRwJXV1TPy6sFEdRR6h3s7CAhVSN6JbiVEBLOGxvrLst5xoOvEkKCgtLPYN4ah4M/Zs5ACxiGHwYsDLRhYh7mM1Pke5OWnRAgMBAAECgYArPK22VySpy722+AjwY+3cGIpKkmc/I7TxKr3qXGv0H/pu4J/hRxkN0Z3ppoZsbAfYCvuUzj8SPYB2sQAlyYE9wAq4YyNFCCmw5j1vVjk7IjIkvNMUCTcw9I1gY/OA5+X6OVmPH/wRRvK18pm6ikQTJ0fGNlv3ALhGcWlMCa+2KwJBAONFfC1dPsseTfDNwMk0iZVHIUOn6m/2Z8p5JZxm5MPdkJxn0JiS8+tYQLFiIuQ9yKOG74QEfAijzZRrhOfp4xsCQQC41Z1xa1RoBes47YEQ8i0wafoXG5B+zgkX9zAVxJpny11lYaiD4nlLthvPgrWXDbjz/FuXNACZ75DhrX4VTsmDAkEA4RKkSqzIjhGB8JNjdB+SjYJ23/DATIdC/JKDe+OudIbNOwfY3fDSnWS2FLVyhoTlWYU9WIm0aD4ITw83/RB0twJAA23zPed/6W4yrxu5CVd0/zD4MvWP+MpZ4Nf1THIZa1OaqAbn2YqaNY1rdq42MFdQ1zYXfPa6DCElzwopDLwpgQJBAKEnFsZwFCJZ76uHy5JacRss9d+UQ+Q4hyly1Vp7wEqjDifFWzpjzDuMWzSadCnwI6QtRpDS9woj26xZjf5y1/c=");
//        final String decrypted = privateDecrypt(ecrypted, privateKey);
    }

}
