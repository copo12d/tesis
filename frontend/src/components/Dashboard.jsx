import '../styles/dashboard.css';
import { Carta } from './Carta';
import Grafica_semanal from './Grafica_semanal';
import { useDashboard } from '../hooks/useDashboard';

export const Dashboard = () => {
  const { dashboardData, error, refetch } = useDashboard();

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <span className="logo-icon">🟪</span>
          <span className="logo-text">Aplicacion</span>
        </div>
        <nav className="sidebar-menu">
          <a className="sidebar-item active" href="#">
            <span className="sidebar-icon">📊</span>
            Dashboard
          </a>
          <a className="sidebar-item" href="#">Contenedores</a>
          <a className="sidebar-item" href="#">Configuración</a>
          <a className="sidebar-item" href="#">Ayuda</a>
          <a className="sidebar-item" href="#">Cerrar sesión</a>
        </nav>
      </aside>
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
