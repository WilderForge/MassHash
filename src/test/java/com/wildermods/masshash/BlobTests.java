package com.wildermods.masshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;

import com.wildermods.masshash.exception.IntegrityException;

public class BlobTests {

	private static final Blob testBlob = new Blob("test".getBytes());
	private static final Blob testBlob2 = new Blob("test".getBytes());
	
	private static final String testHash = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3";
	
	@Test
	public void testBlob() {
		assertEquals(testBlob.hash(), testHash);
		assertEquals(testBlob.toString(), testHash);
	}
	
	@Test
	public void testBlobEquality() {
		assertEquals(testBlob, testBlob);
		assertEquals(testBlob, testBlob2);
		assertEquals(testBlob2, testBlob);
	}
	
	@Test
	public void testDroppedBlobEquality() {
		Hash testBlob2 = testBlob.dropData();
		
		assertEquals(testBlob, testBlob);
		assertEquals(testBlob, testBlob2);
		assertEquals(testBlob2, testBlob);
		assertEquals(testBlob2, testBlob2);
	}
	
	@Test
	public void testDropData() {
		Blob dropped = (Blob) testBlob.dropData();
		assertThrowsExactly(UnsupportedOperationException.class, () -> {dropped.data();});
		assertThrowsExactly(UnsupportedOperationException.class, () -> {dropped.dropData();});
	}
	
	@Test
	public void testVerification() throws IntegrityException {
		testBlob.verify();
		
		Blob dropped = (Blob) testBlob.dropData();
		Blob corrupt = new Blob(testBlob.data(), new Blob("corrupt".getBytes()).hash());
		
		assertThrowsExactly(UnsupportedOperationException.class, () -> dropped.verify());
		assertThrowsExactly(IntegrityException.class, () -> corrupt.verify());
		assertThrowsExactly(IntegrityException.class, () -> new Blob("test".getBytes(), new Blob("corrupt".getBytes())));
	}
	
}
