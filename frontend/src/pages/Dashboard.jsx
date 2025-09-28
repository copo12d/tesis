import '../styles/dashboard.css';
import { Carta } from '../components/Carta';
import Grafica_semanal from '../components/Grafica_semanal';
import { useDashboard } from '../hooks/useDashboard';
import { AdminSection } from '../components/AdminSection';
import { useContext } from 'react';
import AuthContext from '../context/Authcontext';
import Sidebar from '../components/SideBar';


const Dashboard = () => {
  const { dashboardData, error, refetch } = useDashboard();
  const { user, logout } = useContext(AuthContext);

  return (
    <div className="dashboard-layout" style={{ display: 'flex' }}>
      <Sidebar />
      <main className="dashboard-main">
        <h2 className="dashboard-title">Dashboard</h2>
        <div className="dashboard-row">
          {dashboardData.cardsData.map((card, index) => (
            <div key={card.id || index} className="dashboard-card">
              <Carta value={card.value} />
            </div>
          ))}
        </div>
        <div className="dashboard-row">
          <div className="dashboard-card dashboard-card-large">
            <Grafica_semanal data={dashboardData.weeklyData} />
          </div>
        </div>
      </main>
    </div>
  );
};

export { Dashboard };
