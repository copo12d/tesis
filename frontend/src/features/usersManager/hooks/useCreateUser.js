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
      const msg =
        err?.response?.data?.message ||
        err.message ||
        "Error al crear usuario";

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