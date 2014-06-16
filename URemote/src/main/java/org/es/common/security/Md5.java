package org.es.common.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to encode into MD5
 *
 * @author Cyril Leroux
 *         Created on 11/11/12.
 */
public class Md5 {

    /**
     * Encode the string into MD5 finger print
     *
     * @param stringToEncode The string to encode.
     * @return The encoded string.
     */
    public static String encode(String stringToEncode) {
        byte[] hash = null;

        try {
            hash = MessageDigest.getInstance("MD5").digest(stringToEncode.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new Error("No MD5 support in this VM.");
        }

        StringBuilder hashString = new StringBuilder();
        for (byte aHash : hash) {

            String hex = Integer.toHexString(aHash);
            if (hex.length() == 1) {
                hashString.append('0');
                hashString.append(hex.charAt(hex.length() - 1));
            } else {
                hashString.append(hex.substring(hex.length() - 2));
            }
        }
        return hashString.toString();
    }
}