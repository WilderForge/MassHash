package com.wildermods.masshash.exception;

import com.wildermods.masshash.Hash;

/**
 * Represents a type of integrity verification issue.
 * Implementations can define custom messages for reporting problems.
 */
public interface IntegrityProblem {
    /**
     * @return A short description of the integrity issue.
     */
    public String getMessage();

    /**
     * Formats a full message for a specific hash.
     *
     * @param hash The hash associated with the integrity problem.
     * @return A full descriptive message including the hash.
     */
    public default String getMessageFor(Hash hash) {
        return getMessage() + ": (" + hash.hash() + ")";
    }
    
	/**
	 * Converts a {@link Throwable} into an {@link IntegrityProblem}.
	 * <p>
	 * If the provided throwable is an instance of {@link IntegrityException}, this method
	 * delegates to {@link IntegrityException#toProblem()} to preserve any integrity-specific context.
	 * Otherwise, it creates a generic {@code IntegrityProblem} that uses the throwable's
	 * message as its description. If the message is {@code null} or blank, the throwable's
	 * class name is used instead.
	 * </p>
	 *
	 * @param t the throwable to convert into an {@code IntegrityProblem}
	 * @return an {@code IntegrityProblem} representing the throwable
	 */
	public static IntegrityProblem fromThrown(Throwable t) {
		if(t instanceof IntegrityException) {
			return ((IntegrityException) t).toProblem();
		}
		return () -> {
			String message = t.getMessage();
			return message == null || message.isBlank() ? t.getClass().getName() : message;
		};
	}
}
