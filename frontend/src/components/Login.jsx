import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';

export function Login({ onLogin }) {
    const [userName, setUserName] = useState('');
    const [password, setPassword] = useState('');

    const { login, loading, error } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        const result = await login(userName, password);

        if (result.success) {
            onLogin(result.token);
        }

   }

   return (
    <form onSubmit={handleSubmit}>
      <h2>Iniciar Sesión</h2>
      
      <input
        type="text"
        placeholder="Usuario"
        value={userName}
        onChange={e => setUserName(e.target.value)}
        required
      />
      
      <input
        type="password"
        placeholder="Contraseña"
        value={password}
        onChange={e => setPassword(e.target.value)}
        required
      />
      
      <button type="submit" disabled={loading}>
        {loading ? 'Entrando...' : 'Entrar'}
      </button>
      
      <button type="button" onClick={() => window.location.href = '/register'}>
        ¿No tienes cuenta? Regístrate
      </button>
    </form>
  );
    

 
    
};