package com.smarttraffic.service;

import com.smarttraffic.model.Intersection;

/**
 * BUSINESS LOGIC SERVICE:
 * Calculates density, waiting delay, and congestion classifications based on traffic engineering formulas.
 */
public class TrafficAnalyzer {

    public enum CongestionClass { LOW, MODERATE, HIGH, CRITICAL }

    /**
     * Calculates density percentage of an intersection.
     */
    public double calculateDensity(Intersection intersection) {
        if (intersection == null || intersection.getMaxCapacity() <= 0) return 0.0;
        return ((double) intersection.getTotalVehicleCount() / intersection.getMaxCapacity()) * 100.0;
    }

    /**
     * Classifies congestion density into nominal operational tiers.
     */
    public CongestionClass classifyTraffic(double densityPercentage) {
        if (densityPercentage >= 80.0) {
            return densityPercentage >= 90.0 ? CongestionClass.CRITICAL : CongestionClass.HIGH;
        } else if (densityPercentage > 30.0) {
            return CongestionClass.MODERATE;
        }
        return CongestionClass.LOW;
    }

    /**
     * Calculates estimated average waiting time for queued vehicles.
     */
    public int calculateWaitingTime(int totalVehicles, int cycleDurationSec) {
        if (totalVehicles <= 0) return 10;
        int queueFactor = (int) Math.round((totalVehicles / 4.0) * 1.25);
        int baseWait = Math.min(120, Math.max(12, queueFactor));
        return baseWait;
    }
}
