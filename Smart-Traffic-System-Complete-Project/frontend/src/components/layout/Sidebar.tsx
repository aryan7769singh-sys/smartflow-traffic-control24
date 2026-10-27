import React from 'react';
import { useTraffic } from '../../context/TrafficContext';
import {
  LayoutDashboard,
  MapPin,
  GitFork,
  Radio,
  AlertTriangle,
  BarChart3,
  Car,
  Settings,
  Sparkles,
  Info,
} from 'lucide-react';

interface NavItem {
  id: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: number;
}

export const Sidebar: React.FC = () => {
  const { activeTab, setActiveTab, alerts, intersections } = useTraffic();

  const activeAlertCount = alerts.filter((a) => a.status === 'ACTIVE').length;
  const congestedCount = intersections.filter(
    (i) => i.congestionLevel === 'HIGH' || i.congestionLevel === 'CRITICAL'
  ).length;

  const navItems: NavItem[] = [
    { id: 'overview', label: 'Overview Dashboard', icon: LayoutDashboard },
    { id: 'map', label: 'Live Traffic Map', icon: MapPin },
    { id: 'intersections', label: 'Intersections', icon: GitFork, badge: congestedCount },
    { id: 'signals', label: 'Traffic Signal Control', icon: Radio },
    { id: 'alerts', label: 'Congestion Alerts', icon: AlertTriangle, badge: activeAlertCount },
    { id: 'analytics', label: 'Analytics & Reports', icon: BarChart3 },
    { id: 'vehicles', label: 'Vehicle Monitoring', icon: Car },
    { id: 'settings', label: 'Settings', icon: Settings },
  ];

  return (
    <aside className="w-64 bg-[#0B1120] border-r border-slate-800/80 flex flex-col shrink-0 select-none">
      {/* Navigation Links */}
      <nav className="p-4 space-y-1.5 flex-1 overflow-y-auto">
        <div className="text-[10px] uppercase font-mono font-bold text-slate-500 px-3 pb-2 tracking-wider">
          Operations & Control
        </div>

        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;

          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl text-xs font-medium transition-all group ${
                isActive
                  ? 'bg-blue-600/15 text-blue-400 border border-blue-500/30 font-semibold shadow-inner'
                  : 'text-slate-400 hover:text-slate-100 hover:bg-slate-900/60'
              }`}
            >
              <div className="flex items-center gap-3">
                <Icon
                  className={`w-4 h-4 transition-colors ${
                    isActive ? 'text-blue-400' : 'text-slate-400 group-hover:text-slate-200'
                  }`}
                />
                <span>{item.label}</span>
              </div>

              {item.badge !== undefined && item.badge > 0 && (
                <span
                  className={`px-1.5 py-0.5 rounded-md text-[10px] font-mono font-bold ${
                    item.id === 'alerts'
                      ? 'bg-red-500/20 text-red-400 border border-red-500/30'
                      : 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                  }`}
                >
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      {/* College Project Disclaimer Card */}
      <div className="p-4 border-t border-slate-800/80 bg-[#0E1626]/40">
        <div className="p-3 rounded-xl bg-slate-900/60 border border-slate-800/80 space-y-1.5 text-left">
          <div className="flex items-center gap-1.5 text-blue-400 text-xs font-semibold">
            <Info className="w-3.5 h-3.5 shrink-0" />
            <span>PBL Academic Prototype</span>
          </div>
          <p className="text-[11px] text-slate-400 leading-relaxed">
            Course: Object Oriented Techniques Using Java. All vehicle telemetry, camera feeds & sensor readings are simulated for academic evaluation.
          </p>
        </div>
      </div>
    </aside>
  );
};
