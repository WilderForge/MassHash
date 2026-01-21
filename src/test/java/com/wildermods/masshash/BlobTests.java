package com.wildermods.masshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.function.Supplier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;

import com.wildermods.masshash.exception.IntegrityException;
import com.wildermods.masshash.utils.ByteUtil;

public class BlobTests {

	private static final String testHash = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3";
	
	private static final BlobFactory factory = new BlobFactory();
	
	private static final Blob testBlob = factory.blob("test".getBytes());
	private static final Blob testBlob2 = factory.blob("test".getBytes());
	private static final Blob lightBlob = factory.blob(() -> {
			return new ByteArrayInputStream("test".getBytes());
	});
	
	@Test
	public void testNullConstructors() {
		assertThrowsExactly(NullPointerException.class, () -> factory.blob((byte[])null));
		assertThrowsExactly(NullPointerException.class, () -> factory.blob(new byte[0], (String)null));
		assertThrowsExactly(NullPointerException.class, () -> factory.blob((Supplier)null, ""));
		assertThrowsExactly(NullPointerException.class, () -> factory.blob(() -> {
			try {
				return lightBlob.dataStream();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}, null));
	}
	
	@Test
	public void testBlob() {
		assertEquals(testBlob.hash(), testHash);
		assertEquals(testBlob.toString(), testHash);
		assertEquals(lightBlob.toString(), testHash);
	}
	
	@Test
	public void testBlobEquality() {
		assertEquals(testBlob, testBlob);
		assertEquals(testBlob, testBlob2);
		assertEquals(testBlob2, testBlob);
		assertEquals(lightBlob, testBlob);
		assertEquals(testBlob, lightBlob);
	}
	
	@Test
	public void testDroppedBlobEquality() {
		Hash testBlob2 = testBlob.dropData();
		
		assertEquals(testBlob, testBlob);
		assertEquals(testBlob, testBlob2);
		assertEquals(testBlob2, testBlob);
		assertEquals(testBlob2, testBlob2);
		
		Hash lightBlob2 = lightBlob.dropData();
		assertEquals(testBlob, testBlob);
		assertEquals(testBlob, lightBlob2);
		assertEquals(lightBlob2, testBlob);
		assertEquals(lightBlob2, lightBlob2);
	}
	
	@Test
	public void testVerification() throws IntegrityException {
		testBlob.verify();
		
		IBlob corrupt = factory.blob(testBlob.data(), factory.blob("corrupt".getBytes()).hash());
		IBlob corrupt2 = new Blob(
			() -> {
				try {
					return lightBlob.dataStream();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}, ByteUtil.hash("corrupt".getBytes())
		);
		
		assertThrowsExactly(IntegrityException.class, () -> corrupt.verify());
		assertThrowsExactly(IntegrityException.class, () -> factory.blob("test".getBytes(), factory.blob("corrupt".getBytes()).hash()).verify());
		assertThrowsExactly(IntegrityException.class, () -> corrupt2.verify());
	}
	
}
