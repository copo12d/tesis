import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { AuthAPI } from '../api/api';

export const useRegister = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const register = async (userName, password) => {
    setLoading(true);
    setError('');
    
    try {
      const res = await AuthAPI.register(userName, password);

      if (res.data.success) {
        toast.success('¡Registro exitoso!');
        navigate('/login');
        return { success: true };
      } else {
        setError('No se pudo registrar');
        toast.error('Error en el registro');
        return { success: false };
      }
    } catch (err) {
      const errorMessage = err.response?.data?.error || 'Error al registrar';
      setError(errorMessage);
      toast.error(errorMessage);
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  };

  return { register, loading, error };
};