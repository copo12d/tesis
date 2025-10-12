import '../styles/dashboard.css';
import { Carta } from '../components/Carta';
import Grafica_semanal from '../components/Grafica_semanal';
import { useDashboard } from '../hooks/useDashboard';

const Dashboard = () => {
  const { dashboardData, error, refetch } = useDashboard();

  // Asegúrate de que dashboardData.cardsData tenga al menos 3 elementos
  const card1 = dashboardData.cardsData[0] || { value: 0 };
  const card2 = dashboardData.cardsData[1] || { value: 0 };
  const card3 = dashboardData.cardsData[2] || { value: 0 };

  return (
    <div className="dashboard-layout">
      <main className="dashboard-main">
        <h2 className="dashboard-title">Dashboard</h2>
        <div className="dashboard-row">
          <div className="dashboard-card">
            <Carta
              value={112}
              title="Contenedores activos"
              graficaData={[
                { value: 110, month: "Jan" },
                { value: 120, month: "Feb" },
                { value: 105, month: "Mar" },
                { value: 98, month: "Apr" },
                { value: 115, month: "May" },
                { value: 112, month: "Jun" },
              ]}
            />
          </div>
          <div className="dashboard-card">
            <Carta value={card2.value} title="Cantidad de usuarios" />
          </div>
          <div className="dashboard-card">
            <Carta value={card3.value} title="Cantidad de desechos" />
          </div>
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
