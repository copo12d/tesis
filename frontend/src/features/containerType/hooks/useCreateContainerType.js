import { useState } from "react";
import { ContainerTypeAPI } from "../api/containerType.api";
import toast from "react-hot-toast";

export function useCreateContainerType() {
  const [loading, setLoading] = useState(false);

  const createContainerType = async (payload) => {
    setLoading(true);
    try {
      const res = await ContainerTypeAPI.register(payload);
      toast.success(
        res?.data?.meta?.message || "Tipo de contenedor creado correctamente."
      );
      return true;
    } catch (e) {
      toast.error(
        e?.response?.data?.meta?.message ||
          e?.response?.data?.message ||
          e.message ||
          "Error creando tipo de contenedor"
      );
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { createContainerType, loading };
}