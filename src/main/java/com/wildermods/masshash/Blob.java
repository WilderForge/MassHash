package com.wildermods.masshash;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.function.Supplier;

import com.wildermods.masshash.exception.IntegrityException;
import com.wildermods.masshash.utils.ByteUtil;

/**
 * A lightweight implementation of {@link IBlob} that represents data which can be read
 * as a stream. Does not necessarily store the full byte array in memory.
 * <p>
 * This is particularly useful for large files or streams (e.g., files on disk, network streams),
 * where reading the entire content into memory is undesirable. The hash of the data is always stored
 * and can be verified without retaining the raw bytes.
 * </p>
 */
public final class Blob implements IBlob {

	private final Supplier<MessageDigest> digest;
	private final Supplier<InputStream> streamSupplier;
	private final String hash;
	
	Blob (Supplier<MessageDigest> digest, Supplier<InputStream> streamSupplier, String hash) {
		Objects.requireNonNull(digest);
		Objects.requireNonNull(streamSupplier);
		Objects.requireNonNull(streamSupplier.get());
		Objects.requireNonNull(hash);
		this.digest = digest;
		this.streamSupplier = streamSupplier;
		this.hash = hash;
	}
	
	Blob(Supplier<InputStream> streamSupplier, String hash) {
		this(ByteUtil.DEFAULT_DIGEST, streamSupplier, hash);
	}
	
	public Supplier<MessageDigest> digest() {
		return digest;
	}
	
	public String hash() {
		return hash;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public byte[] data() {
		try (InputStream stream = dataStream()){
			return stream.readAllBytes();
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Checks if this blob is transient, meaning the underlying stream cannot be opened.
	 * <p>
	 * This can occur if the file is deleted, the network stream fails, or any other I/O error
	 * prevents the stream from being accessed.
	 * </p>
	 *
	 * @return {@code true} if the data stream cannot be opened, {@code false} otherwise
	 */
	@Override
	public boolean isTransient() {
		try (InputStream stream = streamSupplier.get()){
			return false;
		}
		catch(Exception e) {
			return true;
		}
	}

	/**
	 * Returns a fresh {@link InputStream} for reading the blob's data.
	 * <p>
	 * Each call returns a new stream. The caller is responsible for closing it.
	 * </p>
	 *
	 * @return a fresh {@link InputStream} for reading the blob's contents
	 * @throws IOException if the stream cannot be opened
	 */
	@Override
	public InputStream dataStream() throws IOException {
		try {
			return streamSupplier.get();
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Verifies that the data matches the provided hash.
	 * <p>
	 * This method computes the hash of the current data and compares it to the expected hash. If the hashes do not match,
	 * an {@link IntegrityException} is thrown. This method ensures the integrity of the data.
	 * </p>
	 * 
	 * @throws IntegrityException if the computed hash of the data does not match the expected hash.
	 */
	@Override
	public void verify() throws IntegrityException {
		try (InputStream stream = dataStream()){
			String actualHash = ByteUtil.hash(stream);
			if(!actualHash.equals(hash)) {
				throw new IntegrityException("Expected hash " + hash + " but got " + actualHash);
			}
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
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
