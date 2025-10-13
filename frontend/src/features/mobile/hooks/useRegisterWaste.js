import { useState } from "react";
import { MobileAPI } from "../api/api.mobile";
import { toast } from "react-hot-toast";

export function useRegisterWaste() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  /**
   * Registra un waste.
   * @param {Object} params - { weight, batchId, containerId }
   * containerId debe ser pasado por el componente, no por el usuario.
   */
  const registerWaste = async ({ weight, batchId, containerId }) => {
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      const res = await MobileAPI.registerWaste({ weight, batchId, containerId });
      const msg =
        res?.data?.meta?.message || "Desecho registrado correctamente";
      setSuccess(msg);
      toast.success(msg);
      return true;
    } catch (err) {
      const apiMsg =
        err?.response?.data?.meta?.message ||
        (err?.response?.data?.errors?.[0]?.message) ||
        "No se pudo registrar el desecho";
      setError(apiMsg);
      toast.error(apiMsg);
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { registerWaste, loading, error, success };
}