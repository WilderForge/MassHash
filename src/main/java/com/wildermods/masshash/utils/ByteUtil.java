package com.wildermods.masshash.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility methods for hashing byte data and streams using cryptographic hash functions.
 * <p>
 * This class provides convenience overloads for hashing byte arrays and {@link InputStream}s,
 * supporting explicit algorithms and security providers. All hashes are returned as lowercase
 * hexadecimal strings.
 * </p>
 */
public class ByteUtil {

	public static final Supplier<MessageDigest> DEFAULT_DIGEST = () -> {
		try {
			return MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	};
	
	public static final Function<MessageDigest, Supplier<MessageDigest>> consume = (p) -> {
		return () -> p;
	};
	
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
		return hash(bytes, DEFAULT_DIGEST);
	}
	
	/**
	 * Hashes a given byte array using the specified algorithm.
	 * This method takes the provided byte array, hashes it using the provided algorithm,
	 * and returns the resulting hash as a hexadecimal string.
	 * 
	 * @param bytes the byte array to hash
	 * @param algorithm the name of the hash algorithm
	 * 
	 * @return a hexadecimal string representing the hash
	 * 
	 * @throws NoSuchAlgorithmException if the algorithm is not available
	 * @throws NullPointerException if {@code bytes} or {@code algorithm} is null
	 */
	public static String hash(byte[] bytes, String algorithm) throws NoSuchAlgorithmException {
		Objects.requireNonNull(algorithm, "algorithm cannot be null");
		return hash(bytes, consume.apply(MessageDigest.getInstance(algorithm)));
	}
	
	/**
	 * Hashes a given byte array using the specified algorithm and security provider.
	 * This method takes the provided byte array, hashes it using the provided algorithm,
	 * and returns the resulting hash as a hexadecimal string.
	 * 
	 * @param bytes the byte array to hash
	 * @param algorithm the name of the hash algorithm
	 * @param provider the security provider to use
	 * 
	 * @return a hexadecimal string representing the hash
	 * 
	 * @throws NoSuchAlgorithmException if the algorithm is not available from the provider
	 * @throws NullPointerException if any argument is null
	 */
	public static String hash(byte[] bytes, String algorithm, Provider provider) throws NoSuchAlgorithmException {
		Objects.requireNonNull(algorithm, "Algorithm cannot be null");
		Objects.requireNonNull(provider, "Provider cannot be null");
		return hash(bytes, consume.apply(MessageDigest.getInstance(algorithm, provider)));
	}
	
	/**
	 * Hashes a given byte array using a {@link MessageDigest} supplied by the caller.
	 * <p>
	 * The provided {@code Callable} is invoked exactly once to obtain a fresh
	 * {@link MessageDigest} instance. The digest is then used to hash the entire
	 * byte array in a single operation.
	 * </p>
	 *
	 * @param bytes the byte array to hash
	 * @param digest a callable that supplies a {@link MessageDigest} instance
	 *
	 * @return a hexadecimal string representing the hash of the byte array
	 *
	 * @throws NullPointerException if {@code bytes}, {@code digest}, or the returned
	 *                              {@link MessageDigest} is {@code null}
	 * @throws RuntimeException if the callable throws any other checked exception. The
	 *                              thrown checked exception is the cause.
	 */
	public static String hash(byte[] bytes, Supplier<MessageDigest> digest) {
		Objects.requireNonNull(bytes, "bytes cannot be null");
		Objects.requireNonNull(digest, "digest supplier cannot be null");
		
		MessageDigest d;
		Objects.requireNonNull(d = digest.get(), "caller provided null MessageDigest");
		return bytesToHex(d.digest(bytes));
	}
	
	/**
	 * Hashes the contents of an {@link InputStream} using the SHA-1 algorithm.
	 * <p>
	 * The stream is read sequentially in fixed-size chunks and is not buffered internally
	 * beyond the chunk size. This method does not close the stream.
	 * </p>
	 *
	 * @param stream the input stream to hash
	 * 
	 * @return a hexadecimal string representing the SHA-1 hash
	 * 
	 * @throws IOException if an I/O error occurs while reading the stream
	 * @throws NullPointerException if {@code stream} is null
	 */
	public static String hash(InputStream stream) throws IOException {
		return hash(stream, DEFAULT_DIGEST);
	}
	
	/**
	 * Hashes the contents of an {@link InputStream} using the specified algorithm.
	 * <p>
	 * The stream is consumed by this operation and is not closed.
	 * </p>
	 *
	 * @param stream the input stream to hash
	 * @param algorithm the name of the hash algorithm
	 * 
	 * @return a hexadecimal string representing the hash
	 * 
	 * @throws IOException if an I/O error occurs while reading the stream
	 * @throws NoSuchAlgorithmException if the algorithm is not available
	 * @throws NullPointerException if {@code stream} or {@code algorithm} is null
	 */
	public static String hash(InputStream stream, String algorithm) throws NoSuchAlgorithmException, IOException {
		Objects.requireNonNull(algorithm, "algorithm cannot be null");
		return hash(stream, consume.apply(MessageDigest.getInstance(algorithm)));
	}
	
	/**
	 * Hashes the contents of an {@link InputStream} using the specified algorithm
	 * and security provider.
	 * 
	 * <p>
	 * The stream is consumed by this operation and is not closed.
	 * </p>
	 *
	 * @param stream the input stream to hash
	 * @param algorithm the name of the hash algorithm
	 * @param provider the security provider to use
	 * 
	 * @return a hexadecimal string representing the hash
	 * 
	 * @throws IOException if an I/O error occurs while reading the stream
	 * @throws NoSuchAlgorithmException if the algorithm is not available from the provider
	 * @throws NullPointerException if any argument is null
	 */
	public static String hash(InputStream stream, String algorithm, Provider provider) throws NoSuchAlgorithmException, IOException {
		Objects.requireNonNull(algorithm, "algorithm cannot be null");
		Objects.requireNonNull(provider, "provider cannot be null");
		return hash(stream, consume.apply(MessageDigest.getInstance(algorithm, provider)));
	}
	
	/**
	 * Hashes the contents of an {@link InputStream} using a {@link MessageDigest}
	 * supplied by the caller.
	 * <p>
	 * The provided {@code Callable} is invoked exactly once to obtain a fresh
	 * {@link MessageDigest} instance. The stream is read sequentially in 1 MiB sized
	 * chunks and fed into the digest.
	 * </p>
	 * <p>
	 * The stream is consumed by this operation and is not closed.
	 * </p>
	 *
	 * @param stream the input stream to hash
	 * @param digest a callable that supplies a {@link MessageDigest} instance
	 *
	 * @return a hexadecimal string representing the hash of the stream contents
	 *
	 * @throws IOException if an I/O error occurs while reading the stream
	 * @throws NoSuchAlgorithmException if the callable throws this exception
	 * @throws NullPointerException if {@code stream}, {@code digest}, or the returned
	 *                              {@link MessageDigest} is {@code null}
	 * @throws RuntimeException if the callable throws any other checked exception. The
	 *                              thrown checked exception is the cause.
	 */
	public static String hash(InputStream stream, Supplier<MessageDigest> digest) throws IOException {
		MessageDigest d;
		Objects.requireNonNull(stream, "InputStream cannot be null");
		Objects.requireNonNull(digest, "MessageDigest cannot be null");
		Objects.requireNonNull(d = digest.get(), "caller provided null MessageDigest");
		
		byte[] buffer = new byte[1048576]; // 1 MiB buffer
		int bytesRead;
		while ((bytesRead = stream.read(buffer)) != -1) {
			d.update(buffer, 0, bytesRead);
		}
		return bytesToHex(d.digest());
	}
	
	private static final char[] HEX = "0123456789abcdef".toCharArray();

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
		char[] out = new char[bytes.length * 2];
		for (int i = 0, j = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			out[j++] = HEX[v >>> 4];
			out[j++] = HEX[v & 0x0F];
		}
		return new String(out);
	}
}