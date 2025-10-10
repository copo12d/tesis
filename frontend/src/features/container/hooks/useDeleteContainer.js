import { useState } from "react";
import { ContainerAPI } from "../api/container.api";
import { toast } from "react-hot-toast";

export function useDeleteContainer({ emitToasts = true } = {}) {
  const [deletingId, setDeletingId] = useState(null);

  const remove = async (id) => {
    setDeletingId(id);
    try {
      await ContainerAPI.delete(id);
      if (emitToasts) toast.success("Contenedor eliminado correctamente");
      return true;
    } catch (e) {
      if (emitToasts) {
        toast.error(
          e?.response?.data?.meta?.message ||
          e?.response?.data?.message ||
          e.message ||
          "Error al eliminar contenedor"
        );
      }
      return false;
    } finally {
      setDeletingId(null);
    }
  };

  return { remove, deletingId };
}