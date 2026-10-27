package com.smarttraffic.model;

import java.util.Random;

/**
 * CLASSES & OBJECTS + SIMULATION:
 * Represents a smart induction loop detector or virtual camera sensor.
 */
public class Sensor extends TrafficComponent {
    private String sensorType; // INDUCTION_LOOP, VIRTUAL_CAMERA, RADAR
    private String intersectionId;
    private TrafficSignal.Direction monitoredDirection;
    private static final Random RANDOM = new Random();

    public Sensor(String sensorId, String name, String sensorType, String intersectionId, TrafficSignal.Direction direction) {
        super(sensorId, name);
        this.sensorType = sensorType;
        this.intersectionId = intersectionId;
        this.monitoredDirection = direction;
    }

    @Override
    public String getDiagnosticStatus() {
        return String.format("Sensor %s [%s] monitoring %s of %s: Status=%s",
                getId(), sensorType, monitoredDirection, intersectionId, isOperational() ? "ONLINE" : "OFFLINE");
    }

    /**
     * Provides simulated vehicle count readings.
     * Clearly labeled as simulated per project requirements.
     */
    public int readSimulatedVehicleCount(int currentBaseline) {
        if (!isOperational()) return 0;
        int delta = RANDOM.nextInt(7) - 3; // -3 to +3 flux
        return Math.max(0, currentBaseline + delta);
    }

    public String getSensorType() {
        return sensorType;
    }

    public String getIntersectionId() {
        return intersectionId;
    }

    public TrafficSignal.Direction getMonitoredDirection() {
        return monitoredDirection;
    }
}
