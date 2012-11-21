package org.es.security;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to encode into MD5
 * @author Cyril Leroux
 *
 */
public class Md5 {

	/**
	 * Encode the string into MD5 finger print
	 * @param stringToEncode The string to encode.
	 * @return The encoded string.
	 */
	public static String encode(String stringToEncode) {
		//byte[] uniqueKey = stringToEncode.getBytes();
		byte[] hash      = null;

		try {
			hash = MessageDigest.getInstance("MD5").digest(stringToEncode.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new Error("No MD5 support in this VM.");
		}

		StringBuilder hashString = new StringBuilder();
		for (int i = 0; i < hash.length; i++) {

			String hex = Integer.toHexString(hash[i]);
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