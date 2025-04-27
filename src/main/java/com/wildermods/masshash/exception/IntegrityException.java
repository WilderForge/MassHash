package com.wildermods.masshash.exception;

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

	/**
	 * Default constructor for the IntegrityException.
	 * <p>
	 * This creates a new IntegrityException with no detail message or cause.
	 * </p>
	 */
	public IntegrityException() {
		super();
	}
	
	/**
	 * Constructs an IntegrityException with a specified detail message.
	 * <p>
	 * This creates a new IntegrityException with the specified detail message, 
	 * which provides more information about the cause of the exception.
	 * </p>
	 * 
	 * @param message the detail message that explains the cause of the exception.
	 */
	public IntegrityException(String message) {
		super(message);
	}
	
	/**
	 * Constructs an IntegrityException with a specified cause.
	 * <p>
	 * This creates a new IntegrityException with the specified cause, which is
	 * the underlying reason for the exception.
	 * </p>
	 * 
	 * @param cause the cause of the exception.
	 */
	public IntegrityException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructs an IntegrityException with both a detail message and a cause.
	 * <p>
	 * This creates a new IntegrityException with the specified detail message
	 * and cause, providing both information about the error and the underlying
	 * reason for the exception.
	 * </p>
	 * 
	 * @param message the detail message that explains the cause of the exception.
	 * @param cause the cause of the exception.
	 */
	public IntegrityException(String message, Throwable cause) {
		super(message, cause);
	}
}
