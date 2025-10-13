import { useAuth } from "@/features/auth/hooks/useAuth";
import { useNavigate } from "react-router-dom";

export function useMobileCollectorLogin() {
  const { loginRequest, loading, error, setError } = useAuth();
  const navigate = useNavigate();

  // Puedes guardar la ruta previa para redirect después de login
  const redirectTo = "/mobile/containers/collect"; // Ahora es ruta absoluta

  const handleLogin = async (userName, password) => {
    const result = await loginRequest(userName, password);
    if (result.success) {
      navigate(redirectTo, { replace: true });
    } else if (
      result?.errorCode === "ACCOUNT_LOCKED" ||
      result?.message?.toLowerCase().includes("bloquead")
    ) {
      navigate("/account-locked", { replace: true });
    }
    // Si falla por otro motivo, el toast y error ya lo maneja useAuth
    return result;
  };

  return {
    handleLogin,
    loading,
    error,
    setError,
  };
}