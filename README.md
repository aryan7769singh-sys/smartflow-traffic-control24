# Smart Traffic Monitoring & Management System (SmartFlow)

**Course:** Object Oriented Techniques Using Java  
**Project Category:** Problem-Based Learning (PBL) Academic Prototype  
**Target Jurisdictions:** Delhi NCR Metropolitan Surveillance Grid (8 Monitored Intersections)  
**Primary Users:** Traffic Police, Municipal Corporations, City Planning Authorities, Emergency Services & Commuters  

---

## 🌟 Executive Summary

**SmartFlow** is a modern smart-city traffic monitoring and adaptive signal control prototype. It combines:
1. **An Interactive React + Vite + Tailwind CSS + Leaflet Command Dashboard:** Featuring real-time vehicle simulation, live Delhi NCR map pins, dynamic 4-way crossroad signal controls, automated congestion alert management, vehicle fleet telemetry with emergency vehicle preemption, CSV export, and a printable executive engineering audit report.
2. **A Standalone Object-Oriented Java Architecture:** Demonstrating all fundamental object-oriented techniques (Classes, Objects, Encapsulation, Inheritance, Polymorphism, Interfaces, Abstract Classes, Custom Exception Handling, and the Java Collections Framework) for academic review and faculty viva defense.

> [!NOTE]
> **Data Transparency Notice:** Real municipal IoT loop sensors and live government traffic APIs are not available for academic projects. Therefore, this prototype utilizes realistic, bounded stochastic simulations clearly labeled as simulated telemetry.

---

## 🏛️ Java OOP Concepts Mapping (For Faculty Viva & Project Defense)

| # | OOP Concept | Java Source File | Concrete Implementation Details |
|---|---|---|---|
| **1** | **Classes & Objects** | [`Vehicle.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/model/Vehicle.java), [`TrafficSignal.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/model/TrafficSignal.java), [`TrafficAlert.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/model/TrafficAlert.java) | Real-world traffic entity modeling with constructors, instance variables, and methods. |
| **2** | **Encapsulation** | All classes in `com.smarttraffic.model` | `private` attributes, validated getter/setter mutators, defensive state copying, invariant checks. |
| **3** | **Inheritance** | [`EmergencyVehicle.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/model/EmergencyVehicle.java), [`Intersection.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/model/Intersection.java) | `EmergencyVehicle extends Vehicle`; `Intersection extends TrafficComponent`. Reuses code while specializing behavior. |
| **4** | **Polymorphism** | [`EmergencyVehicle.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/model/EmergencyVehicle.java), [`AdaptiveDensityStrategy.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/strategy/AdaptiveDensityStrategy.java) | Method overriding: `getPriorityLevel()`, `getMovementBehavior()`. Dynamic strategy swapping in `AdaptiveSignalController`. |
| **5** | **Interfaces** | [`SignalControlStrategy.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/strategy/SignalControlStrategy.java), [`TrafficDataProvider.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/provider/TrafficDataProvider.java) | Pure abstractions specifying contracts for signal timing algorithms and data providers. |
| **6** | **Abstract Classes** | [`TrafficComponent.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/model/TrafficComponent.java) | Base class defining shared lifecycle state, operational flags, timestamps, and abstract `getDiagnosticStatus()`. |
| **7** | **Exception Handling** | [`InvalidSignalException.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/exception/InvalidSignalException.java), [`TrafficOverflowException.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/exception/TrafficOverflowException.java) | Custom checked and unchecked exceptions thrown during concurrent green conflicts and capacity overflows, managed with `try-catch-finally`. |
| **8** | **Collections Framework** | [`TrafficSimulationService.java`](file:///b:/Python%20WS/pyhtonPP/Paratha/backend/src/main/java/com/smarttraffic/service/TrafficSimulationService.java) | Utilizes `Map`, `HashMap`, `EnumMap`, `List`, `ArrayList`, and `Collections.unmodifiableList`. |

---

## 🧮 Adaptive Signal Optimization Algorithm

Rather than making unsupported claims of "opaque AI", the system implements a demand-responsive proportional split allocation algorithm:

$$\text{greenTime}_d = \text{minGreen} + \left(\frac{\text{count}_d}{\text{totalCount}}\right) \times \text{adjustableBudget}$$

Where:
- $\text{minGreen} = 10\text{s}$ (configurable safety floor for pedestrian clearance)
- $\text{maxGreen} = 60\text{s}$ (maximum phase ceiling)
- $\text{adjustableBudget} = \text{targetCycleTotal} - (4 \times \text{minGreen})$
- Results are clamped between $[\text{minGreen}, \text{maxGreen}]$ and balanced to maintain network progression.

---

## 💻 Tech Stack

### Frontend Command Dashboard
- **Framework:** React 18 with TypeScript & Vite
- **Styling:** Tailwind CSS (Dark Navy Smart City Control Center theme)
- **Icons:** Lucide React
- **Visual Charts:** Recharts (Area charts, Bar charts, Pie charts, Stacked duration charts)
- **Geographic Mapping:** Leaflet with OpenStreetMap tiles (and interactive SVG schematic mode fallback)
- **Persistence:** LocalStorage synchronization

### Backend OOP Module
- **Language:** Java 21 LTS
- **Architecture:** Layered Architecture (Models, Strategies, Providers, Exceptions, Services)
- **Design Patterns:** Strategy Pattern, Composition Pattern, Provider Pattern

---

## 🚀 How to Run the Project

### 1. Running the Interactive Frontend Dashboard

```powershell
# Navigate to the frontend directory
cd frontend

# Dependencies are already installed! Start the Vite dev server:
npm run dev
```

Open your browser at `http://localhost:3000` (or the port displayed in your terminal).

### 2. Compiling & Running the Java Backend

```powershell
# Navigate to the Java source root
cd backend\src\main\java

# Compile all packages
javac com\smarttraffic\model\*.java com\smarttraffic\strategy\*.java com\smarttraffic\provider\*.java com\smarttraffic\exception\*.java com\smarttraffic\service\*.java com\smarttraffic\Main.java

# Run the OOP verification scenario
java com.smarttraffic.Main
```

---

## ⏱️ 5-Minute Faculty Presentation Guide

| Time | Step | Action in Dashboard | What to Say to Evaluators |
|---|---|---|---|
| **0:00 - 1:00** | **System Overview** | Click **"Run Faculty Demo Scenario"** banner at top | "This is SmartFlow, an intelligent corridor management prototype designed for Delhi NCR with 8 critical junctions." |
| **1:00 - 2:00** | **Traffic Surge & Live Map** | Watch Step 2-4 automatically escalate AIIMS node | "We inject an artificial traffic surge. Notice the AIIMS map pin transitioning from Amber to Critical Red as density reaches 91%." |
| **2:00 - 3:00** | **Alerts & Cooldown** | Tab transitions to **Congestion Alerts** (Step 5) | "The surveillance engine creates a high-priority alert. A 40-second cooldown prevents duplicate notification flooding." |
| **3:00 - 4:00** | **Adaptive Optimization** | Tab transitions to **Signal Control** (Step 6-7) | "Instead of rigid 30s timers, our algorithm dynamically recalculates green splits: SOUTH corridor gets 46s to clear its 62-car queue." |
| **4:00 - 4:45** | **Emergency Preemption** | Observe Step 9 dispatching Ambulance | "An emergency ambulance requests green corridor priority. The controller forces immediate green light preemption on its approach." |
| **4:45 - 5:00** | **Reports & Java OOP** | Open **Analytics & Reports** & print preview | "Export live CSV and printable executive report. The Java backend strictly implements all 8 syllabus OOP concepts." |

---

## 📁 Repository Structure

```
Paratha/
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/         # Toast notifications, DemoBanner
│   │   │   ├── layout/         # Top Navbar, Persistent Sidebar
│   │   │   └── reports/        # PrintableReportModal
│   │   ├── context/            # TrafficContext (Shared state & simulation timer)
│   │   ├── data/               # initialData.ts (8 Delhi NCR intersections & baseline alerts)
│   │   ├── pages/              # Overview, Map, Intersections, Signals, Alerts, Analytics, Vehicles, Settings
│   │   ├── services/           # trafficSimulationEngine, adaptiveSignalService
│   │   ├── types/              # traffic.ts (Complete TypeScript interfaces)
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   └── index.css
│   ├── package.json
│   ├── tailwind.config.js
│   └── vite.config.ts
├── backend/
│   └── src/main/java/com/smarttraffic/
│       ├── exception/          # InvalidSignalException, TrafficOverflowException
│       ├── model/              # TrafficComponent, Vehicle, EmergencyVehicle, Intersection, TrafficSignal, Sensor, TrafficAlert
│       ├── provider/           # TrafficDataProvider, SimulatedDelhiGridProvider
│       ├── service/            # TrafficAnalyzer, AdaptiveSignalController, TrafficSimulationService
│       ├── strategy/           # SignalControlStrategy, AdaptiveDensityStrategy, FixedTimeStrategy
│       └── Main.java           # Complete standalone OOP verification runner
└── README.md
```

---

## ⚠️ Academic Limitations & Real-World City Extensions

1. **Simulated Telemetry:** Does not interface with real physical NEMA TS-2 or 2070 traffic signal hardware or municipal SCATS/SCOOT master servers.
2. **Emergency Vehicle Dispatch:** In real-world cities, signal preemption requires encrypted C-V2X radio beacons and authorized CAD dispatch protocols.
3. **Future Scope:** Adding micro-simulation vehicle trajectory models (SUMO / Aimsun integration) and city bus rapid transit (BRT) priority lanes.
