import { useState, useEffect, useCallback } from "react";
import { BatchAPI } from "../api/api.batch";

export function useBatchList({
  initialPage = 1,
  pageSize = 10,
  sortBy = "creationDate",
  sortDir = "desc",
  autoFetch = true,
  debounceMs = 0,
  initialSearchTerm = "",
  initialSearchType = "",
} = {}) {
  const [items, setItems] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(initialPage);
  const [totalPages, setTotalPages] = useState(1);
  const [searchTerm, setSearchTerm] = useState(initialSearchTerm);
  const [searchType, setSearchType] = useState(initialSearchType);
  const [error, setError] = useState("");
  const [sort, setSort] = useState({ by: sortBy, dir: sortDir });

  // Fetch function
  const fetchData = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await BatchAPI.getAll({
        page: page - 1, // API espera page base 0
        size: pageSize,
        sortBy: sort.by,
        sortDir: sort.dir,
        // Solo agrega búsqueda si tu backend lo soporta:
        ...(searchTerm && { searchTerm }),
        ...(searchType && { searchType }),
      });
      const data = res.data?.data;
      setItems(Array.isArray(data?.content) ? data.content : []);
      setTotal(data?.totalElements ?? 0);
      setTotalPages(data?.totalPages ?? 1);
    } catch {
      setError("No se pudieron cargar los lotes.");
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, sort.by, sort.dir, searchTerm, searchType]);

  // Debounce para búsqueda si se requiere
  useEffect(() => {
    if (!autoFetch) return;
    let timeout;
    if (debounceMs > 0) {
      timeout = setTimeout(fetchData, debounceMs);
    } else {
      fetchData();
    }
    return () => timeout && clearTimeout(timeout);
  }, [fetchData, autoFetch, debounceMs]);

  // Refetch manual
  const refetch = () => fetchData();

  return {
    items,
    total,
    loading,
    page,
    setPage,
    totalPages,
    searchTerm,
    setSearchTerm,
    searchType,
    setSearchType,
    sort,
    setSort,
    error,
    refetch,
  };
}