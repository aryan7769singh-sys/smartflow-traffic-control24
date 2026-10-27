import React from 'react';
import { TrafficProvider, useTraffic } from './context/TrafficContext';
import { Navbar } from './components/layout/Navbar';
import { Sidebar } from './components/layout/Sidebar';
import { DemoBanner } from './components/common/DemoBanner';
import { Toast } from './components/common/Toast';
import { OverviewPage } from './pages/OverviewPage';
import { MapPage } from './pages/MapPage';
import { IntersectionsPage } from './pages/IntersectionsPage';
import { SignalsPage } from './pages/SignalsPage';
import { AlertsPage } from './pages/AlertsPage';
import { AnalyticsPage } from './pages/AnalyticsPage';
import { VehiclesPage } from './pages/VehiclesPage';
import { SettingsPage } from './pages/SettingsPage';

const AppContent: React.FC = () => {
  const { activeTab } = useTraffic();

  const renderActiveTab = () => {
    switch (activeTab) {
      case 'overview':
        return <OverviewPage />;
      case 'map':
        return <MapPage />;
      case 'intersections':
        return <IntersectionsPage />;
      case 'signals':
        return <SignalsPage />;
      case 'alerts':
        return <AlertsPage />;
      case 'analytics':
        return <AnalyticsPage />;
      case 'vehicles':
        return <VehiclesPage />;
      case 'settings':
        return <SettingsPage />;
      default:
        return <OverviewPage />;
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-[#0B1120] text-slate-100">
      {/* Top Navbar */}
      <Navbar />

      {/* Demo Scenario Banner */}
      <DemoBanner />

      {/* Main Body: Sidebar + Dynamic Page Content */}
      <div className="flex-1 flex overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-y-auto bg-[#0B1120]">
          {renderActiveTab()}
        </main>
      </div>

      {/* Global Interactive Toast Notification */}
      <Toast />
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <TrafficProvider>
      <AppContent />
    </TrafficProvider>
  );
};

export default App;
