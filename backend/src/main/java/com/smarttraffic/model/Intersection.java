package com.smarttraffic.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * CLASSES & OBJECTS + COMPOSITION + ENCAPSULATION:
 * Encapsulates junction telemetry, directional vehicle counts, and associated TrafficSignal.
 */
public class Intersection extends TrafficComponent {
    private String locationArea;
    private double latitude;
    private double longitude;
    private Map<TrafficSignal.Direction, Integer> directionalCounts;
    private TrafficSignal associatedTrafficSignal;
    private int maxCapacity;
    private int averageWaitTimeSec;

    public Intersection(String intersectionId, String name, String locationArea, double lat, double lng) {
        super(intersectionId, name);
        this.locationArea = locationArea;
        this.latitude = lat;
        this.longitude = lng;
        this.maxCapacity = 200;
        this.averageWaitTimeSec = 35;

        this.directionalCounts = new EnumMap<>(TrafficSignal.Direction.class);
        this.directionalCounts.put(TrafficSignal.Direction.NORTH, 25);
        this.directionalCounts.put(TrafficSignal.Direction.SOUTH, 25);
        this.directionalCounts.put(TrafficSignal.Direction.EAST, 20);
        this.directionalCounts.put(TrafficSignal.Direction.WEST, 20);

        this.associatedTrafficSignal = new TrafficSignal("SIG-" + intersectionId, name + " Signal Unit");
    }

    @Override
    public String getDiagnosticStatus() {
        return String.format("Intersection %s (%s): TotalVehicles=%d, Density=%.1f%%, Wait=%ds",
                getId(), getName(), getTotalVehicleCount(), calculateDensityPercentage(), averageWaitTimeSec);
    }

    public int getTotalVehicleCount() {
        int total = 0;
        for (int count : directionalCounts.values()) {
            total += count;
        }
        return total;
    }

    public double calculateDensityPercentage() {
        return Math.min(100.0, ((double) getTotalVehicleCount() / maxCapacity) * 100.0);
    }

    // Getters and Setters
    public String getLocationArea() {
        return locationArea;
    }

    public void setLocationArea(String locationArea) {
        this.locationArea = locationArea;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Map<TrafficSignal.Direction, Integer> getDirectionalCounts() {
        return directionalCounts;
    }

    public void setDirectionalCounts(Map<TrafficSignal.Direction, Integer> counts) {
        if (counts != null) {
            this.directionalCounts = new EnumMap<>(counts);
            touch();
        }
    }

    public TrafficSignal getAssociatedTrafficSignal() {
        return associatedTrafficSignal;
    }

    public void setAssociatedTrafficSignal(TrafficSignal associatedTrafficSignal) {
        this.associatedTrafficSignal = associatedTrafficSignal;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getAverageWaitTimeSec() {
        return averageWaitTimeSec;
    }

    public void setAverageWaitTimeSec(int averageWaitTimeSec) {
        this.averageWaitTimeSec = averageWaitTimeSec;
    }
}
