import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/resgister.css';

export function Register() {
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      // Aquí va tu lógica real de registro
      // const res = await api.post('/register', { userName, password });
      // if (res.data.success) {
      //   navigate('/login');
      // } else {
      //   setError('No se pudo registrar');
      // }
      setLoading(false);
    } catch (err) {
      setError(`Error al registrar: ${err.message}`);
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      <div className="register-logo">
        <span className="logo-icon">🟪</span>
        <span className="logo-text">LOGO</span>
      </div>
      <form className="register-form" onSubmit={handleSubmit}>
        <h2>Registro</h2>
        <p className="register-subtitle">Crea tu cuenta</p>

        <label className="register-label">Usuario</label>
        <div className="register-input-group">
          <span className="register-input-icon">👤</span>
          <input
            type="text"
            className="register-input"
            value={userName}
            onChange={e => setUserName(e.target.value)}
            placeholder="username123"
            required
          />
        </div>

        <label className="register-label">Contraseña</label>
        <div className="register-input-group">
          <span className="register-input-icon">🔒</span>
          <input
            type="password"
            className="register-input"
            value={password}
            onChange={e => setPassword(e.target.value)}
            placeholder="************"
            required
          />
        </div>

        <button className="register-btn" type="submit" disabled={loading}>
          {loading ? "Registrando..." : "Registrarse"}
        </button>
        {error && <div className="register-error">{error}</div>}
      </form>
      <div className="register-login">
        <span>¿Ya tienes una cuenta?</span>
        <a href="/login">Iniciar sesión</a>
      </div>
    </div>
  );
}