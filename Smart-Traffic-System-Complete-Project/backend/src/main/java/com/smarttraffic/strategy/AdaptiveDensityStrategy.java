package com.smarttraffic.strategy;

import com.smarttraffic.model.Intersection;
import com.smarttraffic.model.TrafficSignal;
import java.util.EnumMap;
import java.util.Map;

/**
 * POLYMORPHISM & INTERFACES: Demonstrates OOP Concepts 4 & 5.
 * Concrete implementation of SignalControlStrategy implementing the transparent proportional demand allocation formula.
 * Formula:
 * greenTime = minGreen + (directionCount / totalCount) * adjustableBudget
 */
public class AdaptiveDensityStrategy implements SignalControlStrategy {
    private int targetCycleTotalSeconds = 120;

    public AdaptiveDensityStrategy(int targetCycleTotalSeconds) {
        this.targetCycleTotalSeconds = targetCycleTotalSeconds;
    }

    public AdaptiveDensityStrategy() {
        this(120);
    }

    @Override
    public Map<TrafficSignal.Direction, Integer> calculateTimings(
            Intersection intersection, int minGreenSeconds, int maxGreenSeconds) {

        Map<TrafficSignal.Direction, Integer> counts = intersection.getDirectionalCounts();
        int total = intersection.getTotalVehicleCount();

        Map<TrafficSignal.Direction, Integer> result = new EnumMap<>(TrafficSignal.Direction.class);

        if (total == 0) {
            int balanced = Math.max(minGreenSeconds, Math.min(maxGreenSeconds, targetCycleTotalSeconds / 4));
            for (TrafficSignal.Direction dir : TrafficSignal.Direction.values()) {
                result.put(dir, balanced);
            }
            return result;
        }

        int baselineReserve = 4 * minGreenSeconds;
        int adjustableBudget = Math.max(20, targetCycleTotalSeconds - baselineReserve);

        for (TrafficSignal.Direction dir : TrafficSignal.Direction.values()) {
            int count = counts.getOrDefault(dir, 0);
            double ratio = (double) count / total;
            int allocated = minGreenSeconds + (int) Math.round(ratio * adjustableBudget);
            int clamped = Math.max(minGreenSeconds, Math.min(maxGreenSeconds, allocated));
            result.put(dir, clamped);
        }

        return result;
    }

    @Override
    public String getStrategyName() {
        return "Demand-Responsive Adaptive Proportional Allocation";
    }

    @Override
    public String getDecisionRationale(Intersection intersection) {
        Map<TrafficSignal.Direction, Integer> counts = intersection.getDirectionalCounts();
        int max = -1;
        TrafficSignal.Direction peakDir = TrafficSignal.Direction.NORTH;

        for (Map.Entry<TrafficSignal.Direction, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                peakDir = entry.getKey();
            }
        }

        return String.format("Peak demand identified on %s approach (%d vehicles). Extended green duration assigned to clear queue while preserving safety minimum on opposing legs.",
                peakDir, max);
    }
}
