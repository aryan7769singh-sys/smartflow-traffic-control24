package com.smarttraffic.model;

/**
 * CLASS & OBJECTS + ENCAPSULATION: Demonstrates OOP Concepts 1 & 2.
 * Represents a standard vehicular unit moving across the network.
 */
public class Vehicle {
    private String vehicleId;
    private String vehicleType;
    private String intersectionId;
    private String direction; // NORTH, SOUTH, EAST, WEST
    private String status;    // Approaching, Waiting, Passed, Priority Routed
    private double speedKmph;

    public Vehicle(String vehicleId, String vehicleType, String intersectionId, String direction) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle ID cannot be null or empty.");
        }
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType != null ? vehicleType : "Car";
        this.intersectionId = intersectionId;
        this.direction = direction;
        this.status = "Approaching";
        this.speedKmph = 35.0;
    }

    /**
     * Polymorphic method overridden by subclasses (e.g. EmergencyVehicle).
     * @return Dispatch priority rank (0 = Normal, 10 = Emergency)
     */
    public int getPriorityLevel() {
        return 0; // Standard non-priority vehicle
    }

    public String getMovementBehavior() {
        return String.format("Vehicle %s obeys standard cyclic signal phase at %s approach.", vehicleId, direction);
    }

    // Getters and Setters with Encapsulation
    public String getVehicleId() {
        return vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getIntersectionId() {
        return intersectionId;
    }

    public void setIntersectionId(String intersectionId) {
        this.intersectionId = intersectionId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getSpeedKmph() {
        return speedKmph;
    }

    public void setSpeedKmph(double speedKmph) {
        if (speedKmph < 0) {
            throw new IllegalArgumentException("Vehicle speed cannot be negative.");
        }
        this.speedKmph = speedKmph;
    }

    @Override
    public String toString() {
        return String.format("Vehicle[ID=%s, Type=%s, Node=%s, Dir=%s, Status=%s, Priority=%d]",
                vehicleId, vehicleType, intersectionId, direction, status, getPriorityLevel());
    }
}
