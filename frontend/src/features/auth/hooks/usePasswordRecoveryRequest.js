import { useState } from "react";
import { AuthAPI } from "../api/auth.api";
import toast from "react-hot-toast";

export function usePasswordRecoveryRequest() {
  const [loading, setLoading] = useState(false);

  const requestRecovery = async ({ userName, email }) => {
    setLoading(true);
    try {
      // Solo envía el campo que tenga valor
      let payload = {};
      if (userName) payload.userName = userName;
      if (email) payload.email = email;

      await AuthAPI.passwordRecoveryRequest(payload);
      toast.success("Si el usuario existe, recibirás instrucciones para recuperar tu contraseña.");
      return true;
    } catch (err) {
      const apiErrors = err.response?.data?.errors;
      let apiMessage = "Ocurrió un error inesperado";
      if (Array.isArray(apiErrors) && apiErrors.length > 0 && apiErrors[0].message) {
        apiMessage = apiErrors[0].message;
      } else if (err.response?.data?.meta?.message) {
        apiMessage = err.response.data.meta.message;
      }
      toast.error(apiMessage);
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { requestRecovery, loading };
}