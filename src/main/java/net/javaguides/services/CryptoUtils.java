package net.javaguides.services;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

// Ref http://java.sun.com/developer/technicalArticles/Security/CryptoUtils/AES_v1.html
// Ref http://www.java2s.com/Code/Java/Development-Class/ConverthexToBytes.htm

public class CryptoUtils {

  private static Logger logger = LogManager.getLogger(CryptoUtils.class);

  public static String keyGenerationSeed = null;

  public static String keyGenerationSeedCustom = "AlphaMangus#654321@98765";

  /**
   * Turns array of bytes into string
   * 
   * @param buf Array of bytes to convert to hex string
   * @return Generated hex string
   */
  public static String asHex(byte buf[]) {
    StringBuffer strbuf = new StringBuffer(buf.length * 2);
    int i;

    for (i = 0; i < buf.length; i++) {
      if ((buf[i] & 0xff) < 0x10) {
        strbuf.append("0");
      }

      strbuf.append(Long.toString(buf[i] & 0xff, 16));
    }

    return strbuf.toString().toUpperCase();
  }

  /**
   * Converts character array to byte array
   * 
   * @param hexString
   * @return {@link Byte} array
   */
  public static byte[] hexToBytes(String hexString) {
    char[] hex = hexString.toCharArray();
    int length = hex.length / 2;
    byte[] raw = new byte[length];
    for (int i = 0; i < length; i++) {
      int high = Character.digit(hex[i * 2], 16);
      int low = Character.digit(hex[i * 2 + 1], 16);
      int value = (high << 4) | low;
      if (value > 127) {
        value -= 256;
      }
      raw[i] = (byte) value;
    }
    return raw;
  }

  public static String encrypt(String message, String key) {
    // System.out.println("key is"+ key);
    if (message == null) {
      return message;
    }

    // logger.debug("### message : " + message);
    SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
    byte[] encrypted = null;
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
      encrypted = cipher.doFinal(message.getBytes("UTF-8"));
    } catch (Exception e) {
      logger.error("Error found...", e);
    }
    return asHex(encrypted);
  }

  public static String decrypt(String codedMessage, String key) {
    if (codedMessage == null) {
      return codedMessage;
    }
    // logger.debug("### cipher Text : " + codedMessage);
    String originalString = null;
    try {
      SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, skeySpec);
      byte[] original = cipher.doFinal(hexToBytes(codedMessage));
      originalString = new String(original, "UTF-8");
    } catch (Exception e) {
      logger.error("Error found...", e);
    }
    return originalString;
  }

  public static void main(String[] args) throws Exception {
    keyGenerationSeed = "AlphaMango654321";
    String message = "SampleEncryption";
    String enc = encrypt(message, keyGenerationSeed);
    String dec = decrypt(enc, keyGenerationSeed);
    System.out.println("key: " + keyGenerationSeed + " len: " + keyGenerationSeed.length());
    System.out.println("Encypted one: " + enc);
    System.out.println("Decypted one: " + dec);
  }

  /**
   * @return the keyGenerationSeed
   */
  public static String getKeyGenerationSeed() {
    return keyGenerationSeed;
  }

  /**
   * @param keyGenerationSeed the keyGenerationSeed to set
   */
  public static void setKeyGenerationSeed(String keyGenerationSeed) {
    CryptoUtils.keyGenerationSeed = keyGenerationSeed;
  }

  public static String decryptCryptoJS(String encryptPassword, String keyStr) {
    try {
      byte[] cipherData = Base64.getDecoder().decode(encryptPassword);
      byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

      MessageDigest md5 = MessageDigest.getInstance("MD5");
      final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, keyStr.getBytes(StandardCharsets.UTF_8), md5);
      SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
      IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

      byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
      Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
      aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
      byte[] decryptedData = aesCBC.doFinal(encrypted);
      return new String(decryptedData, StandardCharsets.UTF_8);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Generates a key and an initialization vector (IV) with the given salt and password.
   * <p>
   * This method is equivalent to OpenSSL's EVP_BytesToKey function
   * (see https://github.com/openssl/openssl/blob/master/crypto/evp/evp_key.c).
   * By default, OpenSSL uses a single iteration, MD5 as the algorithm and UTF-8 encoded password data.
   * </p>
   * @param keyLength the length of the generated key (in bytes)
   * @param ivLength the length of the generated IV (in bytes)
   * @param iterations the number of digestion rounds
   * @param salt the salt data (8 bytes of data or <code>null</code>)
   * @param password the password data (optional)
   * @param md the message digest algorithm to use
   * @return an two-element array with the generated key and IV
   */
  private static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

    int digestLength = md.getDigestLength();
    int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
    byte[] generatedData = new byte[requiredLength];
    int generatedLength = 0;

    try {
      md.reset();

      // Repeat process until sufficient data has been generated
      while (generatedLength < keyLength + ivLength) {

        // Digest data (last digest if available, password data, salt if available)
        if (generatedLength > 0)
          md.update(generatedData, generatedLength - digestLength, digestLength);
        md.update(password);
        if (salt != null)
          md.update(salt, 0, 8);
        md.digest(generatedData, generatedLength, digestLength);

        // additional rounds
        for (int i = 1; i < iterations; i++) {
          md.update(generatedData, generatedLength, digestLength);
          md.digest(generatedData, generatedLength, digestLength);
        }

        generatedLength += digestLength;
      }

      // Copy key and IV into separate byte arrays
      byte[][] result = new byte[2][];
      result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
      if (ivLength > 0)
        result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

      return result;

    } catch (DigestException e) {
      throw new RuntimeException(e);

    } finally {
      // Clean out temporary data
      Arrays.fill(generatedData, (byte)0);
    }
  }

}
