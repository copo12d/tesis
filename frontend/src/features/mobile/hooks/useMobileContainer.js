import { useEffect, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import { MobileAPI } from "../api/api.mobile";
import { toast } from "react-hot-toast";

export function useMobileContainer() {
  const { id } = useParams();
  const [container, setContainer] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchContainer = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const res = await MobileAPI.getContainer(id);
      setContainer(res?.data?.data ?? null);
    } catch (err) {
      const apiMsg =
        err?.response?.data?.meta?.message ||
        (err?.response?.data?.errors?.[0]?.message) ||
        "No se pudo obtener la información del contenedor";
      setError(apiMsg);
      toast.error(apiMsg);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchContainer();
  }, [fetchContainer]);

  return { container, loading, error, refetch: fetchContainer };
}