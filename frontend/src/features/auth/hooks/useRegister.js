import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { AuthAPI } from '../api/auth.api'; 


export const useRegister = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const register = async (fullName, userName, password, email) => {
    setLoading(true);
    setError('');
    
    try {
      const res = await AuthAPI.register(fullName, userName, password, email);

      if (res.data.meta?.status === 201) {
        const successMsg = res.data.meta?.message || '¡Registro exitoso!';
        toast.success(successMsg);
        navigate('/login');
        return { success: true, message: successMsg };
      } else {
        // Extrae el mensaje de error de la API si existe
        const apiErrorMsg =
          res.data.errors?.[0]?.message ||
          res.data.meta?.message ||
          'No se pudo registrar';
        setError(apiErrorMsg);
        toast.error(apiErrorMsg);
        return { success: false, error: apiErrorMsg };
      }
    } catch (err) {
      // Extrae el mensaje de error del catch si existe
      const errorMessage =
        err.response?.data?.errors?.[0]?.message ||
        err.response?.data?.meta?.message ||
        err.response?.data?.error ||
        'Error al registrar';
      setError(errorMessage);
      toast.error(errorMessage);
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  };

  return { register, loading, error };
};