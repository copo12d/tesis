import { useState } from "react";
import { UsersAPI } from "../api/user.api";
import { useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

export function useCreateUser({
  redirectOnSuccess = "/",   // ruta a redirigir tras crear
  emitToasts = true,         // permitir desactivar toasts si se necesita
} = {}) {
  const [loading, setLoading] = useState(false);
  const [apiMessage, setApiMessage] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const create = async (data) => {
    setLoading(true);
    setError(null);
    setApiMessage(null);
    try {
      const res = await UsersAPI.create(data);

      // Si hay errores en la respuesta, no redirige ni muestra éxito
      const apiErrors = res?.data?.errors;
      if (Array.isArray(apiErrors) && apiErrors.length > 0) {
        let msg = apiErrors[0].message || "Error al crear usuario";
        setError(msg);
        if (emitToasts) {
          toast.error(msg, { duration: 4000 });
        }
        return { success: false, message: msg };
      }

      const message =
        res?.data?.meta?.message ||
        "Usuario creado correctamente";

      setApiMessage(message);

      if (emitToasts) {
        toast.success(message, { duration: 4000 });
      }

      if (redirectOnSuccess) {
        navigate(redirectOnSuccess);
      }

      return { success: true, data: res.data };
    } catch (err) {
      // Extrae el mensaje de error de validación si existe
      const apiErrors = err?.response?.data?.errors;
      let msg = "Error al crear usuario";
      if (Array.isArray(apiErrors) && apiErrors.length > 0 && apiErrors[0].message) {
        msg = apiErrors[0].message;
      } else if (err?.response?.data?.meta?.message) {
        msg = err.response.data.meta.message;
      } else if (err?.response?.data?.message) {
        msg = err.response.data.message;
      } else if (err.message) {
        msg = err.message;
      }

      setError(msg);

      if (emitToasts) {
        toast.error(msg, { duration: 4000 });
      }

      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  };

  return { create, loading, apiMessage, error };
}