package com.wildermods.masshash;

import java.io.UncheckedIOException;

import com.wildermods.masshash.exception.IntegrityException;

/**
 * Represents an object that combines both data and its associated hash.
 * It provides methods for retrieving the data, the hash, and for verifying
 * the integrity of the data by comparing its hash.
 */
public interface IBlob extends Data, Hash {

	/**
	 * Verifies that the data's hash matches the expected hash. 
	 * This method should be called to ensure the integrity of the data.
	 * 
	 * @throws IntegrityException if the hash does not match the expected hash,
	 * indicating data corruption or alteration.
	 */
	public void verify() throws IntegrityException;
	
	/**
	 * Returns the full byte array of the blob data.
	 * <p>
	 * Deprecated because reading the entire data into memory may be expensive for large streams.
	 * Prefer {@link #dataStream()} instead.
	 * </p>
	 *
	 * @return the byte array of the blob
	 * @throws UncheckedIOException if reading the stream fails
	 */
	@Override
	@Deprecated(forRemoval = false)
	public byte[] data();
	
}
