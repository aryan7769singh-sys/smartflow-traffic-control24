package com.smarttraffic.service;

import com.smarttraffic.model.*;
import com.smarttraffic.provider.TrafficDataProvider;
import java.util.*;

/**
 * SIMULATION ENGINE & COLLECTIONS FRAMEWORK: Demonstrates OOP Concepts 1, 2, 4 & 8.
 * Central service orchestrating simulated sensor counts, signal countdowns, and alert dispatching.
 */
public class TrafficSimulationService {
    // Demonstrates Collections Framework: HashMap, ArrayList, List, Map
    private Map<String, Intersection> intersectionsMap;
    private List<Vehicle> vehiclesList;
    private List<TrafficAlert> activeAlerts;
    private TrafficAnalyzer analyzer;
    private AdaptiveSignalController controller;
    private boolean simulating;
    private int simulationTickCount;

    public TrafficSimulationService(TrafficDataProvider dataProvider) {
        this.intersectionsMap = new HashMap<>();
        this.vehiclesList = new ArrayList<>();
        this.activeAlerts = new ArrayList<>();
        this.analyzer = new TrafficAnalyzer();
        this.controller = new AdaptiveSignalController();
        this.simulating = false;
        this.simulationTickCount = 0;

        if (dataProvider != null) {
            for (Intersection node : dataProvider.loadIntersections()) {
                this.intersectionsMap.put(node.getId(), node);
            }
            this.vehiclesList.addAll(dataProvider.loadInitialVehicles());
        }
    }

    public void startSimulation() {
        this.simulating = true;
    }

    public void pauseSimulation() {
        this.simulating = false;
    }

    public void resumeSimulation() {
        this.simulating = true;
    }

    public void resetSimulation(TrafficDataProvider dataProvider) {
        pauseSimulation();
        this.intersectionsMap.clear();
        this.vehiclesList.clear();
        this.activeAlerts.clear();
        this.simulationTickCount = 0;

        if (dataProvider != null) {
            for (Intersection node : dataProvider.loadIntersections()) {
                this.intersectionsMap.put(node.getId(), node);
            }
            this.vehiclesList.addAll(dataProvider.loadInitialVehicles());
        }
    }

    /**
     * Executes one simulation cycle (tick).
     */
    public void tick(int deltaSec) {
        if (!simulating) return;
        simulationTickCount++;

        for (Intersection node : intersectionsMap.values()) {
            TrafficSignal signal = node.getAssociatedTrafficSignal();
            if (signal == null) continue;

            // Update signal countdown
            if (!signal.isManualOverrideActive()) {
                int rem = signal.getTimeRemainingSeconds() - deltaSec;
                if (signal.getCurrentColor() == TrafficSignal.SignalColor.GREEN && rem <= signal.getYellowDurationSeconds()) {
                    signal.setCurrentColor(TrafficSignal.SignalColor.YELLOW);
                }

                if (rem <= 0) {
                    // Transition to next direction
                    TrafficSignal.Direction next = getNextCyclicDirection(signal.getActiveDirection());
                    signal.setActiveDirection(next);
                    signal.setCurrentColor(TrafficSignal.SignalColor.GREEN);
                    int allocatedGreen = signal.getGreenDurations().getOrDefault(next, 30);
                    signal.setTimeRemainingSeconds(allocatedGreen);
                } else {
                    signal.setTimeRemainingSeconds(rem);
                }
            }

            // Inflow and Outflow
            Map<TrafficSignal.Direction, Integer> counts = node.getDirectionalCounts();
            TrafficSignal.Direction active = signal.getActiveDirection();
            boolean isGreen = signal.getCurrentColor() == TrafficSignal.SignalColor.GREEN;

            for (TrafficSignal.Direction dir : TrafficSignal.Direction.values()) {
                int cur = counts.getOrDefault(dir, 0);
                if (isGreen && dir == active) {
                    cur = Math.max(0, cur - 2); // 2 vehicles discharge
                }
                // Random simulated arriving vehicle
                if (Math.random() > 0.5) {
                    cur += 1;
                }
                counts.put(dir, cur);
            }
            node.setDirectionalCounts(counts);

            // Recompute wait time
            int newWait = analyzer.calculateWaitingTime(node.getTotalVehicleCount(), 120);
            node.setAverageWaitTimeSec(newWait);

            // Alert generation for high density
            double density = node.calculateDensityPercentage();
            if (density >= 85.0 && simulationTickCount % 5 == 0) {
                TrafficAlert alert = new TrafficAlert(
                        "ALT-JAVA-" + System.currentTimeMillis(),
                        node.getId(),
                        node.getName(),
                        "CRITICAL_CONGESTION",
                        TrafficAlert.Severity.CRITICAL,
                        String.format("High vehicular queue detected: Density %.1f%% with %d vehicles.",
                                density, node.getTotalVehicleCount())
                );
                activeAlerts.add(0, alert);
            }
        }
    }

    private TrafficSignal.Direction getNextCyclicDirection(TrafficSignal.Direction current) {
        switch (current) {
            case NORTH: return TrafficSignal.Direction.EAST;
            case EAST: return TrafficSignal.Direction.SOUTH;
            case SOUTH: return TrafficSignal.Direction.WEST;
            case WEST: default: return TrafficSignal.Direction.NORTH;
        }
    }

    /**
     * POLYMORPHISM in action: Handles EmergencyVehicle priority preemption.
     */
    public void dispatchEmergencyPriority(EmergencyVehicle ev) {
        Intersection node = intersectionsMap.get(ev.getIntersectionId());
        if (node != null && node.getAssociatedTrafficSignal() != null) {
            TrafficSignal.Direction emgDir = TrafficSignal.Direction.valueOf(ev.getDirection());
            TrafficSignal sig = node.getAssociatedTrafficSignal();

            sig.setManualOverrideActive(true);
            sig.setActiveDirection(emgDir);
            sig.setCurrentColor(TrafficSignal.SignalColor.GREEN);
            sig.setTimeRemainingSeconds(50); // Preemption green corridor

            TrafficAlert alert = new TrafficAlert(
                    "EMG-" + System.currentTimeMillis(),
                    node.getId(),
                    node.getName(),
                    "EMERGENCY_PREEMPTION",
                    TrafficAlert.Severity.CRITICAL,
                    ev.getMovementBehavior()
            );
            activeAlerts.add(0, alert);
        }
    }

    // Collections getters
    public Map<String, Intersection> getIntersectionsMap() {
        return Collections.unmodifiableMap(intersectionsMap);
    }

    public List<Vehicle> getVehiclesList() {
        return Collections.unmodifiableList(vehiclesList);
    }

    public List<TrafficAlert> getActiveAlerts() {
        return Collections.unmodifiableList(activeAlerts);
    }

    public AdaptiveSignalController getController() {
        return controller;
    }

    public boolean isSimulating() {
        return simulating;
    }
}
