package com.wildermods.masshash.exception;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Exception thrown when data integrity is violated.
 * <p>
 * This exception is used to indicate issues with the integrity of data, such as
 * when a hash does not match the expected value, or when other integrity checks
 * fail.
 * </p>
 */
@SuppressWarnings("serial")
public class IntegrityException extends Exception {

	private final IntegrityProblem[] problems;
	
	/**
	 * Default constructor for the IntegrityException.
	 * <p>
	 * This creates a new IntegrityException with no detail message or problem.
	 * </p>
	 */
	public IntegrityException() {
		this(null, null, (IntegrityProblem[])null);
	}
	
	/**
	 * Constructs an IntegrityException with a specified detail message.
	 * <p>
	 * This creates a new IntegrityException with the specified detail message, 
	 * which provides more information about the problem of the exception.
	 * </p>
	 * 
	 * @param message the detail message that explains the problem of the exception.
	 */
	public IntegrityException(String message) {
		this(message, null, (IntegrityProblem[])null);
	}
	
	/**
	 * Constructs an IntegrityException from one or more {@link IntegrityProblem}s.
	 * <p>
	 * This constructor is useful when the exception is caused by known integrity
	 * problems and no additional message or cause is needed.
	 * </p>
	 * 
	 * @param problems one or more integrity problems that contributed to the failure.
	 */
	public IntegrityException(IntegrityProblem... problems) {
		this(null, null, problems);
	}
	
	/**
	 * Constructs an IntegrityException with a message and a cause.
	 * 
	 * @param message the detail message explaining the context of the failure.
	 * @param cause the underlying cause of the exception.
	 */
	public IntegrityException(String message, Throwable cause) {
		this(message, cause, (IntegrityProblem[])null);
	}
	
	
	/**
	 * Constructs an IntegrityException from one or more {@link IntegrityProblem}s.
	 * <p>
	 * This constructor is useful when the exception is caused by known integrity
	 * problems and no additional message or cause is needed.
	 * </p>
	 * 
	 * @param message the detail message explaining the context of the failure.
	 * @param problems one or more integrity problems that contributed to the failure.
	 */
	public IntegrityException(String message, IntegrityProblem... problems) {
		this(message, null, problems);
	}
	
	
	/**
	 * Constructs an IntegrityException with a detail message and multiple causes.
	 * <p>
	 * This is useful when several issues are discovered during integrity checking,
	 * and you want to report all of them at once.
	 * </p>
	 * 
	 * @param message the detail message describing the exception.
	 * @param problems the array of underlying problems contributing to the exception.
	 */
	public IntegrityException(String message, Throwable cause, IntegrityProblem... problems) {
		super(getMessages(message, this.problems = problems == null ? new IntegrityProblem[] {} : Arrays.stream(problems).filter(Objects::nonNull).toList().toArray(new IntegrityProblem[] {})), cause);
		
	}
	
	/**
	 * Returns a stream of all {@link IntegrityProblem}s associated with this exception.
	 * <p>
	 * This allows consumers to process or inspect individual problems.
	 * </p>
	 * 
	 * @return a Stream of IntegrityProblem instances.
	 */
	public Stream<IntegrityProblem> getProblems() {
		return Arrays.stream(problems);
	}
	
	/**
	 * Combines a message and an array of {@link IntegrityProblem}s into a single string.
	 * <p>
	 * This helper method is used internally to construct a full message that includes
	 * the provided message and the messages from all contributing problems. It truncates
	 * the output if the number of problems exceeds a threshold.
	 * </p>
	 * 
	 * @param message the base message to include.
	 * @param problems the array of integrity problems to include in the output.
	 * @return a combined message string.
	 */
	protected static String getMessages(String message, IntegrityProblem... problems) {
		StringBuilder ret = new StringBuilder();
		if(message != null) {
			ret.append(message);
		}
		ret.append('\n');
		
		if(!(problems == null)) {
			int i = 0;
			for(IntegrityProblem t : problems) {
				if(t == null) {
					continue;
				}
				ret.append("\t\tProblem - ");
				if(t instanceof Enum) {
					ret.append(((Enum<?>)t).name());
				}
				else {
					ret.append(t.getClass().getSimpleName());
				}
				if(t.getMessage() != null) {
					ret.append(':');
					ret.append(' ');
					ret.append(t.getMessage());
				}
				ret.append('\n');
				if(i >= 30 && problems.length - i >= 0) {
					ret.append("\t\t...And " + (problems.length - i) + " additional problems.");
					break;
				}
				i++;
			}
		}
		
		return ret.toString();
	}

}
