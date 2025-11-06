import { useState } from "react";
import { AuthAPI } from "../api/auth.api";
import { toast } from "react-hot-toast";

export function useAccountRecoveryRequest() {
  const [loading, setLoading] = useState(false);

  const requestAccountRecovery = async (payload) => {
    setLoading(true);
    try {
      const res = await AuthAPI.accountRecoveryRequest(payload);
      const msg =
        res?.data?.meta?.message ||
        res?.data?.message ||
        "Solicitud de recuperación enviada. Revisa tu correo.";
      toast.success(msg);
      return true;
    } catch (e) {
      const msg =
        e?.response?.data?.meta?.message ||
        e?.response?.data?.message ||
        "No se pudo enviar la solicitud de recuperación.";
      toast.error(msg);
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { requestAccountRecovery, loading };
}