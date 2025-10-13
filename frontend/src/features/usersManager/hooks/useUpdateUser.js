import { useEffect, useState, useCallback } from "react";
import { UsersAPI } from "../api/user.api";
import { toast } from "react-hot-toast";
import { useNavigate } from "react-router-dom";

export function useUpdateUser(id, { redirectOnSuccess = null, emitToasts = true } = {}) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState(null);
  const [apiMessage, setApiMessage] = useState(null);
  const navigate = useNavigate();

  // 1 y 2. Traer la información del usuario
  const fetchUser = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const res = await UsersAPI.get(id);
      setUser(res?.data?.data || null);
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  // 3. Actualizar usuario
  const updateUser = async (payload) => {
    setUpdating(true);
    setError(null);
    setApiMessage(null);
    try {
      const res = await UsersAPI.update(id, payload);

      // Manejo de errores de validación aunque el status sea 200/201
      const apiErrors = res?.data?.errors;
      if (Array.isArray(apiErrors) && apiErrors.length > 0) {
        let msg = apiErrors[0].message || "Error al actualizar usuario";
        setError(msg);
        if (emitToasts) toast.error(msg);
        return { success: false, message: msg };
      }

      const message =
        res?.data?.meta?.message ||
        "Usuario actualizado correctamente";
      setApiMessage(message);

      if (emitToasts) toast.success(message);

      await fetchUser(); // refresca datos tras actualizar

      if (redirectOnSuccess) {
        navigate(redirectOnSuccess);
      }

      return { success: true, message };
    } catch (e) {
      // Extrae mensaje de error de la API si existe
      const apiErrors = e?.response?.data?.errors;
      let msg = "Error al actualizar usuario";
      if (Array.isArray(apiErrors) && apiErrors.length > 0 && apiErrors[0].message) {
        msg = apiErrors[0].message;
      } else if (e?.response?.data?.meta?.message) {
        msg = e.response.data.meta.message;
      } else if (e?.response?.data?.message) {
        msg = e.response.data.message;
      } else if (e.message) {
        msg = e.message;
      }
      setError(msg);
      if (emitToasts) toast.error(msg);
      return { success: false, message: msg };
    } finally {
      setUpdating(false);
    }
  };

  return {
    user,
    loading,
    updating,
    error,
    apiMessage,
    fetchUser,
    updateUser,
  };
}