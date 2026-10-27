package com.smarttraffic.strategy;

import com.smarttraffic.model.Intersection;
import com.smarttraffic.model.TrafficSignal;
import java.util.Map;

/**
 * INTERFACE: Demonstrates OOP Concept 5.
 * Strategy pattern interface allowing runtime swapping of signal control policies.
 */
public interface SignalControlStrategy {
    /**
     * Calculates recommended green durations based on queue demands.
     * @param intersection Target intersection
     * @param minGreenSeconds Minimum safety floor (e.g. 10s)
     * @param maxGreenSeconds Maximum single phase green ceiling (e.g. 60s)
     * @return Map of recommended green durations per direction
     */
    Map<TrafficSignal.Direction, Integer> calculateTimings(
            Intersection intersection, int minGreenSeconds, int maxGreenSeconds);

    /**
     * Human-readable name of the strategy algorithm.
     */
    String getStrategyName();

    /**
     * Explanation rationale for the decision.
     */
    String getDecisionRationale(Intersection intersection);
}
