import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import AuthContext from '../context/Authcontext';
import { useContext } from 'react';
import '../styles/login.css'; 
import { Navigate } from 'react-router-dom';

export function Login() {
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');

  const { loginRequest, loading, error } = useAuth();
  const { login } = useContext(AuthContext);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await loginRequest(userName, password);
    if (result.success) {
      login(result.accessToken, result.refreshToken);
      Navigate('/');
    }
  };

  return (
    <div className="login-container">
      <div className="login-logo">
        <span className="logo-icon">🟪</span>
        <span className="logo-text">LOGO</span>
      </div>
      <form className="login-form" onSubmit={handleSubmit}>
        <h2>Iniciar Sesión</h2>
        <p className="login-subtitle">Accede a tu cuenta</p>

        <label className="login-label">Nombre de usuario</label>
        <div className="login-input-group">
          <span className="login-input-icon">👤</span>
          <input
            type="text"
            className="login-input"
            value={userName}
            onChange={e => setUserName(e.target.value)}
            placeholder="username123"
            required
          />
        </div>

        <label className="login-label">Contraseña</label>
        <div className="login-input-group">
          <span className="login-input-icon">🔒</span>
          <input
            type="password"
            className="login-input"
            value={password}
            onChange={e => setPassword(e.target.value)}
            placeholder="************"
            required
          />
        </div>

        <div className="login-forgot">
          <a href="#">¿Olvidaste tu contraseña?</a>
        </div>

        <button className="login-btn" type="submit" disabled={loading}>
          {loading ? "Ingresando..." : "Ingresar"}
        </button>
        {error && <div className="login-error">{error}</div>}
      </form>
      <div className="login-register">
        <span>No tengo una cuenta</span>
        <a href="/register">Registrarme</a>
      </div>
    </div>
  );
}