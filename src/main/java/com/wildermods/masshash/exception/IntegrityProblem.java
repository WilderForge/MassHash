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
}
