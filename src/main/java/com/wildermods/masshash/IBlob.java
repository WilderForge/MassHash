package com.wildermods.masshash;

import java.io.UncheckedIOException;

import com.wildermods.masshash.exception.IntegrityException;
import com.wildermods.masshash.exception.IntegrityProblem;

/**
 * Represents an object that combines both data and its associated hash.
 * It provides methods for retrieving the data, the hash, and for verifying
 * the integrity of the data by comparing its hash.
 */
public interface IBlob extends Data, Hash {

	/**
	 * Verifies that this blob's current data matches its expected hash.
	 * <p>
	 * This method performs the same integrity check as {@link #check()}, but
	 * converts any reported {@link IntegrityProblem} into an
	 * {@link IntegrityException}. If no integrity problem is found, this method
	 * returns normally.
	 * </p>
	 *
	 * @throws IntegrityException if the computed hash of the data does not match
	 *		 the expected hash
	 * @throws UncheckedIOException if the blob's data cannot be read
	 */
	public default void verify() throws IntegrityException {
		IntegrityProblem problem = check();
		if(problem != null) {
			throw new IntegrityException(problem);
		}
	}
	
	/**
	 * Checks whether this blob's current data matches its expected hash.
	 * <p>
	 * The blob's data is read and hashed, and the resulting hash is compared with
	 * the hash represented by this blob. Unlike {@link #verify()}, an integrity
	 * mismatch is reported by returning an {@link IntegrityProblem} rather than by
	 * throwing an {@link IntegrityException}.
	 * </p>
	 * <p>
	 * Returning {@code null} indicates that the data passed the integrity check.
	 * A non-null result describes the detected integrity problem.
	 * </p>
	 *
	 * @return an {@link IntegrityProblem} describing the hash mismatch, or
	 *		 {@code null} if the data matches the expected hash
	 */
	public IntegrityProblem check();
	
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
