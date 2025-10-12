import { useState } from "react";
import { BatchAPI } from "../api/api.batch";
import { useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

export function useRegisterBatch({
  redirectOnSuccess = "/batch/list",
  emitToasts = true,
} = {}) {
  const [loading, setLoading] = useState(false);
  const [apiMessage, setApiMessage] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const registerBatch = async (data) => {
    setLoading(true);
    setError(null);
    setApiMessage(null);
    try {
      const res = await BatchAPI.register(data);
      const message =
        res?.data?.meta?.message ||
        "Lote registrado correctamente";

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
        err?.response?.data?.meta?.message ||
        err?.response?.data?.message ||
        err.message ||
        "No se pudo registrar el lote";

      setError(msg);

      if (emitToasts) {
        toast.error(msg, { duration: 4000 });
      }

      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  };

  return { registerBatch, loading, apiMessage, error };
}