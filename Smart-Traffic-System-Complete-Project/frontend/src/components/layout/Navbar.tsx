import React, { useState, useEffect } from 'react';
import { useTraffic } from '../../context/TrafficContext';
import {
  Activity,
  Bell,
  Play,
  Pause,
  RotateCcw,
  ShieldCheck,
  User,
  Zap,
} from 'lucide-react';

export const Navbar: React.FC = () => {
  const {
    isSimulating,
    startSimulation,
    pauseSimulation,
    resetSimulation,
    settings,
    setSimulationSpeed,
    alerts,
    setActiveTab,
  } = useTraffic();

  const [currentTime, setCurrentTime] = useState<string>('');

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      setCurrentTime(
        now.toLocaleDateString('en-IN', {
          weekday: 'short',
          month: 'short',
          day: 'numeric',
        }) +
          ' ' +
          now.toLocaleTimeString('en-IN', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
          })
      );
    };
    updateTime();
    const interval = setInterval(updateTime, 1000);
    return () => clearInterval(interval);
  }, []);

  const activeAlertCount = alerts.filter((a) => a.status === 'ACTIVE').length;

  return (
    <header className="h-16 bg-[#0E1626]/90 backdrop-blur-md border-b border-slate-800/80 px-6 flex items-center justify-between sticky top-0 z-40 select-none">
      {/* Brand & System Status */}
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-blue-600 via-indigo-600 to-emerald-400 p-0.5 shadow-lg shadow-blue-500/20 flex items-center justify-center">
            <div className="w-full h-full bg-[#0E1626] rounded-[10px] flex items-center justify-center">
              <Zap className="w-5 h-5 text-blue-400" />
            </div>
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="font-bold text-slate-100 text-base tracking-tight leading-none">
                SmartFlow Traffic Control
              </h1>
              <span className="text-[10px] uppercase font-mono font-bold px-1.5 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                Simulated Grid
              </span>
            </div>
            <p className="text-[11px] text-slate-400 font-mono mt-0.5">
              Delhi NCR Intelligent Corridor Surveillance
            </p>
          </div>
        </div>

        <div className="h-6 w-[1px] bg-slate-800 hidden lg:block" />

        {/* Live Status indicator */}
        <div className="hidden lg:flex items-center gap-2 px-2.5 py-1 rounded-full bg-slate-900/60 border border-slate-800 text-xs">
          <span className="relative flex h-2 w-2">
            <span
              className={`animate-ping absolute inline-flex h-full w-full rounded-full opacity-75 ${
                isSimulating ? 'bg-emerald-400' : 'bg-amber-400'
              }`}
            />
            <span
              className={`relative inline-flex rounded-full h-2 w-2 ${
                isSimulating ? 'bg-emerald-500' : 'bg-amber-500'
              }`}
            />
          </span>
          <span className="text-slate-300 font-medium text-[11px]">
            {isSimulating ? 'Engine Live (2.0s Ticks)' : 'Simulation Halted'}
          </span>
        </div>
      </div>

      {/* Middle: Simulation Quick Controls */}
      <div className="flex items-center gap-3">
        <div className="hidden sm:flex items-center bg-slate-900/90 border border-slate-800 rounded-lg p-1">
          {isSimulating ? (
            <button
              onClick={pauseSimulation}
              className="flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold text-amber-300 hover:bg-amber-500/10 transition"
              title="Pause traffic simulation ticks"
            >
              <Pause className="w-3.5 h-3.5" />
              Pause
            </button>
          ) : (
            <button
              onClick={startSimulation}
              className="flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold text-emerald-300 hover:bg-emerald-500/10 transition"
              title="Resume traffic simulation ticks"
            >
              <Play className="w-3.5 h-3.5" />
              Resume
            </button>
          )}

          <div className="h-4 w-[1px] bg-slate-800 mx-1" />

          {/* Speed Selector */}
          <div className="flex items-center gap-1 px-1">
            {[0.5, 1, 2, 5].map((speed) => (
              <button
                key={speed}
                onClick={() => setSimulationSpeed(speed)}
                className={`px-2 py-0.5 text-[11px] font-mono rounded transition ${
                  settings.simulationSpeed === speed
                    ? 'bg-blue-600 text-white font-bold'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                {speed}x
              </button>
            ))}
          </div>

          <div className="h-4 w-[1px] bg-slate-800 mx-1" />

          <button
            onClick={resetSimulation}
            className="p-1.5 text-slate-400 hover:text-rose-400 hover:bg-slate-800/80 rounded transition"
            title="Reset Simulation to Initial Baseline"
          >
            <RotateCcw className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>

      {/* Right: Clock, Alerts & Profile */}
      <div className="flex items-center gap-4">
        {/* Clock */}
        <div className="hidden md:flex flex-col text-right">
          <span className="text-xs font-mono font-medium text-slate-200">
            {currentTime}
          </span>
          <span className="text-[10px] text-slate-500 uppercase tracking-wider font-mono">
            IST (UTC+05:30)
          </span>
        </div>

        {/* Notifications Icon */}
        <button
          onClick={() => setActiveTab('alerts')}
          className="relative p-2 rounded-lg bg-slate-900/60 border border-slate-800 hover:border-slate-700 text-slate-300 hover:text-white transition"
          title="View Congestion Alerts"
        >
          <Bell className="w-4 h-4" />
          {activeAlertCount > 0 && (
            <span className="absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white shadow-lg shadow-red-500/50">
              {activeAlertCount}
            </span>
          )}
        </button>

        {/* User Profile */}
        <div className="flex items-center gap-2 pl-2 border-l border-slate-800">
          <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white text-xs font-bold ring-2 ring-blue-500/20">
            TA
          </div>
          <div className="hidden xl:flex flex-col">
            <span className="text-xs font-semibold text-slate-200 leading-tight">
              Traffic Administrator
            </span>
            <span className="text-[10px] text-emerald-400 font-medium">
              Delhi Police / MCD
            </span>
          </div>
        </div>
      </div>
    </header>
  );
};
