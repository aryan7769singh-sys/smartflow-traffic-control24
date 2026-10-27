package com.smarttraffic.model;

import java.util.HashMap;
import java.util.Map;

/**
 * CLASSES & OBJECTS + INHERITANCE:
 * Extends TrafficComponent, encapsulating 4-phase traffic light states and cycle durations.
 */
public class TrafficSignal extends TrafficComponent {
    public enum SignalColor { GREEN, YELLOW, RED }
    public enum Direction { NORTH, SOUTH, EAST, WEST }

    private Direction activeDirection;
    private SignalColor currentColor;
    private int timeRemainingSeconds;
    private Map<Direction, Integer> greenDurations;
    private int yellowDurationSeconds;
    private boolean manualOverrideActive;

    public TrafficSignal(String signalId, String name) {
        super(signalId, name);
        this.activeDirection = Direction.NORTH;
        this.currentColor = SignalColor.GREEN;
        this.timeRemainingSeconds = 30;
        this.yellowDurationSeconds = 4;
        this.manualOverrideActive = false;

        this.greenDurations = new HashMap<>();
        this.greenDurations.put(Direction.NORTH, 30);
        this.greenDurations.put(Direction.SOUTH, 30);
        this.greenDurations.put(Direction.EAST, 25);
        this.greenDurations.put(Direction.WEST, 25);
    }

    @Override
    public String getDiagnosticStatus() {
        return String.format("Signal %s: Active Phase=%s, Color=%s, Remaining=%ds, Override=%s",
                getId(), activeDirection, currentColor, timeRemainingSeconds, manualOverrideActive);
    }

    public Direction getActiveDirection() {
        return activeDirection;
    }

    public void setActiveDirection(Direction activeDirection) {
        this.activeDirection = activeDirection;
        touch();
    }

    public SignalColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(SignalColor currentColor) {
        this.currentColor = currentColor;
        touch();
    }

    public int getTimeRemainingSeconds() {
        return timeRemainingSeconds;
    }

    public void setTimeRemainingSeconds(int timeRemainingSeconds) {
        this.timeRemainingSeconds = Math.max(0, timeRemainingSeconds);
        touch();
    }

    public Map<Direction, Integer> getGreenDurations() {
        return greenDurations;
    }

    public void setGreenDurations(Map<Direction, Integer> greenDurations) {
        if (greenDurations != null) {
            this.greenDurations = new HashMap<>(greenDurations);
            touch();
        }
    }

    public int getYellowDurationSeconds() {
        return yellowDurationSeconds;
    }

    public void setYellowDurationSeconds(int yellowDurationSeconds) {
        this.yellowDurationSeconds = yellowDurationSeconds;
    }

    public boolean isManualOverrideActive() {
        return manualOverrideActive;
    }

    public void setManualOverrideActive(boolean manualOverrideActive) {
        this.manualOverrideActive = manualOverrideActive;
        touch();
    }
}
