import { useState } from "react";
import { toast } from "react-hot-toast";
import { AuthAPI } from '../api/auth.api'

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
        setError("Login fallido");
        toast.error("Error al iniciar sesión");
        return { success: false };
      }
    } catch (err) {
      setError(err.response?.data?.error || "Error al iniciar sesión");
      toast.error(err.response?.data?.error || "Error al iniciar sesión");
      return { success: false, error: err.response?.data?.error };
    } finally {
      setLoading(false);
    }
  };

  return { loginRequest, loading, error, setError };
};
