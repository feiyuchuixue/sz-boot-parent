package com.sz.core.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class AESUtil {

    private AESUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final Random RANDOM = new Random();

    private static final String ALGORITHMSTR = "AES/CBC/PKCS5Padding";

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes
     *            byte[]
     * @param radix
     *            可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    /**
     * base 64 encode
     *
     * @param bytes
     *            待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * base 64 decode
     *
     * @param base64Code
     *            待解码的base 64 code
     * @return 解码后的byte[]
     */
    public static byte[] base64Decode(String base64Code) {
        Base64.Decoder decoder = Base64.getDecoder();
        return StringUtils.isEmpty(base64Code) ? null : decoder.decode(base64Code);
    }

    /**
     * AES加密
     *
     * @param content
     *            待加密的内容
     * @param encryptKey
     *            加密密钥
     * @param iv
     *            初始化向量 (IV)，确保每次加密不同
     * @return 加密后的byte[]
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // 生成AES密钥
        SecretKeySpec keySpec = new SecretKeySpec(encryptKey.getBytes(StandardCharsets.UTF_8), "AES");

        // 初始化IV
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 创建Cipher实例
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR); // Encryption algorithms should be used with secure mode and padding scheme
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec); // 使用CBC模式初始化Cipher
        return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * AES加密为base 64 code
     *
     * @param content
     *            待加密的内容
     * @param encryptKey
     *            加密密钥
     * @param iv
     *            初始化向量 (IV)，确保每次加密不同
     * @return 加密后的base 64 code
     */
    public static String aesEncrypt(String content, String encryptKey, byte[] iv) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (StringUtils.isBlank(encryptKey)) {
            return content;
        }
        return base64Encode(aesEncryptToBytes(content, encryptKey, iv));
    }

    /**
     * AES解密
     *
     * @param encryptBytes
     *            待解密的byte[]
     * @param decryptKey
     *            解密密钥
     * @param iv
     *            初始化向量 IV
     * @return 解密后的String
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey, String iv) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // 初始化向量
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

        // 初始化密钥
        SecretKeySpec keySpec = new SecretKeySpec(decryptKey.getBytes(StandardCharsets.UTF_8), "AES");

        // 初始化加密器
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // 执行解密
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes, StandardCharsets.UTF_8);
    }

    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr
     *            待解密的base 64 code
     * @param decryptKey
     *            解密密钥
     * @param iv
     *            初始化向量 IV
     * @return 解密后的string
     */
    public static String aesDecrypt(String encryptStr, String decryptKey, String iv) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (StringUtils.isBlank(decryptKey)) {
            return encryptStr;
        }
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey, iv);
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = RANDOM.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
