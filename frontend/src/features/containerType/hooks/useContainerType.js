import { useEffect, useState, useCallback } from "react";
import { ContainerTypeAPI } from "../api/containerType.api";
import toast from "react-hot-toast";

/**
 * Hook para registrar y obtener un tipo de contenedor.
 */
export function useContainerType(id) {
  const [containerType, setContainerType] = useState(null);
  const [loading, setLoading] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState(null);

  // Traer la información del tipo de contenedor
  const fetchContainerType = useCallback(async () => {
    console.log("ID recibido en hook useContainerType:", id); // <-- LOG 1
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const res = await ContainerTypeAPI.getById(id);
      console.log("Respuesta de getById:", res); // <-- LOG 2
      setContainerType(res?.data?.data || null);
    } catch (e) {
      setError(e);
      toast.error(
        e?.response?.data?.meta?.message ||
          e?.response?.data?.message ||
          e.message ||
          "Error obteniendo tipo de contenedor"
      );
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchContainerType();
  }, [fetchContainerType]);

  // Actualizar tipo de contenedor
  const updateContainerType = async (payload) => {
    setUpdating(true);
    setError(null);
    try {
      const res = await ContainerTypeAPI.update(id, payload);
      toast.success(
        res?.data?.meta?.message || "Tipo de contenedor actualizado correctamente."
      );
      return true;
    } catch (e) {
      setError(e);
      toast.error(
        e?.response?.data?.meta?.message ||
          e?.response?.data?.message ||
          e.message ||
          "Error actualizando tipo de contenedor"
      );
      return false;
    } finally {
      setUpdating(false);
    }
  };

  return {
    containerType,
    loading,
    updating,
    error,
    fetchContainerType,
    updateContainerType,
  };
}