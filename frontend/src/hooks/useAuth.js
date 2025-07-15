import { useState } from "react";
import axios from "axios";
import { toast } from "react-hot-toast";

export const useAuth = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const login = async (userName, password) => {
    setLoading(true);
    setError('');
    try {
      const res = await axios.post("http://localhost:8080/auth/login", {
        userName,
        password,
      });

      if (res.data.token) {
        toast.success("¡Login exitoso!");
        return { success: true, token: res.data.token };
      } else {
        setError("Login fallido");
        toast.error("Error al iniciar sesión");
      }

      return { success: false };
    } catch (err) {
      setError(err.response?.data?.error || "Error al iniciar sesión");
      toast.error(err.response?.data?.error || "Error al iniciar sesión");
      return { success: false, error: err.response?.data?.error };
    } finally {
      setLoading(false);
    }
  };

  return { login, loading, error };
};
