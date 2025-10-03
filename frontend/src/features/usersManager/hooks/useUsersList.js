import { useCallback, useEffect, useState } from "react";
import { UsersAPI } from "../api/user.api";
import { toast } from "react-hot-toast";

/**
 * Hook para obtener usuarios paginados.
 * Opcional: manejará internamente page (1-based en UI) y pageSize.
 */
export function useUsersList({
  initialPage = 1,
  pageSize = 10,
  sortBy = "id",
  sortDir = "DESC",
  autoFetch = true,
  emitToasts = true,
} = {}) {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(initialPage); // UI 1-based
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [totalPages, setTotalPages] = useState(0);
  const fetchUsers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page: page - 1, // backend 0-based
        size: pageSize,
        sortBy,
        sortDir,
      };
      const res = await UsersAPI.list(params);
      const content = res?.data?.data?.content || [];
      const totalElements = res?.data?.data?.totalElements ?? content.length;
      const totalPages = res?.data?.data?.totalPages;
      setItems(content);
      setTotal(totalElements);
      setTotalPages(totalPages);
    } catch (e) {
      const msg =
        e?.response?.data?.meta?.message ||
        e?.response?.data?.message ||
        e.message ||
        "Error cargando usuarios";
      if (emitToasts) toast.error(msg);
      setError(msg);
      setItems([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, sortBy, sortDir, emitToasts]);

  useEffect(() => {
    if (autoFetch) fetchUsers();
  }, [fetchUsers, autoFetch]);

  return {
    items,
    total,
    page,
    pageSize,
    loading,
    error,
    totalPages,
    setPage,
    refetch: fetchUsers,
  };
}