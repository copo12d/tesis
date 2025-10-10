import { useState } from "react";
import { ContainerAPI } from "../api/container.api";
import { toast } from "react-hot-toast";

export function useCreateContainer({ emitToasts = true } = {}) {
  const [loading, setLoading] = useState(false);

  const create = async (values) => {
    setLoading(true);
    try {
      await ContainerAPI.create(values);
      if (emitToasts) toast.success("Contenedor creado correctamente");
      return true;
    } catch (e) {
      if (emitToasts) {
        toast.error(
          e?.response?.data?.meta?.message ||
          e?.response?.data?.message ||
          e.message ||
          "Error al crear contenedor"
        );
      }
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { create, loading };
}