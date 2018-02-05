package org.interview.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.interview.exception.DatahubException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 对称加密工具类
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class AesUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(AesUtil.class);

	private static final String AES		= "AES";
	/**默认加密种子**/
	public static final String AES_SEED	= "interview";

	/**
	 * 加密
	 */
	public static String encryptStr(String content, String seed) {
		byte[] bytes = encrypt(content, seed);
		return new String(Base64.encodeBase64(bytes));
	}

	public static String encryptHexStr(String content, String seed) {
		byte[] bytes = encrypt(content, seed);
		return new String(Hex.encodeHex(bytes));
	}

	/**
	 * 解密
	 * @throws DatahubException 
	 */
	public static String decryptStr(String content, String seed) throws DatahubException {
		byte[] bytes = Base64.decodeBase64(content.getBytes());
		String originalStr = new String(AesUtil.decrypt(bytes, seed));
		return originalStr;
	}

	public static String decryptHexStr(String content, String seed) throws DatahubException {
		try {
			byte[] bytes = Hex.decodeHex(content.toCharArray());
			String originalStr = new String(AesUtil.decrypt(bytes, seed));
			return originalStr;
		} catch (Exception e) {
			if(e instanceof DatahubException){
				throw (DatahubException)e;
			}
			throw new DatahubException(e);
		}
	}

	/**
	 * 加密
	 */
	private static byte[] encrypt(String content, String seed) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(AES);
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(seed.getBytes());
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES);
			Cipher cipher = Cipher.getInstance(AES);
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(byteContent);
			return result;
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("NoSuchAlgorithmException", e);
		} catch (NoSuchPaddingException e) {
			LOGGER.error("NoSuchPaddingException", e);
		} catch (InvalidKeyException e) {
			LOGGER.error("InvalidKeyException", e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException", e);
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("IllegalBlockSizeException", e);
		} catch (BadPaddingException e) {
			LOGGER.error("BadPaddingException", e);
		}
		return null;
	}

	/**
	 * 解密
	 * @throws DatahubException 
	 */
	private static byte[] decrypt(byte[] content, String seed) throws DatahubException {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(AES);
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(seed.getBytes());
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES);
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (Exception e) {
			throw new DatahubException(e);
		}
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * 加密
	 */
	public static byte[] encrypt2(String content, String seed) {
		try {
			SecretKeySpec key = new SecretKeySpec(seed.getBytes(), AES);
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(byteContent);
			return result;
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("NoSuchAlgorithmException", e);
		} catch (NoSuchPaddingException e) {
			LOGGER.error("NoSuchPaddingException", e);
		} catch (InvalidKeyException e) {
			LOGGER.error("InvalidKeyException", e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException", e);
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("IllegalBlockSizeException", e);
		} catch (BadPaddingException e) {
			LOGGER.error("BadPaddingException", e);
		}
		return null;
	}

}
