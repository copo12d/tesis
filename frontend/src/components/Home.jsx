export function Home({ onLogout }) {
  return (
    <div>
      <h2>Bienvenido</h2>
      <button onClick={onLogout}>Cerrar sesión</button>
    </div>
  );
}