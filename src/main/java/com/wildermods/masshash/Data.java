package com.wildermods.masshash;

import java.util.Arrays;

/**
 * Represents a data object that provides access to raw byte data.
 */
@FunctionalInterface
public interface Data {

	/**
	 * Retrieves the byte data associated with this object.
	 * 
	 * @return the byte array representing the data.
	 */
	public byte[] data();
	
	/**
	 * Checks if the data is transient, meaning the data is null or otherwise unavailable.
	 * 
	 * @return true if the data is transient (null), false otherwise.
	 */
	public default boolean isTransient() {
		return data() == null;
	}
	
	/**
	 * Compares this data object with another byte array to see if they are equal.
	 * 
	 * @param data the byte array to compare.
	 * @return true if the data matches the given byte array, false otherwise.
	 */
	public default boolean dataEquals(byte[] data) {
		return Arrays.equals(data(), data);
	}
	
	/**
	 * Compares this data object with another {@link Data} object to see if their 
	 * byte arrays are equal.
	 * 
	 * @param other the other data object to compare.
	 * @return true if the data matches the other data object, false otherwise.
	 */
	public default boolean dataEquals(Data other) {
		return Arrays.equals(data(), other.data());
	}
}
