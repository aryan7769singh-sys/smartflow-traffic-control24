package com.smarttraffic;

import com.smarttraffic.exception.InvalidSignalException;
import com.smarttraffic.model.*;
import com.smarttraffic.provider.SimulatedDelhiGridProvider;
import com.smarttraffic.provider.TrafficDataProvider;
import com.smarttraffic.service.AdaptiveSignalController;
import com.smarttraffic.service.TrafficSimulationService;
import com.smarttraffic.strategy.AdaptiveDensityStrategy;
import com.smarttraffic.strategy.FixedTimeStrategy;

import java.util.Map;

/**
 * PROJECT DEMONSTRATION RUNNER
 * Course: Object Oriented Techniques Using Java
 * Project: Smart Traffic Monitoring and Management System
 * 
 * Verifies all required OOP Concepts:
 * 1. Classes and Objects
 * 2. Encapsulation
 * 3. Inheritance
 * 4. Polymorphism
 * 5. Interfaces
 * 6. Abstract Classes
 * 7. Exception Handling
 * 8. Collections Framework
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   SMART TRAFFIC MONITORING AND MANAGEMENT SYSTEM (JAVA OOP DEMONSTRATION)     ");
        System.out.println("   Course: Object Oriented Techniques Using Java | PBL Academic Prototype      ");
        System.out.println("================================================================================\n");

        printOopConceptsSummary();

        // 1. DATA PROVIDER & SIMULATION INITIALIZATION (Interfaces & Polymorphism)
        System.out.println("\n[PHASE 1] Initializing Simulated Delhi NCR Grid Topology...");
        TrafficDataProvider dataProvider = new SimulatedDelhiGridProvider();
        TrafficSimulationService simService = new TrafficSimulationService(dataProvider);

        System.out.println("Grid Region: " + dataProvider.getGridRegionName());
        System.out.println("Hardware Telemetry Source: Simulated Sensors (Explicitly Simulated for PBL)");
        System.out.println("Loaded Intersections in Map (Collections Framework): " + simService.getIntersectionsMap().size() + " nodes");

        // 2. DISPLAY INITIAL INTERSECTIONS
        System.out.println("\n[PHASE 2] Initial Monitored Node Inventory:");
        System.out.printf("%-12s | %-35s | %-10s | %-8s | %-10s\n", "Node ID", "Intersection Name", "Vehicles", "Density", "Wait Time");
        System.out.println("--------------------------------------------------------------------------------");
        for (Intersection node : simService.getIntersectionsMap().values()) {
            System.out.printf("%-12s | %-35s | %-10d | %-7.1f%% | %-10ds\n",
                    node.getId(), node.getName(), node.getTotalVehicleCount(),
                    node.calculateDensityPercentage(), node.getAverageWaitTimeSec());
        }

        // 3. DEMONSTRATE OOP CONCEPT 3 & 4: INHERITANCE & POLYMORPHISM WITH VEHICLES
        System.out.println("\n[PHASE 3] Demonstrating Inheritance & Polymorphism (Vehicle vs. EmergencyVehicle):");
        Vehicle normalCar = new Vehicle("DL-01-C-1001", "Sedan", "INT-DEL-01", "NORTH");
        Vehicle normalBus = new Vehicle("DL-1P-B-2002", "Bus", "INT-DEL-03", "EAST");
        Vehicle emergencyAmbulance = new EmergencyVehicle("DL-01-AMB-911", "Ambulance", "INT-DEL-02", "NORTH", "CRITICAL_TRAUMA_CALL");

        Vehicle[] vehicleRoster = { normalCar, normalBus, emergencyAmbulance };
        for (Vehicle v : vehicleRoster) {
            // Polymorphic dispatch: calls EmergencyVehicle.getPriorityLevel() or Vehicle.getPriorityLevel()
            System.out.printf(" - %s [Priority: %d] -> %s\n",
                    v.getVehicleId(), v.getPriorityLevel(), v.getMovementBehavior());
        }

        // 4. DEMONSTRATE OOP CONCEPT 5: INTERFACES & STRATEGY PATTERN (Signal Control)
        System.out.println("\n[PHASE 4] Demonstrating Strategy Pattern (Fixed-Time vs. Adaptive Algorithm):");
        Intersection aiimsNode = simService.getIntersectionsMap().get("INT-DEL-02");
        AdaptiveSignalController controller = simService.getController();

        // Baseline: Fixed-time
        controller.setStrategy(new FixedTimeStrategy(30));
        System.out.println("Strategy: " + controller.getStrategy().getStrategyName());
        Map<TrafficSignal.Direction, Integer> fixedTimings = controller.calculateRecommendedTiming(aiimsNode);
        System.out.println("Fixed Splits: " + fixedTimings);

        // Runtime swap to Demand-Responsive Adaptive
        controller.setStrategy(new AdaptiveDensityStrategy(120));
        System.out.println("Switched Strategy: " + controller.getStrategy().getStrategyName());
        Map<TrafficSignal.Direction, Integer> adaptiveTimings = controller.calculateRecommendedTiming(aiimsNode);
        System.out.println("Adaptive Splits: " + adaptiveTimings);
        System.out.println("Decision Rationale: " + controller.getStrategy().getDecisionRationale(aiimsNode));

        // Apply optimization
        controller.optimizeSignalTiming(aiimsNode);
        System.out.println("Optimized " + aiimsNode.getName() + " -> New Wait Time: " + aiimsNode.getAverageWaitTimeSec() + "s");

        // 5. DEMONSTRATE OOP CONCEPT 7: EXCEPTION HANDLING
        System.out.println("\n[PHASE 5] Demonstrating Exception Handling (Safety Interlock Violation):");
        try {
            System.out.println("Attempting safe manual override to NORTH = GREEN...");
            controller.applyManualOverride(aiimsNode, TrafficSignal.Direction.NORTH, TrafficSignal.SignalColor.GREEN);
            System.out.println("Safe override succeeded.");

            System.out.println("Attempting CONCURRENT conflicting override to EAST = GREEN while NORTH is GREEN...");
            controller.applyManualOverride(aiimsNode, TrafficSignal.Direction.EAST, TrafficSignal.SignalColor.GREEN);
            System.out.println("WARNING: Unsafe state allowed (Bug)!");
        } catch (InvalidSignalException ex) {
            System.out.println("[EXCEPTION CAUGHT AS EXPECTED]: " + ex.getMessage());
            System.out.println("Exception handled gracefully: Junction safety interlock preserved.");
        } finally {
            controller.restoreAutomaticCycle(aiimsNode);
            System.out.println("Signal restored to automated cycle in finally block.");
        }

        // 6. SIMULATION PROGRESSION & EMERGENCY DISPATCH
        System.out.println("\n[PHASE 6] Running Simulation Ticks & Emergency Preemption Dispatch:");
        simService.startSimulation();
        for (int i = 1; i <= 3; i++) {
            simService.tick(2); // 2 second simulation delta
        }

        System.out.println("Dispatching Emergency Ambulance to AIIMS Corridor...");
        simService.dispatchEmergencyPriority((EmergencyVehicle) emergencyAmbulance);
        System.out.println("Active Alerts in System: " + simService.getActiveAlerts().size());
        for (TrafficAlert alert : simService.getActiveAlerts()) {
            System.out.println(" -> " + alert);
        }

        System.out.println("\n================================================================================");
        System.out.println("   JAVA BACKEND OOP VERIFICATION COMPLETE: ALL 8 OOP PRINCIPLES SATISFIED!     ");
        System.out.println("================================================================================\n");
    }

    private static void printOopConceptsSummary() {
        System.out.println("PROJECT OOP ARCHITECTURE MAPPING FOR FACULTY REVIEW:");
        System.out.println(" 1. Classes & Objects:     Vehicle, Intersection, TrafficSignal, Sensor, TrafficAlert");
        System.out.println(" 2. Encapsulation:         Private fields with getters/setters/invariants across all models");
        System.out.println(" 3. Inheritance:           EmergencyVehicle extends Vehicle; Intersection extends TrafficComponent");
        System.out.println(" 4. Polymorphism:          Dynamic method overriding in EmergencyVehicle; SignalControlStrategy implementations");
        System.out.println(" 5. Interfaces:            SignalControlStrategy, TrafficDataProvider");
        System.out.println(" 6. Abstract Classes:      TrafficComponent (base diagnostic and lifecycle contract)");
        System.out.println(" 7. Exception Handling:    InvalidSignalException, TrafficOverflowException, try-catch-finally");
        System.out.println(" 8. Collections Framework: Map, HashMap, EnumMap, List, ArrayList, unmodifiable wrappers");
    }
}
