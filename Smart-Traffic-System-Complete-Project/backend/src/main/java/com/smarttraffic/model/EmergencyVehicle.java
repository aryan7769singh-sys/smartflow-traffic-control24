package com.smarttraffic.model;

/**
 * INHERITANCE & POLYMORPHISM: Demonstrates OOP Concepts 3 & 4.
 * Extends the Vehicle superclass with emergency-specific preemption protocols and elevated priority levels.
 */
public class EmergencyVehicle extends Vehicle {
    private String emergencyType; // Ambulance, Fire Engine, Police Cruiser
    private String incidentRef;
    private boolean sirenActive;

    public EmergencyVehicle(String vehicleId, String emergencyType, String intersectionId, String direction, String incidentRef) {
        super(vehicleId, "Emergency", intersectionId, direction);
        this.emergencyType = emergencyType != null ? emergencyType : "Ambulance";
        this.incidentRef = incidentRef;
        this.sirenActive = true;
        setSpeedKmph(55.0); // Emergency response velocity
    }

    /**
     * POLYMORPHIC OVERRIDE: Returns highest priority to trigger green wave preemption.
     */
    @Override
    public int getPriorityLevel() {
        return 10; // Maximum emergency preemption
    }

    /**
     * POLYMORPHIC OVERRIDE: Custom movement behavior overriding Vehicle.
     */
    @Override
    public String getMovementBehavior() {
        return String.format("[EMERGENCY DISPATCH] %s (%s) requests IMMEDIATE GREEN WAVE PREEMPTION on %s approach! Incident: %s",
                getEmergencyType(), getVehicleId(), getDirection(), incidentRef);
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public String getIncidentRef() {
        return incidentRef;
    }

    public boolean isSirenActive() {
        return sirenActive;
    }

    public void setSirenActive(boolean sirenActive) {
        this.sirenActive = sirenActive;
    }

    @Override
    public String toString() {
        return String.format("EmergencyVehicle[ID=%s, Service=%s, Node=%s, Dir=%s, Siren=%s, Priority=%d]",
                getVehicleId(), emergencyType, getIntersectionId(), getDirection(), sirenActive, getPriorityLevel());
    }
}
