import { useState } from "react";
import { BatchAPI } from "../api/api.batch";
import { toast } from "react-hot-toast";

export function useDeleteBatch() {
  const [deletingId, setDeletingId] = useState(null);

  const remove = async (id) => {
    setDeletingId(id);
    try {
      await BatchAPI.softDelete(id);
      toast.success("Lote eliminado correctamente");
      return true;
    } catch (err) {
      const apiMsg =
        err?.response?.data?.meta?.message ||
        (err?.response?.data?.errors?.[0]?.message) ||
        "No se pudo eliminar el lote";
      toast.error(apiMsg);
      return false;
    } finally {
      setDeletingId(null);
    }
  };

  return { remove, deletingId };
}