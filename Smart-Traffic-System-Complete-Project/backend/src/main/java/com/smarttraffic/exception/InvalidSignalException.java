package com.smarttraffic.exception;

/**
 * EXCEPTION HANDLING: Demonstrates OOP Concept 7.
 * Thrown when an unsafe or conflicting signal state is requested.
 */
public class InvalidSignalException extends Exception {
    private String intersectionId;
    private String attemptedState;

    public InvalidSignalException(String message, String intersectionId, String attemptedState) {
        super(String.format("Invalid Signal Operation at [%s]: %s (Attempted: %s)", intersectionId, message, attemptedState));
        this.intersectionId = intersectionId;
        this.attemptedState = attemptedState;
    }

    public String getIntersectionId() {
        return intersectionId;
    }

    public String getAttemptedState() {
        return attemptedState;
    }
}
