package com.wildermods.masshash;

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
	
	/**
	 * Creates a new {@link Hash} instance from the given string hash value.
	 * 
	 * @param hash the string hash value.
	 * @return a new {@link Hash} instance.
	 */
	public static Hash of(String hash) {
		return new Blob((byte[])null, hash);
	}
}