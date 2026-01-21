package com.wildermods.masshash;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.function.Supplier;

import com.wildermods.masshash.utils.ByteUtil;

public class BlobFactory {
	
	public final Provider provider;
	public final Supplier<MessageDigest> digest;
	
	private BlobFactory (Provider provider, Supplier<MessageDigest> digest) {
		this.provider = provider;
		this.digest = digest;
	}
	
	public BlobFactory() {
		this(null, ByteUtil.DEFAULT_DIGEST);
	}
	
	public BlobFactory(Supplier<MessageDigest> digest) {
		this(null, digest);
	}
	
	public BlobFactory(Provider provider) throws NoSuchAlgorithmException {
		this(provider, "SHA-1");
	}
	
	public BlobFactory(Provider provider, String algorithm) throws NoSuchAlgorithmException {
		this(provider, ByteUtil.consume.apply(MessageDigest.getInstance(algorithm, provider)));
	}
	
	public String algorithm() {
		return digest.get().getAlgorithm();
	}
	
	public Blob blob(Supplier<InputStream> stream) {
		try {
			return new Blob(digest, stream, ByteUtil.hash(stream.get(), digest));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public Blob blob(Supplier<InputStream> stream, String hash) {
		final Blob blob = new Blob(digest, stream, hash);
		return blob;
	}
	
	public Blob blob(Path path) throws IOException {
		Supplier<InputStream> streamSupplier = () -> {
			try {
				return Files.newInputStream(path);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
		
		return blob(streamSupplier);
	}
	
	public Blob blob(Path path, String expectedHash) throws IOException {
		Supplier<InputStream> streamSupplier = () -> {
			try {
				return Files.newInputStream(path);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
		
		return blob(streamSupplier, expectedHash);
	}
	
	public Blob blob(byte[] data) {
		Supplier<InputStream> streamSupplier = () -> {
			return new ByteArrayInputStream(data);
		};
		
		return blob(streamSupplier);
	}
	
	public Blob blob(byte[] data, String hash) {
		Supplier<InputStream> streamSupplier = () -> {
			return new ByteArrayInputStream(data);
		};
		
		return blob(streamSupplier, hash);
	}
	
}
