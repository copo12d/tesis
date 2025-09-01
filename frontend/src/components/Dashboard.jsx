import '../styles/dashboard.css';
import { Carta } from './Carta';
import Grafica_semanal from './Grafica_semanal';

export const Dashboard = () => {
   const data = [
    { day: "Sat", value: 10 },
    { day: "Sun", value: 10 },
    { day: "Mon", value: 60 },
    { day: "Tue", value: 82 },
    { day: "Wed", value: 50 },
    { day: "Thu", value: 70 },
    { day: "Fri", value: 50 }
  ];
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
          <div className="dashboard-card">
            <Carta value={75} />
          </div>
          <div className="dashboard-card">
            <Carta value={50} />
          </div>
          <div className="dashboard-card">
            <Carta value={25} />
          </div>
        </div>
        <div className="dashboard-row">
          <div className="dashboard-card dashboard-card-large"><Grafica_semanal data={data} /></div>
        </div>
      </main>
    </div>
  );
};
