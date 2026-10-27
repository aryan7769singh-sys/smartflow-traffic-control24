import React from 'react';
import { useTraffic } from '../../context/TrafficContext';
import { AlertTriangle, CheckCircle, Info, ShieldAlert, X } from 'lucide-react';

export const Toast: React.FC = () => {
  const { toastMessage } = useTraffic();

  if (!toastMessage) return null;

  const icons = {
    info: <Info className="w-5 h-5 text-blue-400 shrink-0" />,
    success: <CheckCircle className="w-5 h-5 text-emerald-400 shrink-0" />,
    warning: <AlertTriangle className="w-5 h-5 text-amber-400 shrink-0" />,
    alert: <ShieldAlert className="w-5 h-5 text-red-400 shrink-0 animate-pulse" />,
  };

  const borders = {
    info: 'border-blue-500/30 bg-blue-950/80 text-blue-200',
    success: 'border-emerald-500/30 bg-emerald-950/80 text-emerald-200',
    warning: 'border-amber-500/30 bg-amber-950/80 text-amber-200',
    alert: 'border-red-500/40 bg-red-950/90 text-red-100 shadow-red-900/30',
  };

  return (
    <div className="fixed bottom-6 right-6 z-50 max-w-md animate-in fade-in slide-in-from-bottom-5 duration-300">
      <div
        className={`flex items-start gap-3 p-4 rounded-xl border backdrop-blur-md shadow-2xl ${
          borders[toastMessage.type]
        }`}
      >
        {icons[toastMessage.type]}
        <div className="text-sm font-medium leading-relaxed pr-2">
          {toastMessage.text}
        </div>
      </div>
    </div>
  );
};
