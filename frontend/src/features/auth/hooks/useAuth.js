import { useState } from "react";
import { toast } from "react-hot-toast";
import { AuthAPI } from '../api/auth.api'

// Utilidad para extraer el mensaje de error de la API
function getApiErrorMessage(obj, fallback = "Ocurrió un error inesperado") {
  const apiErrors = obj?.errors;
  if (Array.isArray(apiErrors) && apiErrors.length > 0 && apiErrors[0].message) {
    return apiErrors[0].message;
  }
  if (obj?.meta?.message) {
    return obj.meta.message;
  }
  return fallback;
}

export const useAuth = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loginRequest = async (userName, password) => {
    setLoading(true);
    setError('');
    try {
      const res = await AuthAPI.login(userName, password);

      const accessToken = res.data.data?.accessToken;
      const refreshToken = res.data.data?.refreshToken;

      if (accessToken && refreshToken) {
        toast.success("¡Login exitoso!");
        return { success: true, accessToken, refreshToken };
      } else {
        const apiMessage = getApiErrorMessage(res.data, "Login fallido");
        setError(apiMessage);
        toast.error(apiMessage);
        return { success: false, error: apiMessage };
      }
    } catch (err) {
      const apiMessage = getApiErrorMessage(err.response?.data);
      setError(apiMessage);
      toast.error(apiMessage);
      return { success: false, error: apiMessage };
    } finally {
      setLoading(false);
    }
  };

  return { loginRequest, loading, error, setError };
};
