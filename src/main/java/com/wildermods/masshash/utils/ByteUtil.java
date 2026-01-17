package com.wildermods.masshash.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class ByteUtil {

	/**
	 * Hashes a given byte array using the SHA-1 algorithm.
	 * <p>
	 * This method takes the provided byte array, hashes it using the SHA-1 algorithm,
	 * and returns the resulting hash as a hexadecimal string.
	 * </p>
	 * 
	 * @param bytes the byte array to hash.
	 * 
	 * @return a hexadecimal string representing the hash of the byte array.
	 * 
	 * @throws NullPointerException if the provided byte array is null.
	 */
	public static String hash(byte[] bytes) {
		Objects.requireNonNull(bytes, "Input byte array cannot be null.");
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			return bytesToHex(digest.digest(bytes));
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("SHA-1 algorithm is unavailable.", e);
		}
	}
	
	/**
	 * Hashes the contents of an InputStream using SHA-1 without loading all bytes into memory.
	 * The stream is read sequentially in 1 MiB chunks.
	 *
	 * @param stream the InputStream to hash
	 * @return the hexadecimal SHA-1 hash
	 * @throws IOException if an I/O error occurs reading the stream
	 * @throws NullPointerException if the stream is null
	 */
	public static String hash(InputStream stream) throws IOException {
		Objects.requireNonNull(stream, "InputStream cannot be null.");
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] buffer = new byte[1048576]; // 1 MiB buffer
			int bytesRead;
			while ((bytesRead = stream.read(buffer)) != -1) {
				digest.update(buffer, 0, bytesRead);
			}
			return bytesToHex(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("SHA-1 algorithm is unavailable.", e);
		}
	}
	
	/**
	 * Converts a byte array into a hexadecimal string representation.
	 * <p>
	 * This method iterates through the provided byte array, formatting each byte
	 * as a two-character hexadecimal string, and appends it to form a complete
	 * hexadecimal representation of the byte array.
	 * </p>
	 * 
	 * @param bytes the byte array to convert to hex.
	 * 
	 * @return a string representing the byte array in hexadecimal format.
	 * 
	 * @throws NullPointerException if the provided byte array is null.
	 */
	private static String bytesToHex(byte[] bytes) {
		Objects.requireNonNull(bytes, "Input byte array cannot be null.");
		StringBuilder hex = new StringBuilder();
		for(byte b : bytes) {
			hex.append(String.format("%02x", b));
		}
		return hex.toString();
	}
}