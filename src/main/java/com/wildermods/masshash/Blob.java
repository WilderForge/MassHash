package com.wildermods.masshash;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.wildermods.masshash.exception.IntegrityException;
import com.wildermods.masshash.utils.ByteUtil;

/**
 * Represents a data blob with associated hash. The Blob can store the data as
 * a byte array and its hash, and includes methods for verifying the integrity
 * of the data by checking its hash.
 */
public record Blob(byte[] data, String hash) implements IBlob {

    /**
     * Constructs a Blob from the given data and computes its hash.
     *
     * @param data The byte array representing the data.
     */
    public Blob(byte[] data) {
        this(data, ByteUtil.hash(data));
    }

    /**
     * Constructs a Blob from the given data and hash.
     *
     * @param data The byte array representing the data.
     * @param hash The hash of the data.
     * 
     * @throws IntegrityException if the provided hash does not match the data's hash.
     */
    public Blob(byte[] data, Hash hash) throws IntegrityException {
        this(data, hash.hash());
        verify();
    }

    /**
     * Constructs a Blob from the contents of a file.
     *
     * @param path The path to the file.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public Blob(Path path) throws IOException {
        this(Files.readAllBytes(path));
    }

    /**
     * Constructs a Blob from the contents of a file and verifies its hash.
     *
     * @param path The path to the file.
     * @param hash The expected hash of the file contents.
     * 
     * @throws IOException if an I/O error occurs while reading the file.
     * @throws IntegrityException if the file's hash does not match the provided hash.
     */
    public Blob(Path path, String hash) throws IOException, IntegrityException {
        this(Files.readAllBytes(path), hash);
        verify();
    }

    /**
     * Constructs a Blob from the contents of a file and verifies its hash.
     *
     * @param path The path to the file.
     * @param hash The expected hash of the file contents.
     * 
     * @throws IOException if an I/O error occurs while reading the file.
     * @throws IntegrityException if the file's hash does not match the provided hash.
     */
    public Blob(Path path, Hash hash) throws IOException, IntegrityException {
        this(path, hash.hash());
    }

    /**
     * Constructs a Blob from the data read from an InputStream.
     *
     * @param stream The InputStream from which data is read.
     * @throws IOException if an I/O error occurs while reading from the stream.
     */
    public Blob(InputStream stream) throws IOException {
        this(stream.readAllBytes());
    }

    /**
     * Constructs a Blob from the data read from an InputStream and verifies its hash.
     *
     * @param stream The InputStream from which data is read.
     * @param hash The expected hash of the data.
     * 
     * @throws IOException if an I/O error occurs while reading from the stream.
     * @throws IntegrityException if the data's hash does not match the provided hash.
     */
    public Blob(InputStream stream, String hash) throws IOException, IntegrityException {
        this(stream.readAllBytes(), hash);
        verify();
    }

    /**
     * Constructs a Blob from the data read from an InputStream and verifies its hash.
     *
     * @param stream The InputStream from which data is read.
     * @param hash The expected hash of the data.
     * 
     * @throws IOException if an I/O error occurs while reading from the stream.
     * @throws IntegrityException if the data's hash does not match the provided hash.
     */
    public Blob(InputStream stream, Hash hash) throws IOException, IntegrityException {
        this(stream, hash.hash());
    }
	
    /**
     * Drops the data from the current object and returns a new Hash that represents the hash of this Blob.
     * The original blob still holds the data for as long as you keep it referenced.
     *
     * @return A new {@link Hash} object that represents this blob, but  with no associated data.
     */
	public Hash dropData() {
		if(isTransient()) {
			throw new UnsupportedOperationException("Data already dropped!");
		}
		return new Blob((byte[])null, hash);
	}
	
	/**
	 * Returns the data associated with this Blob
	 * 
	 * @return a byte array that contains the data stored in this blob
	 */
	@Override
	public byte[] data() {
		if(data == null) {
			throw new UnsupportedOperationException("Null data! Was the data dropped?");
		}
		return data;
	}
	
	@Override
	public int hashCode() {
		return hash.hashCode();
	}
	
	/**
	 * Compares this object with another Hash object for equality. All {@link Blob} objects are also instances of {@link Hash}.
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
	 * Verifies that the data stored in this object matches the provided hash.
	 * <p>
	 * This method computes the hash of the current data and compares it to the expected hash. If the hashes do not match,
	 * an {@link IntegrityException} is thrown. This method ensures the integrity of the data.
	 * </p>
	 * 
	 * @throws IntegrityException if the computed hash of the data does not match the expected hash.
	 */
	@Override
	public void verify() throws IntegrityException {
		String dataHash = ByteUtil.hash(data());
		if(!dataHash.equals(hash)) {
			throw new IntegrityException("Expected hash " + hash + " but got " + dataHash);
		}
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
