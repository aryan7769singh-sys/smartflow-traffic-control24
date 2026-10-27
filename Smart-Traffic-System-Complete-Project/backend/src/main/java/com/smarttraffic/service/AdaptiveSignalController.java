package com.smarttraffic.service;

import com.smarttraffic.exception.InvalidSignalException;
import com.smarttraffic.model.Intersection;
import com.smarttraffic.model.TrafficSignal;
import com.smarttraffic.strategy.AdaptiveDensityStrategy;
import com.smarttraffic.strategy.SignalControlStrategy;
import java.util.Map;

/**
 * CONTROLLER SERVICE + STRATEGY PATTERN:
 * Manages signal optimization using pluggable algorithms and enforces safety interlocks.
 */
public class AdaptiveSignalController {
    private SignalControlStrategy strategy;
    private int minGreenSec = 10;
    private int maxGreenSec = 60;

    public AdaptiveSignalController(SignalControlStrategy strategy) {
        this.strategy = strategy != null ? strategy : new AdaptiveDensityStrategy();
    }

    public AdaptiveSignalController() {
        this(new AdaptiveDensityStrategy());
    }

    /**
     * Runtime polymorphism: Switch strategy algorithm on the fly.
     */
    public void setStrategy(SignalControlStrategy strategy) {
        if (strategy != null) {
            this.strategy = strategy;
        }
    }

    public SignalControlStrategy getStrategy() {
        return strategy;
    }

    /**
     * Calculates recommended signal durations.
     */
    public Map<TrafficSignal.Direction, Integer> calculateRecommendedTiming(Intersection intersection) {
        return strategy.calculateTimings(intersection, minGreenSec, maxGreenSec);
    }

    /**
     * Executes adaptive optimization on an intersection.
     */
    public void optimizeSignalTiming(Intersection intersection) {
        if (intersection == null || intersection.getAssociatedTrafficSignal() == null) return;

        Map<TrafficSignal.Direction, Integer> newTimings = calculateRecommendedTiming(intersection);
        TrafficSignal signal = intersection.getAssociatedTrafficSignal();
        signal.setGreenDurations(newTimings);

        // Adjust wait time following queue clearance
        int currentWait = intersection.getAverageWaitTimeSec();
        intersection.setAverageWaitTimeSec(Math.max(15, (int) Math.round(currentWait * 0.82)));
    }

    /**
     * Demonstrates EXCEPTION HANDLING:
     * Applies manual override while validating that conflicting states do not compromise junction safety.
     */
    public void applyManualOverride(Intersection intersection, TrafficSignal.Direction dir,
                                    TrafficSignal.SignalColor color) throws InvalidSignalException {
        if (intersection == null) {
            throw new InvalidSignalException("Target intersection reference is null", "UNKNOWN", "NULL");
        }

        TrafficSignal signal = intersection.getAssociatedTrafficSignal();
        if (signal == null) {
            throw new InvalidSignalException("Associated traffic signal unit missing", intersection.getId(), "MISSING_SIGNAL");
        }

        // Safety rule: Cannot set a direction to GREEN if conflicting cross-street is already GREEN
        if (color == TrafficSignal.SignalColor.GREEN && signal.isManualOverrideActive()
                && signal.getCurrentColor() == TrafficSignal.SignalColor.GREEN
                && signal.getActiveDirection() != dir) {
            throw new InvalidSignalException(
                    "Safety Interlock Triggered: Cannot grant concurrent green phase to perpendicular direction " + dir,
                    intersection.getId(),
                    "CONCURRENT_GREEN_" + dir
            );
        }

        signal.setManualOverrideActive(true);
        signal.setActiveDirection(dir);
        signal.setCurrentColor(color);
        signal.setTimeRemainingSeconds(45);
    }

    public void restoreAutomaticCycle(Intersection intersection) {
        if (intersection != null && intersection.getAssociatedTrafficSignal() != null) {
            intersection.getAssociatedTrafficSignal().setManualOverrideActive(false);
        }
    }
}
