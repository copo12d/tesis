import '../styles/dashboard.css';
import { Carta } from '../components/Carta';
import Grafica_semanal from '../components/Grafica_semanal';
import { useDashboard } from '../hooks/useDashboard';


const Dashboard = () => {
  const { dashboardData, error, refetch } = useDashboard();

  return (
    <div className="dashboard-layout" >
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
