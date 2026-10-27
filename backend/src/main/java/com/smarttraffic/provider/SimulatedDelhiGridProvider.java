package com.smarttraffic.provider;

import com.smarttraffic.model.EmergencyVehicle;
import com.smarttraffic.model.Intersection;
import com.smarttraffic.model.TrafficSignal;
import com.smarttraffic.model.Vehicle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * COLLECTIONS FRAMEWORK + POLYMORPHISM: Demonstrates OOP Concepts 4 & 8.
 * Provides realistic simulated telemetry for the 8 Delhi NCR intersections.
 */
public class SimulatedDelhiGridProvider implements TrafficDataProvider {

    @Override
    public List<Intersection> loadIntersections() {
        List<Intersection> list = new ArrayList<>();

        list.add(createNode("INT-DEL-01", "Connaught Place - Barakhamba Jn", "Central Delhi Radial Rd", 28.6304, 77.2227, 32, 45, 28, 35));
        list.add(createNode("INT-DEL-02", "AIIMS Ring Road Flyover Junction", "South Delhi Medical Emergency Corridor", 28.5672, 77.2100, 58, 62, 44, 38));
        list.add(createNode("INT-DEL-03", "ITO Crossing - Vikas Marg", "East Delhi Gateway / Secretariat", 28.6289, 77.2407, 48, 42, 55, 36));
        list.add(createNode("INT-DEL-04", "Dhaula Kuan NH-48 Interchange", "South-West Delhi Airport Corridor", 28.5921, 77.1618, 25, 30, 22, 31));
        list.add(createNode("INT-DEL-05", "Ashram Chowk Underpass Junction", "South-East Delhi / Mathura Road", 28.5714, 77.2588, 40, 38, 35, 33));
        list.add(createNode("INT-DEL-06", "Rajouri Garden Metro Crossing", "West Delhi Ring Road", 28.6492, 77.1226, 18, 22, 15, 19));
        list.add(createNode("INT-DEL-07", "Kashmere Gate ISBT Junction", "North Delhi Interstate Bus Hub", 28.6669, 77.2285, 20, 15, 12, 16));
        list.add(createNode("INT-DEL-08", "Mayur Vihar - Noida Link Road", "Trans-Yamuna NCR Border Corridor", 28.6041, 77.2942, 14, 16, 18, 12));

        return list;
    }

    private Intersection createNode(String id, String name, String area, double lat, double lng,
                                   int north, int south, int east, int west) {
        Intersection node = new Intersection(id, name, area, lat, lng);
        Map<TrafficSignal.Direction, Integer> counts = new EnumMap<>(TrafficSignal.Direction.class);
        counts.put(TrafficSignal.Direction.NORTH, north);
        counts.put(TrafficSignal.Direction.SOUTH, south);
        counts.put(TrafficSignal.Direction.EAST, east);
        counts.put(TrafficSignal.Direction.WEST, west);
        node.setDirectionalCounts(counts);
        return node;
    }

    @Override
    public List<Vehicle> loadInitialVehicles() {
        List<Vehicle> list = new ArrayList<>();
        list.add(new Vehicle("VEH-101", "Car", "INT-DEL-01", "SOUTH"));
        list.add(new Vehicle("VEH-102", "Bus", "INT-DEL-03", "EAST"));
        list.add(new Vehicle("VEH-103", "Truck", "INT-DEL-05", "NORTH"));
        // Polymorphism: EmergencyVehicle added to List<Vehicle>
        list.add(new EmergencyVehicle("VEH-EMG-911", "Ambulance", "INT-DEL-02", "NORTH", "INC-TRAUMA-77"));
        return list;
    }

    @Override
    public String getGridRegionName() {
        return "Delhi National Capital Region (NCR)";
    }

    @Override
    public boolean isLiveHardwareSource() {
        return false; // Explicitly simulated per project requirements
    }
}
