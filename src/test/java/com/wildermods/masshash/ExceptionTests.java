package com.wildermods.masshash;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.wildermods.masshash.exception.IntegrityException;
import com.wildermods.masshash.exception.IntegrityProblem;

public class ExceptionTests {

	@Test
	public void testConstructor1() {
		IntegrityException e = new IntegrityException();
		assertNull(e.getCause());
		assertNotNull(e.getProblems());
		assertTrue(e.getProblems().toList().size() == 0);
	}
	
	@Test
	public void testConstructor2() {
		IntegrityException e = new IntegrityException("Message");
		assertNull(e.getCause());
		assertNotNull(e.getProblems());
		assertTrue(e.getProblems().toList().size() == 0);
	}
	
	@Test
	public void testConstructor3() {
		
		IntegrityProblem t1 = () -> "ONE";
		IntegrityProblem t2 = () -> "TWO";
		
		IntegrityException e = new IntegrityException(t1, t2);
		
		assertEquals(e.getProblems().toList().get(0), t1);
		assertEquals(e.getProblems().toList().get(1), t2);
		assertTrue(e.getMessage().contains(t1.getMessage()));
		assertTrue(e.getMessage().contains(t2.getMessage()));
	}

	
	@Test
	public void testConstructor6_NullValue() {
		final String msg = "WWWWW";
		IntegrityProblem t = null;
		IntegrityProblem t1 = () -> "T1";
		IntegrityProblem t2 = () -> "T2";
		IntegrityProblem t3 = null;
		IntegrityProblem t4 = () -> ("T4");
		IntegrityException e = new IntegrityException(msg, t, t1, t2, t3, t4);
		
		e.printStackTrace();
		
		assertNotNull(e.getMessage());
		assertTrue(e.getProblems().toList().size() == 3);
		assertEquals(e.getProblems().toList().get(0), t1);
		assertEquals(e.getProblems().toList().get(1), t2);
		assertEquals(e.getProblems().toList().get(2), t4);

		assertTrue(e.getMessage().contains(t1.getMessage()));
		assertTrue(e.getMessage().contains(t2.getMessage()));
		assertTrue(e.getMessage().contains(t4.getMessage()));
		assertTrue(e.getMessage().contains(msg));
	}
	
	@Test
	public void testConstructor6_LotsOfValues() {
		
		String msg = "LotsOfProblems";
		IntegrityException e;
		{
			Random random = new Random();
			IntegrityProblem[] problems = new IntegrityProblem[random.nextInt(1000, 3000)];
			
			for(int i = 0; i < problems.length; i++) {
				if(i % 4 == 0) {
					problems[i] = null;
				}
				else {
					problems[i] = () -> UUID.randomUUID().toString();
				}	
			}
			e = new IntegrityException(msg, problems);
		}
		
		e.printStackTrace();
		
		assertNotNull(e.getMessage());
		{
			List<IntegrityProblem> problems = e.getProblems().toList();
			for(int i = 0; i < Math.min(problems.size(), 30); i++) {
				IntegrityProblem problem = problems.get(i);
				assertNotNull(problem);
			}
			assertTrue(e.getMessage().contains(msg));
			e.printStackTrace();
			//System.out.println("...And " + (problems.size() - 30) + " additional problems.");
			assertTrue(e.getMessage().contains("...And " + (problems.size() - 30) + " additional problems."));
		}
		
	}
	
}
