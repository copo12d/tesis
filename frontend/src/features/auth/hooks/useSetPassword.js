import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-hot-toast";

export function useSetPassword(
  apiCall,
  { successRedirect = "/login", successMessage = "Contraseña establecida correctamente." } = {}
) {
  const { id, token } = useParams();
  const navigate = useNavigate();

  const [password, setPassword] = useState("");
  const [repeat, setRepeat] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    if (e?.preventDefault) e.preventDefault();

    if (password !== repeat) {
      toast.error("Las contraseñas no coinciden.");
      return false;
    }

    setLoading(true);
    try {
      await apiCall(id, token, { password });
      toast.success(`${successMessage} Ahora puedes iniciar sesión.`);
      navigate(successRedirect, { replace: true });
      return true;
    } catch (err) {
      // Tomar el mensaje directo del backend
      const msg =
        err?.response?.data?.errors?.[0]?.message ||
        err?.response?.data?.meta?.message ||
        err?.response?.data?.message ||
        "No se pudo establecer la contraseña.";
      toast.error(msg);
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    password,
    setPassword,
    repeat,
    setRepeat,
    loading,
    handleSubmit,
  };
}