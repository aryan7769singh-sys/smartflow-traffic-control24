import React, { createContext, useContext, useState, useEffect, useRef, useCallback } from 'react';
import {
  Intersection,
  TrafficAlert,
  Vehicle,
  SystemSettings,
  OptimizationLog,
  ActivityItem,
  Direction,
  SignalColor,
  EmergencyRouteProposal,
} from '../types/traffic';
import {
  DEFAULT_SETTINGS,
  INITIAL_INTERSECTIONS,
  INITIAL_ALERTS,
  INITIAL_VEHICLES,
} from '../data/initialData';
import { simulateTick } from '../services/trafficSimulationEngine';
import { optimizeIntersection } from '../services/adaptiveSignalService';

interface TrafficContextType {
  intersections: Intersection[];
  alerts: TrafficAlert[];
  vehicles: Vehicle[];
  settings: SystemSettings;
  logs: OptimizationLog[];
  activities: ActivityItem[];
  isSimulating: boolean;
  activeTab: string;
  selectedIntersectionId: string | null;
  demoStep: number | null;
  demoActive: boolean;
  emergencyProposal: EmergencyRouteProposal | null;
  toastMessage: { text: string; type: 'info' | 'success' | 'warning' | 'alert' } | null;

  // Actions
  setActiveTab: (tab: string) => void;
  setSelectedIntersectionId: (id: string | null) => void;
  startSimulation: () => void;
  pauseSimulation: () => void;
  resumeSimulation: () => void;
  resetSimulation: () => void;
  setSimulationSpeed: (speed: number) => void;
  addTrafficToIntersection: (id: string, count?: number) => void;
  runOptimization: (id: string) => void;
  runAllOptimizations: () => void;
  setManualOverride: (id: string, direction: Direction, color: SignalColor, enabled: boolean) => void;
  acknowledgeAlert: (alertId: string) => void;
  resolveAlert: (alertId: string) => void;
  clearResolvedAlerts: () => void;
  addIntersection: (newInt: Partial<Intersection>) => void;
  updateIntersection: (id: string, updates: Partial<Intersection>) => void;
  deleteIntersection: (id: string) => void;
  addVehicle: (veh: Partial<Vehicle>) => void;
  dispatchEmergencyVehicle: (category: 'Ambulance' | 'Fire Engine' | 'Police Cruiser', targetId: string, direction: Direction) => void;
  clearEmergencyRoute: () => void;
  updateSettings: (newSettings: Partial<SystemSettings>) => void;
  resetSettingsToDefault: () => void;
  runDemoScenario: () => void;
  resetDemoScenario: () => void;
  showToast: (text: string, type?: 'info' | 'success' | 'warning' | 'alert') => void;
}

const TrafficContext = createContext<TrafficContextType | undefined>(undefined);

const LOCAL_STORAGE_KEY_INTERSECTIONS = 'smart_traffic_intersections_v1';
const LOCAL_STORAGE_KEY_SETTINGS = 'smart_traffic_settings_v1';
const LOCAL_STORAGE_KEY_ALERTS = 'smart_traffic_alerts_v1';

export const TrafficProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Load initial state with localStorage hydration
  const [intersections, setIntersections] = useState<Intersection[]>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_KEY_INTERSECTIONS);
      if (saved) return JSON.parse(saved);
    } catch {
      // ignore
    }
    return INITIAL_INTERSECTIONS;
  });

  const [alerts, setAlerts] = useState<TrafficAlert[]>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_KEY_ALERTS);
      if (saved) return JSON.parse(saved);
    } catch {
      // ignore
    }
    return INITIAL_ALERTS;
  });

  const [vehicles, setVehicles] = useState<Vehicle[]>(INITIAL_VEHICLES);

  const [settings, setSettings] = useState<SystemSettings>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_KEY_SETTINGS);
      if (saved) return JSON.parse(saved);
    } catch {
      // ignore
    }
    return DEFAULT_SETTINGS;
  });

  const [logs, setLogs] = useState<OptimizationLog[]>([
    {
      id: 'OPT-INIT-01',
      timestamp: '12:35:00',
      intersectionId: 'INT-DEL-02',
      intersectionName: 'AIIMS Ring Road Flyover Junction',
      previousTimings: { NORTH: 25, SOUTH: 25, EAST: 25, WEST: 25 },
      recommendedTimings: { NORTH: 42, SOUTH: 46, EAST: 32, WEST: 28 },
      rationale: 'Heavy north-south corridor surge cleared via dynamic 46s green allocation.',
      estimatedDelayReductionSec: 24,
      estimatedFuelSavedLiters: 4.8,
    },
  ]);

  const [activities, setActivities] = useState<ActivityItem[]>([
    {
      id: 'ACT-001',
      timestamp: '12:45:00',
      type: 'SIM',
      message: 'Traffic simulation engine initialized in Delhi NCR test grid.',
      level: 'info',
    },
  ]);

  const [isSimulating, setIsSimulating] = useState<boolean>(true);
  const [activeTab, setActiveTab] = useState<string>('overview');
  const [selectedIntersectionId, setSelectedIntersectionId] = useState<string | null>(null);
  const [demoStep, setDemoStep] = useState<number | null>(null);
  const [demoActive, setDemoActive] = useState<boolean>(false);
  const [emergencyProposal, setEmergencyProposal] = useState<EmergencyRouteProposal | null>(null);
  const [toastMessage, setToastMessage] = useState<{ text: string; type: 'info' | 'success' | 'warning' | 'alert' } | null>(null);

  // Sync to localStorage
  useEffect(() => {
    try {
      localStorage.setItem(LOCAL_STORAGE_KEY_INTERSECTIONS, JSON.stringify(intersections));
    } catch {
      // ignore
    }
  }, [intersections]);

  useEffect(() => {
    try {
      localStorage.setItem(LOCAL_STORAGE_KEY_SETTINGS, JSON.stringify(settings));
    } catch {
      // ignore
    }
  }, [settings]);

  useEffect(() => {
    try {
      localStorage.setItem(LOCAL_STORAGE_KEY_ALERTS, JSON.stringify(alerts.slice(0, 30)));
    } catch {
      // ignore
    }
  }, [alerts]);

  const showToast = useCallback((text: string, type: 'info' | 'success' | 'warning' | 'alert' = 'info') => {
    setToastMessage({ text, type });
    setTimeout(() => {
      setToastMessage((curr) => (curr?.text === text ? null : curr));
    }, 4500);
  }, []);

  // Main simulation timer hook
  const timerRef = useRef<number | null>(null);

  useEffect(() => {
    if (!isSimulating) {
      if (timerRef.current) {
        clearInterval(timerRef.current);
        timerRef.current = null;
      }
      return;
    }

    const intervalMs = Math.max(400, Math.round(settings.updateIntervalMs / settings.simulationSpeed));

    timerRef.current = window.setInterval(() => {
      setIntersections((currIntersections) => {
        setAlerts((currAlerts) => {
          setVehicles((currVehicles) => {
            const { updatedIntersections, updatedAlerts, updatedVehicles, newActivities } = simulateTick(
              currIntersections,
              currAlerts,
              currVehicles,
              settings,
              Math.max(1, Math.round(settings.simulationSpeed * 1.5))
            );

            if (newActivities.length > 0) {
              setActivities((currAct) => [...newActivities, ...currAct].slice(0, 50));
            }

            return updatedVehicles;
          });
          return currAlerts;
        });
        return currIntersections;
      });
    }, intervalMs);

    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
        timerRef.current = null;
      }
    };
  }, [isSimulating, settings.updateIntervalMs, settings.simulationSpeed, settings]);

  // Simulation controls
  const startSimulation = () => {
    setIsSimulating(true);
    showToast('Simulation started: Live traffic data updating.', 'info');
  };

  const pauseSimulation = () => {
    setIsSimulating(false);
    showToast('Simulation paused. Signal countdowns halted.', 'warning');
  };

  const resumeSimulation = () => {
    setIsSimulating(true);
    showToast('Simulation resumed.', 'info');
  };

  const resetSimulation = () => {
    setIsSimulating(false);
    setIntersections(INITIAL_INTERSECTIONS);
    setAlerts(INITIAL_ALERTS);
    setVehicles(INITIAL_VEHICLES);
    setEmergencyProposal(null);
    setDemoActive(false);
    setDemoStep(null);
    localStorage.removeItem(LOCAL_STORAGE_KEY_INTERSECTIONS);
    localStorage.removeItem(LOCAL_STORAGE_KEY_ALERTS);
    showToast('Simulation data reset to default test grid baseline.', 'info');
  };

  const setSimulationSpeed = (speed: number) => {
    setSettings((prev) => ({ ...prev, simulationSpeed: speed }));
    showToast(`Simulation rate set to ${speed}x speed.`, 'info');
  };

  // Traffic density injection
  const addTrafficToIntersection = (id: string, count = 25) => {
    setIntersections((prev) =>
      prev.map((item) => {
        if (item.id === id) {
          const addPerDir = Math.ceil(count / 4);
          const newCounts = {
            NORTH: item.directionalCounts.NORTH + addPerDir,
            SOUTH: item.directionalCounts.SOUTH + addPerDir,
            EAST: item.directionalCounts.EAST + addPerDir,
            WEST: item.directionalCounts.WEST + addPerDir,
          };
          const total = newCounts.NORTH + newCounts.SOUTH + newCounts.EAST + newCounts.WEST;
          const capacity = settings.maxVehicleCapacityPerIntersection || 200;
          const density = Math.min(100, Math.round((total / capacity) * 100));

          return {
            ...item,
            directionalCounts: newCounts,
            totalVehicles: total,
            densityPercentage: density,
            congestionLevel: density >= 80 ? 'CRITICAL' : density >= 60 ? 'HIGH' : 'MODERATE',
            averageWaitTimeSec: Math.round(item.averageWaitTimeSec + count * 0.45),
          };
        }
        return item;
      })
    );
    showToast(`Added +${count} vehicles to intersection queue.`, 'warning');
  };

  // Adaptive optimization execution
  const runOptimization = (id: string) => {
    setIntersections((prev) => {
      return prev.map((item) => {
        if (item.id === id) {
          const { updatedIntersection, log } = optimizeIntersection(item, settings);
          setLogs((prevLogs) => [log, ...prevLogs]);
          setActivities((prevAct) => [
            {
              id: `ACT-${Date.now()}`,
              timestamp: new Date().toLocaleTimeString(),
              type: 'OPTIMIZE',
              message: `Adaptive algorithm optimized timings for ${item.name}. Delay reduced by ~${log.estimatedDelayReductionSec}s.`,
              level: 'success',
            },
            ...prevAct,
          ]);
          showToast(`Optimized ${item.name}: Timings adjusted to clear directional queues.`, 'success');
          return updatedIntersection;
        }
        return item;
      });
    });
  };

  const runAllOptimizations = () => {
    let totalDelaySaved = 0;
    setIntersections((prev) => {
      return prev.map((item) => {
        const { updatedIntersection, log } = optimizeIntersection(item, settings);
        setLogs((prevLogs) => [log, ...prevLogs]);
        totalDelaySaved += log.estimatedDelayReductionSec;
        return updatedIntersection;
      });
    });
    showToast(`Adaptive network optimization complete! Estimated delay reduction: ${totalDelaySaved}s total.`, 'success');
  };

  // Manual signal override
  const setManualOverride = (id: string, direction: Direction, color: SignalColor, enabled: boolean) => {
    setIntersections((prev) =>
      prev.map((item) => {
        if (item.id === id) {
          return {
            ...item,
            signal: {
              ...item.signal,
              isManualOverride: enabled,
              activeDirection: direction,
              color: color,
              overrideColor: enabled ? color : undefined,
              overrideDirection: enabled ? direction : undefined,
            },
          };
        }
        return item;
      })
    );

    const target = intersections.find((i) => i.id === id);
    const msg = enabled
      ? `Manual override ACTIVE at ${target?.name || id}: Forced ${color} on ${direction}.`
      : `Manual override DISABLED at ${target?.name || id}. Restored automated cycle.`;

    setActivities((prev) => [
      {
        id: `ACT-${Date.now()}`,
        timestamp: new Date().toLocaleTimeString(),
        type: 'OVERRIDE',
        message: msg,
        level: enabled ? 'warning' : 'info',
      },
      ...prev,
    ]);

    showToast(msg, enabled ? 'warning' : 'info');
  };

  // Alerts management
  const acknowledgeAlert = (alertId: string) => {
    setAlerts((prev) =>
      prev.map((a) => (a.id === alertId ? { ...a, status: 'ACKNOWLEDGED', acknowledgedBy: 'Traffic Administrator' } : a))
    );
    showToast(`Alert #${alertId} acknowledged.`, 'info');
  };

  const resolveAlert = (alertId: string) => {
    setAlerts((prev) =>
      prev.map((a) => (a.id === alertId ? { ...a, status: 'RESOLVED', resolvedAt: new Date().toLocaleTimeString() } : a))
    );
    showToast(`Alert #${alertId} marked as resolved.`, 'success');
  };

  const clearResolvedAlerts = () => {
    setAlerts((prev) => prev.filter((a) => a.status !== 'RESOLVED'));
    showToast('Resolved alerts purged from active list.', 'info');
  };

  // Intersection CRUD
  const addIntersection = (newInt: Partial<Intersection>) => {
    const id = `INT-DEL-0${intersections.length + 1}`;
    const fullInt: Intersection = {
      id,
      name: newInt.name || `New Junction ${id}`,
      locationArea: newInt.locationArea || 'Delhi NCR Region',
      coordinates: newInt.coordinates || [28.6139, 77.2090],
      directionalCounts: newInt.directionalCounts || { NORTH: 20, SOUTH: 20, EAST: 20, WEST: 20 },
      totalVehicles: 80,
      densityPercentage: 36,
      congestionLevel: 'MODERATE',
      averageWaitTimeSec: 30,
      signal: {
        activeDirection: 'NORTH',
        color: 'GREEN',
        timeRemainingSec: 25,
        timings: { NORTH: 25, SOUTH: 25, EAST: 25, WEST: 25 },
        isManualOverride: false,
        yellowDurationSec: 4,
        cycleTotalSec: 100,
      },
      history: [
        { timeStr: '12:00', vehicleCount: 75, density: 34, avgWaitSec: 28 },
        { timeStr: '12:15', vehicleCount: 80, density: 36, avgWaitSec: 30 },
      ],
      cameraStatus: 'ONLINE',
      sensorStatus: 'ACTIVE_SIMULATED',
    };

    setIntersections((prev) => [...prev, fullInt]);
    showToast(`Intersection "${fullInt.name}" added to surveillance network.`, 'success');
  };

  const updateIntersection = (id: string, updates: Partial<Intersection>) => {
    setIntersections((prev) => prev.map((item) => (item.id === id ? { ...item, ...updates } : item)));
    showToast(`Intersection details updated.`, 'info');
  };

  const deleteIntersection = (id: string) => {
    setIntersections((prev) => prev.filter((item) => item.id !== id));
    showToast(`Intersection removed from monitoring network.`, 'warning');
  };

  // Vehicle additions
  const addVehicle = (veh: Partial<Vehicle>) => {
    const newVeh: Vehicle = {
      id: `VEH-${Date.now().toString().slice(-4)}`,
      category: veh.category || 'Car',
      intersectionId: veh.intersectionId || intersections[0]?.id || 'INT-DEL-01',
      intersectionName:
        intersections.find((i) => i.id === veh.intersectionId)?.name || intersections[0]?.name || 'Central Crossing',
      direction: veh.direction || 'NORTH',
      speedKmph: veh.speedKmph || 40,
      estimatedArrivalSec: veh.estimatedArrivalSec || 15,
      status: 'Approaching',
      isEmergency: veh.category === 'Emergency',
      licensePlate: veh.licensePlate || `DL-01-${Math.floor(1000 + Math.random() * 9000)}`,
    };

    setVehicles((prev) => [newVeh, ...prev]);
    showToast(`Simulated vehicle ${newVeh.licensePlate} (${newVeh.category}) inserted.`, 'info');
  };

  // Emergency Vehicle Priority Demonstration
  const dispatchEmergencyVehicle = (
    category: 'Ambulance' | 'Fire Engine' | 'Police Cruiser',
    targetId: string,
    direction: Direction
  ) => {
    const target = intersections.find((i) => i.id === targetId) || intersections[0];
    const plate = category === 'Ambulance' ? 'DL-01-AMB-911' : category === 'Fire Engine' ? 'DL-01-FIRE-101' : 'DL-01-POL-100';

    const proposal: EmergencyRouteProposal = {
      id: `EMG-${Date.now()}`,
      timestamp: new Date().toLocaleTimeString(),
      vehiclePlate: plate,
      category,
      targetIntersectionId: target.id,
      targetIntersectionName: target.name,
      priorityDirection: direction,
      greenWaveDurationSec: 45,
      status: 'ACTIVE_GREEN',
    };

    setEmergencyProposal(proposal);

    // Force green wave on the target intersection in that direction
    setIntersections((prev) =>
      prev.map((item) => {
        if (item.id === target.id) {
          return {
            ...item,
            signal: {
              ...item.signal,
              activeDirection: direction,
              color: 'GREEN',
              timeRemainingSec: 45,
              isManualOverride: true,
              overrideColor: 'GREEN',
              overrideDirection: direction,
            },
          };
        }
        return item;
      })
    );

    // Create high-visibility emergency alert
    const emgAlert: TrafficAlert = {
      id: `ALT-EMG-${Date.now()}`,
      intersectionId: target.id,
      intersectionName: target.name,
      type: 'EMERGENCY_PRIORITY',
      severity: 'CRITICAL',
      description: `EMERGENCY VEHICLE PRIORITY: ${category} (${plate}) approaching from ${direction}. Green wave granted at ${target.name}.`,
      timestamp: new Date().toLocaleTimeString(),
      status: 'ACTIVE',
    };

    setAlerts((prev) => [emgAlert, ...prev]);

    setActivities((prev) => [
      {
        id: `ACT-${Date.now()}`,
        timestamp: new Date().toLocaleTimeString(),
        type: 'EMERGENCY',
        message: `Emergency Signal Preemption: Green wave activated for ${category} approaching ${target.name} [${direction} corridor].`,
        level: 'alert',
      },
      ...prev,
    ]);

    showToast(`EMERGENCY DISPATCH: Green light preemption granted at ${target.name}!`, 'alert');
  };

  const clearEmergencyRoute = () => {
    if (emergencyProposal) {
      // Restore normal automatic mode to the intersection
      setIntersections((prev) =>
        prev.map((item) => {
          if (item.id === emergencyProposal.targetIntersectionId) {
            return {
              ...item,
              signal: {
                ...item.signal,
                isManualOverride: false,
              },
            };
          }
          return item;
        })
      );
    }
    setEmergencyProposal(null);
    showToast('Emergency green corridor cleared; normal traffic schedule restored.', 'info');
  };

  // Settings
  const updateSettings = (newSettings: Partial<SystemSettings>) => {
    setSettings((prev) => ({ ...prev, ...newSettings }));
    showToast('System configuration settings updated.', 'success');
  };

  const resetSettingsToDefault = () => {
    setSettings(DEFAULT_SETTINGS);
    localStorage.removeItem(LOCAL_STORAGE_KEY_SETTINGS);
    showToast('Configuration restored to factory standard.', 'info');
  };

  // Interactive 10-step Demo Scenario Runner for Faculty Presentation
  const demoIntervalRef = useRef<number | null>(null);

  const runDemoScenario = () => {
    setDemoActive(true);
    setDemoStep(1);
    setActiveTab('overview');
    showToast('Starting 10-Step College PBL Project Demonstration Scenario...', 'info');

    // Step 1: Normal traffic across all intersections
    setIntersections(INITIAL_INTERSECTIONS);

    // Step 2 (3s later): Surge traffic at AIIMS (INT-DEL-02)
    setTimeout(() => {
      setDemoStep(2);
      addTrafficToIntersection('INT-DEL-02', 60);
      showToast('Step 2/10: Artificial vehicle surge injected at AIIMS Junction (+60 vehicles).', 'warning');
    }, 3500);

    // Step 3 (7s later): Density crosses High/Critical threshold
    setTimeout(() => {
      setDemoStep(3);
      showToast('Step 3/10: Traffic Density escalated to 94% (Congestion state: CRITICAL).', 'warning');
    }, 7000);

    // Step 4 (10s later): Switch to Map View to demonstrate marker turning Red
    setTimeout(() => {
      setDemoStep(4);
      setActiveTab('map');
      setSelectedIntersectionId('INT-DEL-02');
      showToast('Step 4/10: Map shows AIIMS Junction highlighted in RED with critical queue marker.', 'info');
    }, 10500);

    // Step 5 (14s later): Switch to Alerts view showing congestion alert
    setTimeout(() => {
      setDemoStep(5);
      setActiveTab('alerts');
      showToast('Step 5/10: Automated Congestion Alert generated and queued with alert cooldown.', 'alert');
    }, 14000);

    // Step 6 (18s later): Switch to Signal Control & execute adaptive optimization
    setTimeout(() => {
      setDemoStep(6);
      setActiveTab('signals');
      setSelectedIntersectionId('INT-DEL-02');
      showToast('Step 6/10: Running real-time Adaptive Optimization algorithm on AIIMS.', 'info');
    }, 18000);

    // Step 7 (22s later): Apply recommended timings
    setTimeout(() => {
      setDemoStep(7);
      runOptimization('INT-DEL-02');
      showToast('Step 7/10: Timings dynamically recalculated: Green phase extended to clear backlog.', 'success');
    }, 22000);

    // Step 8 (26s later): Observe traffic clearance
    setTimeout(() => {
      setDemoStep(8);
      showToast('Step 8/10: Adaptive green phase dissipates waiting queue; density dropping.', 'info');
    }, 26000);

    // Step 9 (30s later): Emergency vehicle priority demonstration
    setTimeout(() => {
      setDemoStep(9);
      dispatchEmergencyVehicle('Ambulance', 'INT-DEL-02', 'NORTH');
      showToast('Step 9/10: Emergency vehicle detected! Preemption green wave granted immediately.', 'alert');
    }, 30000);

    // Step 10 (35s later): Return to Overview / Analytics showing fuel savings and metrics
    setTimeout(() => {
      setDemoStep(10);
      setActiveTab('overview');
      showToast('Step 10/10: Demonstration complete! System performance metrics and logs updated.', 'success');
    }, 35000);
  };

  const resetDemoScenario = () => {
    if (demoIntervalRef.current) {
      clearInterval(demoIntervalRef.current);
    }
    setDemoActive(false);
    setDemoStep(null);
    resetSimulation();
  };

  return (
    <TrafficContext.Provider
      value={{
        intersections,
        alerts,
        vehicles,
        settings,
        logs,
        activities,
        isSimulating,
        activeTab,
        selectedIntersectionId,
        demoStep,
        demoActive,
        emergencyProposal,
        toastMessage,
        setActiveTab,
        setSelectedIntersectionId,
        startSimulation,
        pauseSimulation,
        resumeSimulation,
        resetSimulation,
        setSimulationSpeed,
        addTrafficToIntersection,
        runOptimization,
        runAllOptimizations,
        setManualOverride,
        acknowledgeAlert,
        resolveAlert,
        clearResolvedAlerts,
        addIntersection,
        updateIntersection,
        deleteIntersection,
        addVehicle,
        dispatchEmergencyVehicle,
        clearEmergencyRoute,
        updateSettings,
        resetSettingsToDefault,
        runDemoScenario,
        resetDemoScenario,
        showToast,
      }}
    >
      {children}
    </TrafficContext.Provider>
  );
};

export const useTraffic = () => {
  const context = useContext(TrafficContext);
  if (!context) {
    throw new Error('useTraffic must be used within a TrafficProvider');
  }
  return context;
};
