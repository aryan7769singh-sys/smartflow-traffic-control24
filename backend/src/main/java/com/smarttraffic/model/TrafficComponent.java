package com.smarttraffic.model;

import java.time.LocalDateTime;

/**
 * ABSTRACT CLASS: Demonstrates OOP Concept 6 (Abstract Classes).
 * Serves as the base foundation for all physical and logical smart traffic infrastructure components.
 */
public abstract class TrafficComponent {
    private String id;
    private String name;
    private LocalDateTime lastUpdated;
    private boolean operational;

    public TrafficComponent(String id, String name) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Component ID cannot be null or empty.");
        }
        this.id = id;
        this.name = name;
        this.lastUpdated = LocalDateTime.now();
        this.operational = true;
    }

    /**
     * Abstract method enforced upon all concrete child classes (Polymorphism).
     * @return Status diagnostic report string
     */
    public abstract String getDiagnosticStatus();

    // Getters and Encapsulation
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void touch() {
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isOperational() {
        return operational;
    }

    public void setOperational(boolean operational) {
        this.operational = operational;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Operational: %s)", id, name, operational);
    }
}
