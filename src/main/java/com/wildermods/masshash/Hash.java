package com.wildermods.masshash;

import java.security.MessageDigest;
import java.util.function.Supplier;

import com.wildermods.masshash.utils.ByteUtil;

/**
 * A functional interface representing a hash. It provides methods for obtaining 
 * the hash value as a string and for comparing it to other hashes.
 */
@FunctionalInterface
public interface Hash {

	/**
	 * Returns the hash value as a string.
	 * 
	 * @return the hash value.
	 */
	public String hash();
	
	/**
	 * @return The hashing algorithm used.
	 */
	public default String algorithm() {
		return digest().get().getAlgorithm();
	}
	
	/**
	 * The messageDigest that is used to obtain the
	 * algorithm
	 */
	public default Supplier<MessageDigest> digest() {
		return ByteUtil.DEFAULT_DIGEST;
	}
	
	/**
	 * Compares this hash to another hash and returns true if they are equal.
	 * 
	 * @param other the other hash to compare to.
	 * @return true if the two hashes are equal, false otherwise.
	 */
	public default boolean hashEquals(Hash other) {
		return hash().equals(other.hash());
	}
	
	/**
	 * Compares this hash to a string hash value and returns true if they are equal.
	 * 
	 * @param hash the string hash value to compare to.
	 * @return true if the hash matches the given string, false otherwise.
	 */
	public default boolean hashEquals(String hash) {
		return hash().equals(hash);
	}
	
	public static class Internal {
		private static record Impl(String hash, Supplier<MessageDigest> digest) implements Hash {
			
			private Impl(String hash, Supplier<MessageDigest> digest) {
				this.hash = hash;
				final MessageDigest d = digest.get();
				this.digest = () -> d;
			}
			
			@Override
			public int hashCode() {
				return hash.hashCode();
			}
			
			/**
			 * Compares this object with another Hash object for equality. All {@link IBlob} objects are also instances of {@link Hash}.
			 * <p>
			 * Two {@link Hash} objects are considered equal if their hashes are the same. This method specifically compares
			 * the hash of the other object with the hash of this object. If the other object is not an instance of {@link Hash},
			 * the method returns {@code false}.
			 * </p>
			 * 
			 * @param o the object to compare with this Hash object.
			 * @return {@code true} if the other object is a {@link Hash} and has the same hash; {@code false} otherwise.
			 */
			@Override
			public boolean equals(Object o) {
				if(o instanceof Hash) {
					return hash().equals(((Hash) o).hash());
				}
				return false;
			}
			
			/**
			 * Returns a string representation of this Blob, which is its hash value.
			 * <p>
			 * This method overrides the default {@link Object#toString()} method to provide a more meaningful
			 * string representation of the Blob.
			 * </p>
			 * 
			 * @return the hash of the Blob as a string.
			 */
			@Override
			public String toString() {
				return hash();
			}
			
		}
	}
	
	/**
	 * Creates a new {@link Hash} instance from the given string hash value.
	 * 
	 * @param hash the string hash value.
	 * @return a new {@link Hash} instance.
	 */
	public static Hash of(String hash) {
		return new Internal.Impl(hash, ByteUtil.DEFAULT_DIGEST);
	}
	
	/**
	 * Creates a new {@link Hash} instance from the given string and digest values.
	 * 
	 * @param hash the string hash value.
	 * @return a new {@link Hash} instance.
	 */
	public static Hash of(Supplier<MessageDigest> digest, String hash) {
		return new Internal.Impl(hash, digest);
	}
}