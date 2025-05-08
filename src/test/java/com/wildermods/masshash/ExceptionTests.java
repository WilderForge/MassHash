package com.wildermods.masshash;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.wildermods.masshash.exception.IntegrityException;

public class ExceptionTests {

	@Test
	public void testConstructor1() {
		IntegrityException e = new IntegrityException();
		assertNull(e.getCause());
		assertNotNull(e.getProblems());
		assertTrue(e.getProblems().size() == 0);
	}
	
	@Test
	public void testConstructor2() {
		IntegrityException e = new IntegrityException("Message");
		assertNull(e.getCause());
		assertNotNull(e.getProblems());
		assertTrue(e.getProblems().size() == 0);
	}
	
	@Test
	public void testConstructor3() {
		Throwable t = new Throwable("THROWABLE");
		IntegrityException e = new IntegrityException(t);
		
		assertEquals(e.getCause(), t);
		assertEquals(e.getProblems().get(0), t);
		assertTrue(e.getProblems().size() == 1);
		assertTrue(e.getMessage().contains(t.getMessage()));
	}
	
	@Test
	public void testConstructor3_Null() {
		Throwable t = null;
		IntegrityException e = new IntegrityException(t);
		
		assertNull(e.getCause());
		assertNotNull(e.getProblems());
		assertTrue(e.getProblems().size() == 0);
	}
	
	@Test
	public void testConstructor4() {
		
		Error error = new Error();
		NullPointerException n = new NullPointerException("WWWWWW");
		
		IntegrityException e = new IntegrityException(error, n);
		
		assertEquals(e.getProblems().get(0), error);
		assertEquals(e.getProblems().get(1), n);
		assertTrue(e.getMessage().contains(n.getMessage()));
	}
	
	@Test
	public void testConstructor5() {
		final String msg = "WWWWW";
		Throwable t = new Throwable("THROWABLE");
		IntegrityException e = new IntegrityException(msg, t);
		
		e.printStackTrace();
		
		assertNotNull(e.getCause());
		assertNotNull(e.getMessage());
		assertEquals(e.getCause(), t);
		assertEquals(e.getProblems().get(0), t);
		assertTrue(e.getProblems().size() == 1);
		assertTrue(e.getMessage().contains(t.getMessage()));
		assertTrue(e.getMessage().contains(msg));
	}
	
	@Test
	public void testConstructor5_Null() {
		final String msg = "WWWWW";
		IntegrityException e = new IntegrityException(msg, (Throwable)null);
		
		e.printStackTrace();
		
		assertNull(e.getCause());
		assertNotNull(e.getMessage());
		assertTrue(e.getProblems().size() == 0);
		assertTrue(e.getMessage().contains(msg));
	}
	
	@Test
	public void testConstructor6() {
		final String msg = "WWWWW";
		Throwable t = new Throwable("THROWABLE");
		Throwable t2 = new RuntimeException("RUNTIME_EXCEPTION");
		IntegrityException e = new IntegrityException(msg, t, t2);
		
		e.printStackTrace();
		
		assertNotNull(e.getCause());
		assertNotNull(e.getMessage());
		assertEquals(e.getCause(), t);
		assertEquals(e.getProblems().get(0), t);
		assertEquals(e.getProblems().get(1), t2);
		assertTrue(e.getProblems().size() == 2);
		assertTrue(e.getMessage().contains(t.getMessage()));
		assertTrue(e.getMessage().contains(t2.getMessage()));
		assertTrue(e.getMessage().contains(msg));
	}
	
	@Test
	public void testConstructor6_Null() {
		final String msg = "WWWWW";
		IntegrityException e = new IntegrityException(msg, (Throwable[])null);
		
		e.printStackTrace();
		
		assertNull(e.getCause());
		assertNotNull(e.getMessage());
		assertTrue(e.getProblems().size() == 0);
		assertTrue(e.getMessage().contains(msg));
	}
	
	@Test
	public void testConstructor6_NullValue() {
		final String msg = "WWWWW";
		Throwable t = null;
		Throwable t1 = new Throwable("THROWABLE");
		Throwable t2 = new RuntimeException("RUNTIME_EXCEPTION");
		Throwable t3 = null;
		Throwable t4 = new AssertionError("ASSERTION_ERROR");
		IntegrityException e = new IntegrityException(msg, t, t1, t2, t3, t4);
		
		e.printStackTrace();
		
		assertNotNull(e.getCause());
		assertNotNull(e.getMessage());
		assertEquals(e.getCause(), t1);
		assertEquals(e.getProblems().get(0), t1);
		assertEquals(e.getProblems().get(1), t2);
		assertEquals(e.getProblems().get(2), t4);
		assertTrue(e.getProblems().size() == 3);
		assertTrue(e.getMessage().contains(t1.getMessage()));
		assertTrue(e.getMessage().contains(t2.getMessage()));
		assertTrue(e.getMessage().contains(t4.getMessage()));
		assertTrue(e.getMessage().contains(msg));
	}
	
	@Test
	public void testConstructor6_LotsOfValues() {
		final String msg = "WWWWW";
		
		Throwable[] throwables = new Throwable[1000];
		
		int nulls = 0;
		Random random = new Random();
		for(int i = 0; i < throwables.length; i++) {
			String message;
			switch(random.nextInt(4)) {
				case 0:
					nulls++;
					throwables[i] = null;
					break;
				case 1:
					throwables[i] = new Throwable(UUID.randomUUID().toString());
					break;
				case 2:
					throwables[i] = new Exception(UUID.randomUUID().toString());
					break;
				case 3:
					throwables[i] = new RuntimeException(UUID.randomUUID().toString());
			}
		}
		IntegrityException e = new IntegrityException(msg, throwables);
		
		e.printStackTrace();
		
		assertNotNull(e.getCause());
		assertNotNull(e.getMessage());
		
		List<Throwable> problems = e.getProblems();
		for(int i = 0; i < Math.min(problems.size(), 30); i++) {
			Throwable problem = problems.get(i);
			assertNotNull(problem);
			assertTrue(e.getMessage().contains(problem.getMessage()));
		}
		assertTrue(e.getMessage().contains(msg));
		e.printStackTrace();
		System.out.println("...And " + (throwables.length - 30) + " additional problems.");
		assertTrue(e.getMessage().contains("...And " + (throwables.length - 30) + " additional problems."));
		
	}
	
}
