package com.wildermods.masshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.function.Supplier;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import com.wildermods.masshash.exception.IntegrityException;
import com.wildermods.masshash.utils.ByteUtil;

public class BlobTests {

	private static final String testHash = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3";
	
	private static final Blob testBlob = new Blob("test".getBytes());
	private static final Blob testBlob2 = new Blob("test".getBytes());
	private static final LightBlob lightBlob = new LightBlob(
		(Supplier<InputStream>)() -> {
			return new ByteArrayInputStream(
				"test".getBytes()
			);}, testHash
		);
	
	@Test
	public void testNullConstructors() {
		assertThrowsExactly(NullPointerException.class, () -> new Blob((byte[])null));
		assertThrowsExactly(NullPointerException.class, () -> new Blob(new byte[0], (String)null));
		assertThrowsExactly(NullPointerException.class, () -> new LightBlob(null, ""));
		assertThrowsExactly(NullPointerException.class, () -> new LightBlob(lightBlob.streamSupplier(), null));
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
		
		IBlob corrupt = new Blob(testBlob.data(), new Blob("corrupt".getBytes()).hash());
		IBlob corrupt2 = new LightBlob(
			lightBlob.streamSupplier(), ByteUtil.hash("corrupt".getBytes())
		);
		
		assertThrowsExactly(IntegrityException.class, () -> corrupt.verify());
		assertThrowsExactly(IntegrityException.class, () -> new Blob("test".getBytes(), new Blob("corrupt".getBytes())));
		assertThrowsExactly(IntegrityException.class, () -> corrupt2.verify());
	}
	
}
