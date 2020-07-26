package com.workingbit.digestauthwithjwt.util;

import org.springframework.security.crypto.codec.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {

  public static String md5Hex(String data) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("No MD5 algorithm available!");
    }

    return new String(Hex.encode(digest.digest(data.getBytes())));
  }

}
