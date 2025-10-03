import { useState, useCallback } from "react";
import { UsersAPI } from "../api/user.api";
import { toast } from "react-hot-toast";

export function useDeleteUser({ emitToasts = true } = {}) {
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState(null);

  const remove = useCallback(
    async (id) => {
      if (!id || deletingId) return false;
      setDeletingId(id);
      setError(null);
      try {
        await UsersAPI.delete(id);
        if (emitToasts) toast.success("Usuario eliminado");
        return true;
      } catch (e) {
        const msg =
          e?.response?.data?.meta?.message ||
          e?.response?.data?.message ||
          "Error eliminando usuario";
        setError(msg);
        if (emitToasts) toast.error(msg);
        return false;
      } finally {
        setDeletingId((prev) => (prev === id ? null : prev));
      }
    },
    [deletingId, emitToasts]
  );

  return { remove, deletingId, error };
}