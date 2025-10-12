import { useState } from "react";
import { ContainerTypeAPI } from "../api/containerType.api";
import toast from "react-hot-toast";

/**
 * Hook para eliminar un tipo de contenedor.
 * @param {Object} options - Opciones, por ejemplo onSuccess para refetch.
 */
export function useDeleteContainerType({ onSuccess } = {}) {
  const [deletingId, setDeletingId] = useState(null);

  const remove = async (id) => {
    setDeletingId(id);
    try {
      const res = await ContainerTypeAPI.delete(id);
      const apiMessage =
        res?.data?.meta?.message || "Tipo de contenedor eliminado correctamente.";
      toast.success(apiMessage);
      if (typeof onSuccess === "function") onSuccess();
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
      setDeletingId(null);
    }
  };

  return { remove, deletingId };
}