package com.wildermods.masshash.exception;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

	private final Throwable[] problems;
	
	/**
	 * Default constructor for the IntegrityException.
	 * <p>
	 * This creates a new IntegrityException with no detail message or problem.
	 * </p>
	 */
	public IntegrityException() {
		super();
		problems = new Throwable[]{};
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
		super(message);
		problems = new Throwable[] {};
	}
	
	/**
	 * Constructs an IntegrityException with a specified cause.
	 * <p>
	 * This creates a new IntegrityException with the specified cause, which is
	 * the underlying reason for the exception.
	 * </p>
	 * 
	 * @param problem the cause of the exception.
	 */
	public IntegrityException(Throwable cause) {
		super(cause);
		if(cause != null) {
			problems = new Throwable[] {cause};
		}
		else {
			problems = new Throwable[] {};
		}
	}
	
	/**
	 * Constructs an IntegrityException with multiple causes.
	 * <p>
	 * This constructor is used when multiple underlying problems contribute to
	 * the integrity failure. The first problem in the array is used as the primary
	 * cause, and their messages are combined into the exception message.
	 * </p>
	 *
	 * @param problems the array of causes contributing to this exception.
	 */
	public IntegrityException(Throwable... problems) {
		super(getMessages("", problems), problems == null ? null : (Arrays.stream(problems).filter(Objects::nonNull).findFirst().orElse(null)));
		this.problems = problems != null ? problems : new Throwable[]{};
	}
	
	/**
	 * Constructs an IntegrityException with both a detail message and a problem.
	 * <p>
	 * This creates a new IntegrityException with the specified detail message
	 * and cause, providing both information about the error and the underlying
	 * reason for the exception.
	 * </p>
	 * 
	 * @param message the detail message that explains the cause of the exception.
	 * @param problem the cause of the exception.
	 */
	public IntegrityException(String message, Throwable cause) {
		super(getMessages(message, cause), cause);
		if(cause != null) {
			this.problems = new Throwable[] {cause};
		}
		else {
			this.problems = new Throwable[] {};
		}
	}
	
	/**
	 * Constructs an IntegrityException with a detail message and multiple causes.
	 * <p>
	 * This is useful when several issues are discovered during integrity checking,
	 * and you want to report all of them at once. The first cause in the array
	 * is treated as the primary cause.
	 * </p>
	 * 
	 * @param message the detail message describing the exception.
	 * @param problems the array of underlying causes contributing to the exception.
	 */
	public IntegrityException(String message, Throwable... problems) {
		super(getMessages(message, problems), problems == null ? null : (Arrays.stream(problems).filter(Objects::nonNull).findFirst().orElse(null)));
		this.problems = problems != null ? problems : new Throwable[]{};
	}
	
	public List<Throwable> getProblems() {
		return Arrays.stream(problems).filter(Objects::nonNull).toList();
	}
	
	/**
	 * Combines a message and an array of throwable causes into a single string.
	 * <p>
	 * This helper method is used internally to construct a full message that includes
	 * the provided message and the messages from all contributing problems.
	 * </p>
	 * 
	 * @param message the base message to include.
	 * @param throwables the array of throwable causes to append.
	 * @return a combined message string.
	 */
	protected static String getMessages(String message, Throwable... throwables) {
		StringBuilder ret = new StringBuilder();
		if(message != null) {
			ret.append(message);
		}
		ret.append('\n');
		
		if(!(throwables == null)) {
			int i = 0;
			for(Throwable t : throwables) {
				if(t == null) {
					continue;
				}
				ret.append("\t\tProblem - ");
				ret.append(t.getClass().getName());
				if(t.getMessage() != null) {
					ret.append(':');
					ret.append(' ');
					ret.append(t.getMessage());
				}
				ret.append('\n');
				if(i >= 30 && throwables.length - i >= 0) {
					ret.append("\t\t...And " + (throwables.length - i) + " additional problems.");
					break;
				}
				i++;
			}
		}
		
		return ret.toString();
	}
}
