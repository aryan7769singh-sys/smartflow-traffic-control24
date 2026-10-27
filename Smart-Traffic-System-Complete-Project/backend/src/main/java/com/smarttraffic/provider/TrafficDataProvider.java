package com.smarttraffic.provider;

import com.smarttraffic.model.Intersection;
import com.smarttraffic.model.Vehicle;
import java.util.List;

/**
 * INTERFACE: Demonstrates OOP Concept 5.
 * Decouples the simulation core from data sources (enabling in-memory simulated data now, or physical IoT/APIs later).
 */
public interface TrafficDataProvider {
    List<Intersection> loadIntersections();
    List<Vehicle> loadInitialVehicles();
    String getGridRegionName();
    boolean isLiveHardwareSource();
}
