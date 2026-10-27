import React from 'react';
import { useTraffic } from '../../context/TrafficContext';
import { PlayCircle, CheckCircle2, RotateCcw, Sparkles } from 'lucide-react';

const DEMO_STEPS = [
  'Baseline Network Monitoring',
  'Simulated Traffic Surge at AIIMS',
  'Density Escalates to High/Critical',
  'Live Map Marker Visual Transition',
  'Automated Congestion Alert Dispatch',
  'Dynamic Adaptive Optimization Trigger',
  'Green Phase Timings Recalculation',
  'Queue Dissipation & Delay Reduction',
  'Emergency Vehicle Preemption Wave',
  'Final Operational Audit & Analytics',
];

export const DemoBanner: React.FC = () => {
  const { demoActive, demoStep, runDemoScenario, resetDemoScenario } = useTraffic();

  return (
    <div className="bg-gradient-to-r from-blue-900/40 via-indigo-900/30 to-purple-900/40 border-b border-blue-500/20 px-6 py-2.5 flex flex-wrap items-center justify-between gap-4">
      <div className="flex items-center gap-3">
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-blue-500/20 text-blue-300 border border-blue-500/30">
          <Sparkles className="w-3.5 h-3.5 text-blue-400" />
          PBL Project Demo Mode
        </span>
        <span className="text-xs text-slate-300 hidden md:inline">
          Interactive 10-step sequence demonstrating simulated sensor inputs, real-time optimization, and emergency priority.
        </span>
      </div>

      <div className="flex items-center gap-3">
        {demoActive && demoStep !== null ? (
          <div className="flex items-center gap-2 bg-slate-900/80 px-3 py-1 rounded-lg border border-blue-500/40">
            <span className="w-2 h-2 rounded-full bg-blue-400 animate-ping" />
            <span className="text-xs font-mono text-blue-300 font-semibold">
              Step {demoStep}/10:
            </span>
            <span className="text-xs text-slate-200 font-medium">
              {DEMO_STEPS[demoStep - 1] || 'Scenario Execution'}
            </span>
            <button
              onClick={resetDemoScenario}
              className="ml-2 p-1 hover:bg-slate-800 rounded text-slate-400 hover:text-white transition"
              title="Stop & Reset Demo"
            >
              <RotateCcw className="w-3.5 h-3.5" />
            </button>
          </div>
        ) : (
          <button
            onClick={runDemoScenario}
            className="flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs font-semibold bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white shadow-lg shadow-blue-600/25 transition active:scale-95"
          >
            <PlayCircle className="w-4 h-4" />
            Run Faculty Demo Scenario (5-Min Flow)
          </button>
        )}
      </div>
    </div>
  );
};
