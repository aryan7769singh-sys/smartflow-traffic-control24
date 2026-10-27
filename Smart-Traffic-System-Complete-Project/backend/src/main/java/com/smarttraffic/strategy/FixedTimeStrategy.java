package com.smarttraffic.strategy;

import com.smarttraffic.model.Intersection;
import com.smarttraffic.model.TrafficSignal;
import java.util.EnumMap;
import java.util.Map;

/**
 * POLYMORPHISM & INTERFACES: Demonstrates OOP Concepts 4 & 5.
 * Traditional fixed-time cyclic control (e.g. static 30s per leg regardless of vehicle backlog).
 */
public class FixedTimeStrategy implements SignalControlStrategy {
    private int fixedGreenSeconds = 30;

    public FixedTimeStrategy(int fixedGreenSeconds) {
        this.fixedGreenSeconds = fixedGreenSeconds;
    }

    public FixedTimeStrategy() {
        this(30);
    }

    @Override
    public Map<TrafficSignal.Direction, Integer> calculateTimings(
            Intersection intersection, int minGreenSeconds, int maxGreenSeconds) {
        Map<TrafficSignal.Direction, Integer> result = new EnumMap<>(TrafficSignal.Direction.class);
        for (TrafficSignal.Direction dir : TrafficSignal.Direction.values()) {
            result.put(dir, fixedGreenSeconds);
        }
        return result;
    }

    @Override
    public String getStrategyName() {
        return "Fixed-Time Cyclic Allocation (Legacy Baseline)";
    }

    @Override
    public String getDecisionRationale(Intersection intersection) {
        return "Static fixed-time splits applied regardless of directional vehicle variations.";
    }
}
