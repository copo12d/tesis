import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { MobileAPI } from "../api/api.mobile";
import { toast } from "react-hot-toast";

export function useMobileContainer() {
  const { id } = useParams();
  const [container, setContainer] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!id || id === "0") {
      setContainer(null);
      setError("ID de contenedor inválido");
      return;
    }
    setLoading(true);
    setError("");
    MobileAPI.getContainer(id)
      .then((res) => {
        const data = res?.data?.data ?? null;
        setContainer(data);
        // Guarda el id directamente aquí
        if (data?.id) {
          localStorage.setItem("containerId", data.id);
        }
      })
      .catch((err) => {
        const apiMsg =
          err?.response?.data?.meta?.message ||
          (err?.response?.data?.errors?.[0]?.message) ||
          "No se pudo obtener la información del contenedor";
        setError(apiMsg);
        toast.error(apiMsg);
      })
      .finally(() => setLoading(false));
  }, [id]);

  return { container, loading, error };
}