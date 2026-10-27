package com.smarttraffic.exception;

/**
 * EXCEPTION HANDLING: Demonstrates OOP Concept 7.
 * Thrown when intersection queue capacity exceeds safety design limits.
 */
public class TrafficOverflowException extends RuntimeException {
    private String intersectionId;
    private int currentVehicles;
    private int maxCapacity;

    public TrafficOverflowException(String intersectionId, int currentVehicles, int maxCapacity) {
        super(String.format("Critical Traffic Overflow at node [%s]: %d queued vehicles exceeds maximum safety capacity of %d!",
                intersectionId, currentVehicles, maxCapacity));
        this.intersectionId = intersectionId;
        this.currentVehicles = currentVehicles;
        this.maxCapacity = maxCapacity;
    }

    public String getIntersectionId() {
        return intersectionId;
    }

    public int getCurrentVehicles() {
        return currentVehicles;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}
