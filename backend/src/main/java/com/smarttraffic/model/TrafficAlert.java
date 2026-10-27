package com.smarttraffic.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ENCAPSULATION & DATA MODELS:
 * Represents real-time congestion and incident threshold alarms.
 */
public class TrafficAlert {
    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }
    public enum Status { ACTIVE, ACKNOWLEDGED, RESOLVED }

    private String alertId;
    private String intersectionId;
    private String intersectionName;
    private String alertType;
    private Severity severity;
    private String description;
    private LocalDateTime timestamp;
    private Status status;

    public TrafficAlert(String alertId, String intersectionId, String intersectionName,
                        String alertType, Severity severity, String description) {
        this.alertId = alertId;
        this.intersectionId = intersectionId;
        this.intersectionName = intersectionName;
        this.alertType = alertType;
        this.severity = severity;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.status = Status.ACTIVE;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // Getters and Setters
    public String getAlertId() {
        return alertId;
    }

    public String getIntersectionId() {
        return intersectionId;
    }

    public String getIntersectionName() {
        return intersectionName;
    }

    public String getAlertType() {
        return alertType;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[%s] Alert %s at %s (%s): %s",
                severity, alertId, intersectionName, status, description);
    }
}
